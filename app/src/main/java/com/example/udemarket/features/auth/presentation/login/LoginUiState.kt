package com.example.udemarket.features.auth.presentation.login

/**
 * Representa el estado de la pantalla de Login.
 */
data class LoginUiState(
    val email: String = "",
    val isEmailError: Boolean = false,
    val emailErrorMessage: String? = null,
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isLoginEnabled: Boolean = false
)