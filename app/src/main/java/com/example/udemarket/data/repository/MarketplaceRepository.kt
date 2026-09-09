package com.example.udemarket.data.repository

import com.example.udemarket.core.ResultState
import com.example.udemarket.data.model.MarketplaceItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MarketplaceRepository(private val db: FirebaseFirestore) {

    fun getMarketplaceItems(): Flow<ResultState<List<MarketplaceItem>>> = callbackFlow {
        trySend(ResultState.Loading)
        val subscription = db.collection("marketplace")
            .orderBy("fechaPublicacion")
            .addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(ResultState.Error(error.localizedMessage ?: "Error desconocido"))
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { it.toObject(MarketplaceItem::class.java) } ?: emptyList()
            trySend(ResultState.Success(items))
        }
        awaitClose { subscription.remove() }
    }

    fun getMarketplaceItem(itemId: String): Flow<ResultState<MarketplaceItem>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection("marketplace").document(itemId).get()
            .addOnSuccessListener { document ->
                val item = document.toObject(MarketplaceItem::class.java)
                if (item != null) trySend(ResultState.Success(item))
                else trySend(ResultState.Error("Producto no encontrado"))
                close()
            }
            .addOnFailureListener { e ->
                trySend(ResultState.Error(e.localizedMessage ?: "Error"))
                close()
            }
        awaitClose()
    }

    fun saveMarketplaceItem(item: MarketplaceItem): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        val docRef = if (item.itemId.isEmpty()) db.collection("marketplace").document() 
                     else db.collection("marketplace").document(item.itemId)
        
        val itemToSave = if (item.itemId.isEmpty()) item.copy(itemId = docRef.id) else item
        
        docRef.set(itemToSave)
            .addOnSuccessListener { trySend(ResultState.Success(Unit)); close() }
            .addOnFailureListener { e -> trySend(ResultState.Error(e.localizedMessage ?: "Error")); close() }
        awaitClose()
    }

    fun deleteMarketplaceItem(itemId: String): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection("marketplace").document(itemId).delete()
            .addOnSuccessListener { trySend(ResultState.Success(Unit)); close() }
            .addOnFailureListener { e -> trySend(ResultState.Error(e.localizedMessage ?: "Error")); close() }
        awaitClose()
    }
}
