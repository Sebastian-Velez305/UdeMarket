package com.example.udemarket.features.profile.presentation

import com.example.udemarket.data.model.Profile
import com.example.udemarket.data.model.User

data class ProfileUiState(
    val user: User? = null,
    val profile: Profile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
