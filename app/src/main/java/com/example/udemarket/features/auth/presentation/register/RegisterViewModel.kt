package com.example.udemarket.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udemarket.core.ResultState
import com.example.udemarket.data.model.User
import com.example.udemarket.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, isRegisterEnabled = validateForm(name, it.phone, it.email, it.password, it.confirmPassword)) }
    }

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone, isRegisterEnabled = validateForm(it.name, phone, it.email, it.password, it.confirmPassword)) }
    }

    fun onEmailChanged(email: String) {
        val trimmedEmail = email.trim()
        val isError = email.isNotEmpty() && !trimmedEmail.endsWith("@misena.edu.co")
        _uiState.update {
            it.copy(
                email = email,
                isEmailError = isError,
                emailErrorMessage = if (isError) "Usa tu correo @misena.edu.co" else null,
                isRegisterEnabled = validateForm(it.name, it.phone, email, it.password, it.confirmPassword),
                errorMessage = null
            )
        }
    }

    fun onCareerChanged(career: String) {
        _uiState.update { it.copy(career = career) }
    }

    fun onPasswordChanged(password: String) {
        val isError = password.isNotEmpty() && password.length < 6
        _uiState.update {
            it.copy(
                password = password,
                isPasswordError = isError,
                passwordErrorMessage = if (isError) "Mínimo 6 caracteres" else null,
                isRegisterEnabled = validateForm(it.name, it.phone, it.email, password, it.confirmPassword),
                errorMessage = null
            )
        }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                confirmPassword = password,
                isRegisterEnabled = validateForm(it.name, it.phone, it.email, it.password, password),
                errorMessage = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun validateForm(name: String, phone: String, email: String, password: String, confirm: String): Boolean {
        return name.isNotBlank() && phone.trim().length >= 7 && email.trim().endsWith("@misena.edu.co") && password.length >= 6 && password == confirm
    }

    fun register(onSuccess: () -> Unit) {
        val cleanEmail = _uiState.value.email.trim().lowercase()
        val user = User(
            nombre = _uiState.value.name.trim(),
            email = cleanEmail,
            carrera = _uiState.value.career.trim(),
            reputacion = 5.0
        )
        
        viewModelScope.launch {
            repository.signUp(cleanEmail, _uiState.value.password, user, _uiState.value.phone.trim()).collect { result ->
                when (result) {
                    is ResultState.Loading -> _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    is ResultState.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
                    }
                    is ResultState.Error -> {
                        // AQUÍ: Mostramos el error real para saber qué pasa en tu Firebase
                        val errorReal = result.message
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error: $errorReal") }
                    }
                }
            }
        }
    }
}
