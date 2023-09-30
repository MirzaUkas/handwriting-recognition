package com.mirz.handwriting.data.repository

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.RecognitionContext
import com.google.mlkit.vision.digitalink.WritingArea
import com.mirz.handwriting.common.MLKitModelStatus
import com.mirz.handwriting.domain.repository.MLKitRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MLKitRepositoryImpl @Inject constructor(
    private val recognitionModel: DigitalInkRecognitionModel,
    private val recognizer: DigitalInkRecognizer
) : MLKitRepository {

    override val predictions = Channel<List<String>>(4)

    private var strokeBuilder: Ink.Stroke.Builder = Ink.Stroke.builder()

    private val remoteModelManager = RemoteModelManager.getInstance()


    override fun checkIfModelIsDownloaded(): Flow<MLKitModelStatus> = callbackFlow {
        trySend(MLKitModelStatus.CheckingDownload)

        remoteModelManager
            .isModelDownloaded(recognitionModel)
            .addOnSuccessListener { isDownloaded ->
                if (isDownloaded)
                    trySend(MLKitModelStatus.Downloaded)
                else
                    trySend(MLKitModelStatus.NotDownloaded)
            }
            .addOnCompleteListener { close() }
            .addOnFailureListener {
                it.printStackTrace()
                close(it)
            }

        awaitClose { cancel() }
    }

    override fun downloadModel(): Flow<MLKitModelStatus> = callbackFlow {
        val downloadConditions = DownloadConditions.Builder()
            .build()

        trySend(MLKitModelStatus.Downloading)
        remoteModelManager
            .download(recognitionModel, downloadConditions)
            .addOnSuccessListener {
                trySend(MLKitModelStatus.Downloaded)
            }
            .addOnCompleteListener { close() }
            .addOnFailureListener {
                it.printStackTrace()
                close(it)
            }

        awaitClose { cancel() }
    }

    override fun record(x: Float, y: Float) {
        val point = Ink.Point.create(x, y)
        this.strokeBuilder.addPoint(point)
    }

    override fun finishRecording(writingArea: WritingArea, preContext: String) {
        val stroke = this.strokeBuilder.build()
        val recognitionContext = RecognitionContext.builder()
            .setWritingArea(writingArea).setPreContext(preContext)

        val inkBuilder = Ink.builder()
        inkBuilder.addStroke(stroke)

        try {
            recognizer.recognize(inkBuilder.build(), recognitionContext.build())
                .addOnCompleteListener {
                    this.strokeBuilder = Ink.Stroke.builder()
                }
                .addOnSuccessListener { result -> this.predictions.trySend(result.candidates.map { it.text }) }
                .addOnFailureListener { it.printStackTrace() }
        } catch (e: Exception) {
            Log.e("HERE", e.localizedMessage.toString())
        }

    }

    override fun close() {
        this.recognizer.close()
    }
}