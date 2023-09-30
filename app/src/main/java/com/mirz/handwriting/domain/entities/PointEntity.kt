package com.mirz.handwriting.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointEntity(
    val x: Float? = null,
    val y: Float? = null,
): Parcelable