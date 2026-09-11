package com.example.udemarket.data.model

data class Message(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "sent" // sent, delivered, read
)
