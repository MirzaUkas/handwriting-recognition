package com.mirz.handwriting.ui.screens.home

import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.LessonEntity

data class HomeUiState(
    val resultLessons: Response<List<LessonEntity>> = Response.Idle,
    val refreshing: Boolean = false
)
