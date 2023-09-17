package com.mirz.handwriting.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.repository.AuthRepository
import com.mirz.handwriting.domain.repository.SignInWithGoogleResponse
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val isUserAuthenticatedInFirebase = auth.currentUser != null


    override suspend fun firebaseSignIn(email: String, password: String): SignInWithGoogleResponse {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Response.Success(result.user != null)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

}