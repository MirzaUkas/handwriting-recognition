package com.mirz.handwriting.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class MLKitModule {

    @Provides
    fun provideRecognizer(recognitionModel: DigitalInkRecognitionModel) =
        DigitalInkRecognition.getClient(
            DigitalInkRecognizerOptions
                .builder(recognitionModel)
                .build()
        )

    @Provides
    fun provideRecognitionModel() = DigitalInkRecognitionModel
        .builder(DigitalInkRecognitionModelIdentifier.ID)
        .build()
}