package com.example.udemarket.features.chat.presentation.detail

import com.example.udemarket.data.model.Message

data class ChatDetailUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val newMessageText: String = ""
)
