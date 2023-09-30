package com.mirz.handwriting.ui.screens.profile

import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.LessonEntity
import com.mirz.handwriting.domain.entities.UserEntity

data class ProfileUiState (
    val resultReports: Response<List<LessonEntity>> = Response.Idle,
    val resultProfile: Response<UserEntity> = Response.Idle,
    val resultLogout: Response<Boolean> = Response.Idle,
)