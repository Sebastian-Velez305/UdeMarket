package com.example.udemarket.data.repository

import com.example.udemarket.core.ResultState
import com.example.udemarket.data.model.Profile
import com.example.udemarket.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : AuthRepository {

    override fun signUp(email: String, password: String, user: User, phoneNumber: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        
        val cleanEmail = email.trim().lowercase()
        
        if (!cleanEmail.endsWith("@misena.edu.co")) {
            trySend(ResultState.Error("Usa un correo @misena.edu.co válido"))
            close()
            return@callbackFlow
        }

        auth.createUserWithEmailAndPassword(cleanEmail, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val newUser = user.copy(uid = uid, email = cleanEmail)
                val newProfile = Profile(userId = uid, phoneNumber = phoneNumber)
                
                val batch = db.batch()
                batch.set(db.collection("users").document(uid), newUser)
                batch.set(db.collection("profiles").document(uid), newProfile)
                
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
        
        val cleanEmail = email.trim().lowercase()

        auth.signInWithEmailAndPassword(cleanEmail, password)
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
                if (user != null) trySend(ResultState.Success(user))
                else trySend(ResultState.Error("Usuario no encontrado"))
                close()
            }
            .addOnFailureListener { e ->
                trySend(ResultState.Error(e.localizedMessage ?: "Error al obtener datos"))
                close()
            }
        awaitClose()
    }

    override fun updateUserData(user: User): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection("users").document(user.uid).set(user)
            .addOnSuccessListener { trySend(ResultState.Success(Unit)); close() }
            .addOnFailureListener { e -> trySend(ResultState.Error(e.localizedMessage ?: "Error")); close() }
        awaitClose()
    }

    override fun getProfileData(uid: String): Flow<ResultState<Profile>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection("profiles").document(uid).get()
            .addOnSuccessListener { document ->
                val profile = document.toObject(Profile::class.java) ?: Profile(userId = uid)
                trySend(ResultState.Success(profile))
                close()
            }
            .addOnFailureListener { e -> trySend(ResultState.Error(e.localizedMessage ?: "Error")); close() }
        awaitClose()
    }

    override fun updateProfileData(profile: Profile): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection("profiles").document(profile.userId).set(profile)
            .addOnSuccessListener { trySend(ResultState.Success(Unit)); close() }
            .addOnFailureListener { e -> trySend(ResultState.Error(e.localizedMessage ?: "Error")); close() }
        awaitClose()
    }

    override fun signOut() {
        auth.signOut()
    }
}
