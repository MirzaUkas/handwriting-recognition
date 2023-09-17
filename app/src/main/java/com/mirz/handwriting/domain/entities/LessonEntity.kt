package com.mirz.handwriting.domain.entities

data class LessonEntity(
    val id: String? = null,
    val title: String? = null,
    val level: Int? = null,
    val active: Boolean? = null,
    val items: List<QuestionEntity>? = null,
)
