package com.mirz.handwriting.domain.repository

import com.mirz.handwriting.common.Response

typealias SignInWithGoogleResponse = Response<Boolean>

interface AuthRepository {
    val isUserAuthenticatedInFirebase: Boolean
    suspend fun firebaseSignIn(email: String, password: String): SignInWithGoogleResponse
}