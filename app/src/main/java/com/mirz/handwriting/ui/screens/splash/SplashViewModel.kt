package com.mirz.handwriting.ui.screens.splash

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirz.handwriting.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SplashViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    var uiState = mutableStateOf(SplashUiState())
        private set

    init {
        getLoginState()
    }
    private fun getLoginState()  = viewModelScope.launch {
        val isLoggedIn = repository.isUserAuthenticatedInFirebase
        delay(2000)
        uiState.value = uiState.value.copy(isLoggedIn = isLoggedIn)
    }
}