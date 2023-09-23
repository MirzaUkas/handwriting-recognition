package com.mirz.handwriting.ui.screens.question

import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.QuestionEntity

data class QuestionUiState(
    val isCorrect: Boolean = false,
    val resetCanvas: Boolean = false,
    val showModelStatusProgress: Boolean = false,
    val finalText: String = "",
    val pos: Int = 0,
    val question: QuestionEntity = QuestionEntity(),
    val predictions: List<String> = emptyList(),
    val submitReportResponse: Response<Any> = Response.Idle,
)
