package com.example.udemarket.data.repository

import com.example.udemarket.core.ResultState
import com.example.udemarket.data.model.Profile
import com.example.udemarket.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signUp(email: String, password: String, user: User, phoneNumber: String): Flow<ResultState<String>>
    fun signIn(email: String, password: String): Flow<ResultState<String>>
    fun sendEmailVerification(): Flow<ResultState<Unit>>
    fun getCurrentUserUid(): String?
    fun getUserData(uid: String): Flow<ResultState<User>>
    fun updateUserData(user: User): Flow<ResultState<Unit>>
    fun getProfileData(uid: String): Flow<ResultState<Profile>>
    fun updateProfileData(profile: Profile): Flow<ResultState<Unit>>
    fun signOut()
}
