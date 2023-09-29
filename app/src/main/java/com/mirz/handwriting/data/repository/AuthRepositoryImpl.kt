package com.mirz.handwriting.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.UserEntity
import com.mirz.handwriting.domain.repository.AuthRepository
import com.mirz.handwriting.domain.repository.SignInWithGoogleResponse
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override val isUserAuthenticatedInFirebase = auth.currentUser != null


    override suspend fun firebaseSignIn(email: String, password: String): SignInWithGoogleResponse {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = firestore.collection("user").document(result.user?.uid.toString()).get()
                .await().toObject(UserEntity::class.java)

            if (user?.role == "mentee") {
                Response.Success(user)
            } else {
                auth.signOut()
                Response.Failure(Exception("Anda tidak memiliki akses"))
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

}