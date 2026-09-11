package com.example.udemarket.data.model

data class Conversation(
    val id: String = "",
    val participants: List<String> = emptyList(), // IDs de los usuarios
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val productId: String? = null, // Vínculo con el producto de Marketplace
    val productName: String? = null,
    val unreadCount: Map<String, Int> = emptyMap() // userId -> count
)
