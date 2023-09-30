package com.mirz.handwriting.domain.repository

import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.UserEntity

typealias SignInWithGoogleResponse = Response<UserEntity>

interface AuthRepository {
    val isUserAuthenticatedInFirebase: Boolean
    suspend fun firebaseSignIn(email: String, password: String): SignInWithGoogleResponse
    suspend fun getUserData(): Response<UserEntity>
    suspend fun logout(): Response<Boolean>
}