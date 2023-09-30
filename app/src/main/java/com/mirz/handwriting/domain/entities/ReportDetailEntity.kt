package com.mirz.handwriting.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportDetailEntity(
    val answer: String? = null,
    val answerId: Int? = null,
    val correct: Boolean? = null,
    val retryCount: Int? = null,
    val points: List<PointEntity>? = null
): Parcelable
