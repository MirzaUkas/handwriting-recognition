package com.mirz.handwriting.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.mirz.handwriting.data.DigitalInkProvider
import com.mirz.handwriting.data.DigitalInkProviderImpl
import com.mirz.handwriting.data.repository.AuthRepositoryImpl
import com.mirz.handwriting.data.repository.LessonRepositoryImpl
import com.mirz.handwriting.data.repository.MLKitRepositoryImpl
import com.mirz.handwriting.data.repository.QuestionRepositoryImpl
import com.mirz.handwriting.domain.repository.AuthRepository
import com.mirz.handwriting.domain.repository.LessonRepository
import com.mirz.handwriting.domain.repository.MLKitRepository
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
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl(
        auth = auth,
        firestore = firestore
    )

    @Provides
    fun provideLessonRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
    ): LessonRepository = LessonRepositoryImpl(
        firestore = firestore,
        auth = auth
    )

    @Provides
    fun provideQuestionRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
    ): QuestionRepository = QuestionRepositoryImpl(
        firestore = firestore,
        auth = auth
    )

    @Provides
    fun provideMLKitRepository(
        recognitionModel: DigitalInkRecognitionModel,
        recognizer: DigitalInkRecognizer
    ): MLKitRepository = MLKitRepositoryImpl(
        recognitionModel, recognizer
    )

}