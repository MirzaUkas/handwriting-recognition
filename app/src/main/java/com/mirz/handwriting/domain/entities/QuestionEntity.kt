package com.mirz.handwriting.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionEntity(
    val id: Int? = null,
    val questionId: String? = null,
    val report: ReportDetailEntity? = null,
    val answer: String? = null,
    val question: String? = null,
): Parcelable