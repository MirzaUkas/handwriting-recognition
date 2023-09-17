package com.mirz.handwriting.domain.repository

import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.LessonEntity

interface LessonRepository {

    suspend fun getLessons(): Response<List<LessonEntity>>
    suspend fun getLesson(id: String): Response<LessonEntity>
}