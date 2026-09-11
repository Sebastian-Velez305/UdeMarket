package com.example.udemarket.features.chat.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udemarket.core.ResultState
import com.example.udemarket.data.model.Message
import com.example.udemarket.data.repository.AuthRepository
import com.example.udemarket.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val conversationId: String,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    val currentUserId: String = authRepository.getCurrentUserUid() ?: ""

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            chatRepository.getMessages(conversationId).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is ResultState.Success -> {
                        _uiState.update { it.copy(
                            messages = result.data,
                            isLoading = false,
                            errorMessage = null
                        ) }
                    }
                    is ResultState.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        ) }
                    }
                }
            }
        }
    }

    fun onMessageTextChanged(text: String) {
        _uiState.update { it.copy(newMessageText = text) }
    }

    fun sendMessage() {
        val content = _uiState.value.newMessageText.trim()
        if (content.isEmpty()) return

        val message = Message(
            conversationId = conversationId,
            senderId = currentUserId,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            try {
                chatRepository.sendMessage(message)
                _uiState.update { it.copy(newMessageText = "") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al enviar mensaje") }
            }
        }
    }
}
