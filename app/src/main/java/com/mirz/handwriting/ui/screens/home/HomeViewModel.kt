package com.mirz.handwriting.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.repository.AuthRepository
import com.mirz.handwriting.domain.repository.LessonRepository
import com.mirz.handwriting.ui.screens.login.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: LessonRepository) : ViewModel() {
    var uiState = mutableStateOf(HomeUiState())
        private set


    init {
        getLessons()
    }

    private fun getLessons()  = viewModelScope.launch {
        val result = repository.getLessons()
        uiState.value = uiState.value.copy(resultLessons = result)
    }
}