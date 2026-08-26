package com.example.udemarket.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.update { 
            it.copy(
                name = name,
                isRegisterEnabled = validateForm(name, it.phone, it.email, it.password)
            )
        }
    }

    fun onPhoneChanged(phone: String) {
        _uiState.update { 
            it.copy(
                phone = phone,
                isRegisterEnabled = validateForm(it.name, phone, it.email, it.password)
            )
        }
    }

    fun onEmailChanged(email: String) {
        val isError = email.isNotEmpty() && !email.endsWith("@udea.edu.co")
        _uiState.update {
            it.copy(
                email = email,
                isEmailError = isError,
                emailErrorMessage = if (isError) "El correo debe ser @udea.edu.co" else null,
                isRegisterEnabled = validateForm(it.name, it.phone, email, it.password)
            )
        }
    }

    fun onCareerChanged(career: String) {
        _uiState.update { it.copy(career = career) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                isRegisterEnabled = validateForm(it.name, it.phone, it.email, password)
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    private fun validateForm(name: String, phone: String, email: String, password: String): Boolean {
        return name.isNotBlank() && 
               phone.isNotBlank() && 
               email.endsWith("@udea.edu.co") && 
               password.length >= 6
    }

    fun register() {
        _uiState.update { it.copy(isLoading = true) }
        // Lógica de registro aquí
    }
}
