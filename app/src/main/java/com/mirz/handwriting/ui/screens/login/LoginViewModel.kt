package com.mirz.handwriting.ui.screens.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    var uiState = mutableStateOf(LoginUiState())
        private set
    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password



    fun onEmailChange(newValue: String) {
        uiState.value =
            uiState.value.copy(email = newValue, signInWithGoogleResponse = Response.Idle)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value =
            uiState.value.copy(password = newValue, signInWithGoogleResponse = Response.Idle)
    }

    fun onLogin() = viewModelScope.launch {
        uiState.value = uiState.value.copy(signInWithGoogleResponse = Response.Loading)
        val signInWithGoogleResponse = repository.firebaseSignIn(email, password)
        uiState.value = uiState.value.copy(signInWithGoogleResponse = signInWithGoogleResponse)
    }
}