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

    suspend fun sendMessage(message: Message) {
        // Validación de seguridad: no permitir mensajes si el remitente no está en la conversación
        // (Esto se reforzará con reglas de Firestore)
        val messageRef = db.collection("conversations")
            .document(message.conversationId)
            .collection("messages")
            .document()
        
        val finalMessage = message.copy(id = messageRef.id)
        val batch = db.batch()
        batch.set(messageRef, finalMessage)
        
        val conversationRef = db.collection("conversations").document(message.conversationId)
        batch.update(conversationRef, mapOf(
            "lastMessage" to message.content,
            "lastMessageTimestamp" to message.timestamp
        ))
        
        batch.commit().await()
    }

    suspend fun getOrCreateConversation(
        myId: String, 
        otherId: String, 
        productId: String, 
        productName: String
    ): String {
        // REGLA DE NEGOCIO CRÍTICA: No chatear consigo mismo
        if (myId == otherId) {
            throw Exception("No puedes iniciar una conversación sobre tu propio producto.")
        }

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

        // Crear nueva si no existe
        val newDoc = db.collection("conversations").document()
        val conversation = Conversation(
            id = newDoc.id,
            participants = listOf(myId, otherId),
            productId = productId,
            productName = productName,
            lastMessage = "Interés en el producto",
            lastMessageTimestamp = System.currentTimeMillis()
        )
        newDoc.set(conversation).await()
        return newDoc.id
    }
}
