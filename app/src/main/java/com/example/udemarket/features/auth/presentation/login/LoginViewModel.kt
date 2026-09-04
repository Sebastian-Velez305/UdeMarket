package com.example.udemarket.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udemarket.core.ResultState
import com.example.udemarket.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        val isError = email.isNotEmpty() && !email.endsWith("@misena.edu.co")
        _uiState.update {
            it.copy(
                email = email,
                isEmailError = isError,
                emailErrorMessage = if (isError) "Acceso restringido únicamente para aprendices SENA con correo @misena.edu.co" else null,
                isLoginEnabled = validateForm(email, it.password)
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                isLoginEnabled = validateForm(it.email, password)
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return email.endsWith("@misena.edu.co") && password.length >= 6
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.signIn(_uiState.value.email, _uiState.value.password).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is ResultState.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
                    }
                    is ResultState.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        // Podrías agregar un campo de error general en UiState si deseas
                    }
                }
            }
        }
    }
}
