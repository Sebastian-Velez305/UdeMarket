package com.example.udemarket.features.chat.presentation.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udemarket.core.ResultState
import com.example.udemarket.data.repository.AuthRepository
import com.example.udemarket.data.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatInboxViewModel(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatInboxUiState())
    val uiState: StateFlow<ChatInboxUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    init {
        loadConversations()
    }

    fun loadConversations() {
        val currentUserId = authRepository.getCurrentUserUid() ?: return
        
        // Cancelamos el trabajo anterior si existe para evitar duplicados
        fetchJob?.cancel()
        
        fetchJob = viewModelScope.launch {
            chatRepository.getConversations(currentUserId).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is ResultState.Success -> {
                        _uiState.update { it.copy(
                            conversations = result.data,
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
}
