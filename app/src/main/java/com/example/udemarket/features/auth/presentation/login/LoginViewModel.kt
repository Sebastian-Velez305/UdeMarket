package com.example.udemarket.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        val isError = email.isNotEmpty() && !email.endsWith("@udea.edu.co")
        _uiState.update {
            it.copy(
                email = email,
                isEmailError = isError,
                emailErrorMessage = if (isError) "El correo debe ser @udea.edu.co" else null,
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
        return email.endsWith("@udea.edu.co") && password.length >= 6
    }

    fun login() {
        // Implementar lógica de login aquí
        _uiState.update { it.copy(isLoading = true) }
    }
}