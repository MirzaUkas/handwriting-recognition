package com.mirz.handwriting.domain.repository

import com.google.mlkit.vision.digitalink.WritingArea
import com.mirz.handwriting.common.MLKitModelStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

interface MLKitRepository {
    val predictions: Channel<List<String>>

    fun finishRecording(writingArea: WritingArea, preContext: String)
    fun record(x: Float, y: Float)

    fun downloadModel(): Flow<MLKitModelStatus>
    fun checkIfModelIsDownloaded(): Flow<MLKitModelStatus>

    fun close()
}