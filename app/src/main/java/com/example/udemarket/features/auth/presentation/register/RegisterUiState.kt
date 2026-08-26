package com.example.udemarket.features.auth.presentation.register

data class RegisterUiState(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val isEmailError: Boolean = false,
    val emailErrorMessage: String? = null,
    val career: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isRegisterEnabled: Boolean = false
)
