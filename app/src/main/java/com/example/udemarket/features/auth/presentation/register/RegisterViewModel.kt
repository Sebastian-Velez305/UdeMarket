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
        val isError = email.isNotEmpty() && !email.endsWith("@misena.edu.co")
        _uiState.update {
            it.copy(
                email = email,
                isEmailError = isError,
                emailErrorMessage = if (isError) "Acceso restringido únicamente para aprendices SENA con correo @misena.edu.co" else null,
                isRegisterEnabled = validateForm(it.name, it.phone, email, it.password)
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
                passwordErrorMessage = if (isError) "La contraseña debe tener al menos 6 caracteres" else null,
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
               email.endsWith("@misena.edu.co") && 
               password.length >= 6
    }

    fun register(onSuccess: () -> Unit) {
        val user = User(
            nombre = _uiState.value.name,
            email = _uiState.value.email,
            carrera = _uiState.value.career,
            reputacion = 5.0
        )
        
        viewModelScope.launch {
            repository.signUp(_uiState.value.email, _uiState.value.password, user).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is ResultState.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
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
