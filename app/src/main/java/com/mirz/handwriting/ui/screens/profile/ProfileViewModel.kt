package com.mirz.handwriting.ui.screens.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.repository.AuthRepository
import com.mirz.handwriting.domain.repository.LessonRepository
import com.mirz.handwriting.ui.screens.lesson.LessonUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val lessonRepository: LessonRepository, private val authRepository: AuthRepository
) : ViewModel() {
    var uiState = mutableStateOf(ProfileUiState())
        private set

    init {
        getProfile()
    }

    private fun getReports() = viewModelScope.launch {
        uiState.value = uiState.value.copy(resultReports = Response.Loading)
        delay(500L)
        uiState.value = uiState.value.copy(resultReports = lessonRepository.getReport())
    }

    private fun getProfile() = viewModelScope.launch {
        uiState.value = uiState.value.copy(resultProfile = authRepository.getUserData())
        getReports()
    }

    fun logout() = viewModelScope.launch {
        val response = authRepository.logout()
        uiState.value = uiState.value.copy(resultLogout = response)
    }
}