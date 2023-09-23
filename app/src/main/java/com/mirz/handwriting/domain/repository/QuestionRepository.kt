package com.mirz.handwriting.domain.repository

import com.mirz.handwriting.common.Response

interface QuestionRepository {

    suspend fun submitQuestion(id: String, pos: Int, answer: String, correct: Boolean) : Response<Any>
}