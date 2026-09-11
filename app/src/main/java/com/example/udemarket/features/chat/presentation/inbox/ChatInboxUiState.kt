package com.example.udemarket.features.chat.presentation.inbox

import com.example.udemarket.data.model.Conversation

data class ChatInboxUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
