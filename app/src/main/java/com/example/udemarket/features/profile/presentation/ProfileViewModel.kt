package com.example.udemarket.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udemarket.core.ResultState
import com.example.udemarket.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    fun loadUserData() {
        val uid = repository.getCurrentUserUid()
        if (uid != null) {
            viewModelScope.launch {
                repository.getUserData(uid).collect { result ->
                    when (result) {
                        is ResultState.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is ResultState.Success -> _uiState.update { it.copy(isLoading = false, user = result.data) }
                        is ResultState.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                }
                
                repository.getProfileData(uid).collect { result ->
                    when (result) {
                        is ResultState.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is ResultState.Success -> _uiState.update { it.copy(isLoading = false, profile = result.data) }
                        is ResultState.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                }
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        repository.signOut()
        onSuccess()
    }
}
