package com.mirz.handwriting.ui.screens.lesson

import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.LessonEntity

data class LessonUiState (
    val resultLesson: Response<LessonEntity> = Response.Idle,
)