package com.mirz.handwriting.ui.screens.lesson

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirz.handwriting.domain.repository.LessonRepository
import com.mirz.handwriting.ui.screens.question.QuestionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository
) : ViewModel(){
    var uiState = mutableStateOf(LessonUiState())
        private set


    fun getLesson(id : String) = viewModelScope.launch {
        uiState.value = uiState.value.copy(resultLesson = lessonRepository.getLesson(id))
    }
}