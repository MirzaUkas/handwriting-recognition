package com.mirz.handwriting.ui.screens.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.digitalink.WritingArea
import com.mirz.handwriting.common.DrawEvent
import com.mirz.handwriting.common.MLKitModelStatus
import com.mirz.handwriting.data.DigitalInkProvider
import com.mirz.handwriting.domain.entities.QuestionEntity
import com.mirz.handwriting.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val digitalInkProvider: DigitalInkProvider,
    private val questionRepository: QuestionRepository,
) : ViewModel() {
    private val _finalText = MutableStateFlow("")
    private val _resetCanvas = MutableStateFlow(false)
    private val _pos = MutableStateFlow(0)
    private val _question = MutableStateFlow(QuestionEntity())

    private val _predictions = digitalInkProvider.predictions
        .consumeAsFlow()
        .onEach {
            if (it.isEmpty())
                return@onEach

            setFinalText(text = _finalText.value.plus(it[0]))
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _digitalInkModelStatus = digitalInkProvider.checkIfModelIsDownloaded()
        .flatMapLatest { status ->
            if (status == MLKitModelStatus.Downloaded)
                flowOf(status)
            else
                digitalInkProvider.downloadModel()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, MLKitModelStatus.NotDownloaded)


    val uiState: StateFlow<QuestionUiState>
        get() = combine(
            _digitalInkModelStatus,
            _resetCanvas,
            _predictions,
            _finalText,
            _pos,
            _question,
        ) { result ->
            val digitalInkModelStatus = result[0] as MLKitModelStatus
            val resetCanvas = result[1] as Boolean
            val predictions = result[2] as List<String>
            val finalText = result[3] as String
            val pos = result[4] as Int
            val question = result[5] as QuestionEntity
            val areModelsDownloaded = digitalInkModelStatus == MLKitModelStatus.Downloaded
            QuestionUiState(
                showModelStatusProgress = !areModelsDownloaded,
                resetCanvas = resetCanvas,
                predictions = predictions,
                finalText = finalText,
                question = question,
                pos = pos,
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, QuestionUiState())

    private var finishRecordingJob: Job? = null


    fun onDrawEvent(event: Event) {
        when (event) {
            is Event.Pointer -> {
                when (val drawEvent = event.event) {
                    is DrawEvent.Down -> {
                        this.finishRecordingJob?.cancel()
                        _resetCanvas.value = false
                        digitalInkProvider.record(drawEvent.x, drawEvent.y)
                    }

                    is DrawEvent.Move -> {
                        digitalInkProvider.record(drawEvent.x, drawEvent.y)
                    }

                    is DrawEvent.Up -> {
//                        this.finishRecordingJob = viewModelScope.launch {
//                            delay(DEBOUNCE_INTERVAL)
//                            _resetCanvas.value = true
//                            digitalInkProvider.finishRecording()
//                        }
                    }
                }
            }

            is Event.OnStop -> {
                digitalInkProvider.close()
            }

            is Event.TextChanged -> {
                setFinalText(event.text)
            }

            is Event.PredictionSelected -> {
                setFinalText(text = _finalText.value.dropLast(1).plus(event.prediction))
            }
        }
    }

    fun clearCanvas(writingArea: WritingArea) {
        this.finishRecordingJob = viewModelScope.launch {
            _resetCanvas.value = true
            _finalText.value = ""
            digitalInkProvider.finishRecording(writingArea, "")
        }
    }

    fun submit(writingArea: WritingArea) {
        this.finishRecordingJob = viewModelScope.launch {
            _resetCanvas.value = true
            _finalText.value = ""
            digitalInkProvider.finishRecording(writingArea, "")
        }
    }

    fun setQuestionData(question: QuestionEntity) = viewModelScope.launch {
        _question.value = question
    }

    private fun setFinalText(text: String) = viewModelScope.launch {
        _finalText.value = text
        val question = _question.value

        questionRepository.submitQuestion(
            id = question.questionId.toString(),
            pos = question.id ?: -1,
            answer = text
        )
    }
}


sealed class Event {
    data class TextChanged(val text: String) : Event()
    data class Pointer(val event: DrawEvent) : Event()
    data class PredictionSelected(val prediction: String) : Event()

    object OnStop : Event()
}