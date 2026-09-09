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
        val trimmedEmail = email.trim()
        val isError = email.isNotEmpty() && !trimmedEmail.endsWith("@misena.edu.co")
        _uiState.update {
            it.copy(
                email = email,
                isEmailError = isError,
                emailErrorMessage = if (isError) "Usa tu correo @misena.edu.co" else null,
                isLoginEnabled = validateForm(email, it.password),
                errorMessage = null
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                isLoginEnabled = validateForm(it.email, password),
                errorMessage = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return email.trim().endsWith("@misena.edu.co") && password.length >= 6
    }

    fun login(onSuccess: () -> Unit) {
        val cleanEmail = _uiState.value.email.trim().lowercase()
        viewModelScope.launch {
            repository.signIn(cleanEmail, _uiState.value.password).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is ResultState.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
                    }
                    is ResultState.Error -> {
                        val friendlyMessage = when {
                            result.message.contains("INVALID_LOGIN_CREDENTIALS") || 
                            result.message.contains("invalid-credential") ||
                            result.message.contains("user-not-found") -> "Correo o contraseña incorrectos"
                            result.message.contains("network-request-failed") -> "Sin conexión a internet"
                            else -> "Error de acceso. Intenta de nuevo."
                        }
                        _uiState.update { it.copy(isLoading = false, errorMessage = friendlyMessage) }
                    }
                }
            }
        }
    }
}
