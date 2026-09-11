package com.example.udemarket.data.repository

import com.example.udemarket.core.ResultState
import com.example.udemarket.data.model.Conversation
import com.example.udemarket.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository(private val db: FirebaseFirestore) {

    /**
     * Obtiene la lista de conversaciones en las que participa el usuario actual.
     */
    fun getConversations(userId: String): Flow<ResultState<List<Conversation>>> = callbackFlow {
        trySend(ResultState.Loading)
        val subscription = db.collection("conversations")
            .whereArrayContains("participants", userId)
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResultState.Error(error.localizedMessage ?: "Error al cargar chats"))
                    return@addSnapshotListener
                }
                val conversations = snapshot?.documents?.mapNotNull { it.toObject(Conversation::class.java) } ?: emptyList()
                trySend(ResultState.Success(conversations))
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Obtiene los mensajes de una conversación específica en tiempo real.
     */
    fun getMessages(conversationId: String): Flow<ResultState<List<Message>>> = callbackFlow {
        trySend(ResultState.Loading)
        val subscription = db.collection("conversations").document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResultState.Error(error.localizedMessage ?: "Error al cargar mensajes"))
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) } ?: emptyList()
                trySend(ResultState.Success(messages))
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Envía un mensaje y actualiza la metadata de la conversación.
     */
    suspend fun sendMessage(message: Message) {
        val messageRef = db.collection("conversations")
            .document(message.conversationId)
            .collection("messages")
            .document()
        
        val finalMessage = message.copy(id = messageRef.id)
        
        val batch = db.batch()
        batch.set(messageRef, finalMessage)
        
        // Actualizar el último mensaje en la conversación principal
        val conversationRef = db.collection("conversations").document(message.conversationId)
        batch.update(conversationRef, mapOf(
            "lastMessage" to message.content,
            "lastMessageTimestamp" to message.timestamp
        ))
        
        batch.commit().await()
    }

    /**
     * Busca o crea una conversación entre dos usuarios por un producto específico.
     */
    suspend fun getOrCreateConversation(
        myId: String, 
        otherId: String, 
        productId: String, 
        productName: String
    ): String {
        // Buscar si ya existe
        val existing = db.collection("conversations")
            .whereEqualTo("productId", productId)
            .whereArrayContains("participants", myId)
            .get().await()
        
        val found = existing.documents.firstOrNull { doc ->
            val participants = doc.get("participants") as? List<*>
            participants?.contains(otherId) == true
        }

        if (found != null) return found.id

        // Si no existe, crearla
        val newDoc = db.collection("conversations").document()
        val conversation = Conversation(
            id = newDoc.id,
            participants = listOf(myId, otherId),
            productId = productId,
            productName = productName,
            lastMessage = "Iniciaste un chat por este producto",
            lastMessageTimestamp = System.currentTimeMillis()
        )
        newDoc.set(conversation).await()
        return newDoc.id
    }
}
