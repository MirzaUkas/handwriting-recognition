package com.mirz.handwriting.domain.repository

interface QuestionRepository {

    suspend fun submitQuestion(id: String, pos: Int, answer: String)
}