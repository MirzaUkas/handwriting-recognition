package com.mirz.handwriting.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mirz.handwriting.data.repository.AuthRepositoryImpl
import com.mirz.handwriting.data.repository.LessonRepositoryImpl
import com.mirz.handwriting.data.repository.QuestionRepositoryImpl
import com.mirz.handwriting.domain.repository.AuthRepository
import com.mirz.handwriting.domain.repository.LessonRepository
import com.mirz.handwriting.domain.repository.QuestionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {

    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
    ): AuthRepository = AuthRepositoryImpl(
        auth = auth
    )

    @Provides
    fun provideLessonRepository(
        firestore: FirebaseFirestore
    ): LessonRepository = LessonRepositoryImpl(
        firestore = firestore
    )

    @Provides
    fun provideQuestionRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
    ): QuestionRepository = QuestionRepositoryImpl(
        firestore = firestore,
        auth = auth
    )

}