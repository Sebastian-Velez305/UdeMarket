package com.example.udemarket.data.repository

import com.example.udemarket.core.ResultState
import com.example.udemarket.data.model.Profile
import com.example.udemarket.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : AuthRepository {

    override fun signUp(email: String, password: String, user: User, phoneNumber: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        
        if (!email.endsWith("@misena.edu.co")) {
            trySend(ResultState.Error("Acceso restringido únicamente para aprendices SENA con correo @misena.edu.co"))
            close()
            return@callbackFlow
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val newUser = user.copy(uid = uid, email = email)
                val newProfile = Profile(userId = uid, phoneNumber = phoneNumber)
                
                val batch = db.batch()
                val userRef = db.collection("users").document(uid)
                val profileRef = db.collection("profiles").document(uid)
                
                batch.set(userRef, newUser)
                batch.set(profileRef, newProfile)
                
                batch.commit()
                    .addOnSuccessListener {
                        trySend(ResultState.Success(uid))
                        close()
                    }
                    .addOnFailureListener { e ->
                        trySend(ResultState.Error(e.localizedMessage ?: "Error al guardar perfil"))
                        close()
                    }
            }
            .addOnFailureListener { e ->
                trySend(ResultState.Error(e.localizedMessage ?: "Error en registro"))
                close()
            }
        
        awaitClose()
    }

    override fun signIn(email: String, password: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        
        if (!email.endsWith("@misena.edu.co")) {
            trySend(ResultState.Error("Acceso restringido únicamente para aprendices SENA con correo @misena.edu.co"))
            close()
            return@callbackFlow
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                trySend(ResultState.Success(result.user?.uid ?: ""))
                close()
            }
            .addOnFailureListener { e ->
                trySend(ResultState.Error(e.localizedMessage ?: "Error al iniciar sesión"))
                close()
            }
        awaitClose()
    }

    override fun sendEmailVerification(): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        auth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                trySend(ResultState.Success(Unit))
                close()
            }
            ?.addOnFailureListener { e ->
                trySend(ResultState.Error(e.localizedMessage ?: "Error al enviar verificación"))
                close()
            }
        awaitClose()
    }

    override fun getCurrentUserUid(): String? = auth.currentUser?.uid

    override fun getUserData(uid: String): Flow<ResultState<User>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    trySend(ResultState.Success(user))
                } else {
                    trySend(ResultState.Error("Usuario no encontrado"))
                }
                close()
            }
            .addOnFailureListener { e ->
                trySend(ResultState.Error(e.localizedMessage ?: "Error al obtener datos del usuario"))
                close()
            }
        awaitClose()
    }

    override fun updateUserData(user: User): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection("users").document(user.uid).set(user)
            .addOnSuccessListener {
                trySend(ResultState.Success(Unit))
                close()
            }
            .addOnFailureListener { e ->
                trySend(ResultState.Error(e.localizedMessage ?: "Error al actualizar datos"))
                close()
            }
        awaitClose()
    }

    override fun getProfileData(uid: String): Flow<ResultState<Profile>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection("profiles").document(uid).get()
            .addOnSuccessListener { document ->
                val profile = document.toObject(Profile::class.java)
                if (profile != null) {
                    trySend(ResultState.Success(profile))
                } else {
                    val newProfile = Profile(userId = uid)
                    db.collection("profiles").document(uid).set(newProfile)
                    trySend(ResultState.Success(newProfile))
                }
                close()
            }
            .addOnFailureListener { e ->
                trySend(ResultState.Error(e.localizedMessage ?: "Error al obtener perfil"))
                close()
            }
        awaitClose()
    }

    override fun updateProfileData(profile: Profile): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection("profiles").document(profile.userId).set(profile)
            .addOnSuccessListener {
                trySend(ResultState.Success(Unit))
                close()
            }
            .addOnFailureListener { e ->
                trySend(ResultState.Error(e.localizedMessage ?: "Error al actualizar perfil"))
                close()
            }
        awaitClose()
    }

    override fun signOut() {
        auth.signOut()
    }
}
