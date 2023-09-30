package com.mirz.handwriting.domain.repository

import com.google.mlkit.vision.digitalink.Ink
import com.mirz.handwriting.common.Response

interface QuestionRepository {

    suspend fun submitQuestion(
        id: String,
        pos: Int,
        answer: String,
        correct: Boolean,
        points: List<Ink.Point>
    ): Response<Any>
}