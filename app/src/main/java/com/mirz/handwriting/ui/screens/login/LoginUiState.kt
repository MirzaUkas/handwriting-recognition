package com.mirz.handwriting.ui.screens.login

import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.repository.SignInWithGoogleResponse

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val signInWithGoogleResponse: SignInWithGoogleResponse = Response.Idle,
)