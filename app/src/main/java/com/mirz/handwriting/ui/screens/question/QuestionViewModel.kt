package com.mirz.handwriting.ui.screens.question

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.WritingArea
import com.mirz.handwriting.common.DrawEvent
import com.mirz.handwriting.common.MLKitModelStatus
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.common.SingleFlowViewModel
import com.mirz.handwriting.domain.entities.QuestionEntity
import com.mirz.handwriting.domain.repository.MLKitRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val mlKitRepository: MLKitRepository,
    private val questionRepository: QuestionRepository,
) : ViewModel(), DigitalInkViewModel {
    private var _strokeBuilder = Ink.Stroke.builder()
    private val _points = MutableStateFlow(listOf<Ink.Point>())
    private val _finalText = MutableStateFlow("")
    private val _resetCanvas = MutableStateFlow(false)
    private val _pos = MutableStateFlow(0)
    private val _question = MutableStateFlow(QuestionEntity())
    private val _submitResponse = MutableStateFlow(QuestionUiState().submitReportResponse)
    private val _predictions = mlKitRepository.predictions.consumeAsFlow().onEach {
        if (it.isEmpty()) return@onEach

        setFinalText(text = _finalText.value.plus(it[0]))
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _digitalInkModelStatus =
        mlKitRepository.checkIfModelIsDownloaded().flatMapLatest { status ->
            if (status == MLKitModelStatus.Downloaded) flowOf(status)
            else mlKitRepository.downloadModel()
        }.stateIn(viewModelScope, SharingStarted.Lazily, MLKitModelStatus.NotDownloaded)

    override val state: StateFlow<QuestionUiState>
        get() = combine(
            _digitalInkModelStatus,
            _resetCanvas,
            _predictions,
            _finalText,
            _pos,
            _question,
            _submitResponse,
            _points
        ) { result ->
            val digitalInkModelStatus = result[0] as MLKitModelStatus
            val resetCanvas = result[1] as Boolean
            val predictions = result[2] as List<String>
            val finalText = result[3] as String
            val pos = result[4] as Int
            val question = result[5] as QuestionEntity
            val submitResponse = result[6] as Response<Any>
            val points = result[7] as List<Ink.Point>
            val areModelsDownloaded = digitalInkModelStatus == MLKitModelStatus.Downloaded
            QuestionUiState(
                showModelStatusProgress = !areModelsDownloaded,
                resetCanvas = resetCanvas,
                predictions = predictions,
                finalText = finalText,
                question = question,
                pos = pos,
                submitReportResponse = submitResponse,
                points = points,
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, QuestionUiState())


    private var finishRecordingJob: Job? = null


    fun clearCanvas(writingArea: WritingArea) {
        this.finishRecordingJob = viewModelScope.launch {
            _resetCanvas.value = true
            _finalText.value = ""
            mlKitRepository.finishRecording(writingArea, "")
        }
    }

    fun submit(writingArea: WritingArea) {
        this.finishRecordingJob = viewModelScope.launch {
            Log.e("HERE", _finalText.value)
            _resetCanvas.value = true
            _finalText.value = ""
            mlKitRepository.finishRecording(writingArea, "")
        }
    }

    fun setQuestionData(question: QuestionEntity) = viewModelScope.launch {
        _question.value = question
    }

    private fun setFinalText(text: String) = viewModelScope.launch {
        _finalText.value = text
    }

    private fun setPoints(point: Ink.Point) = viewModelScope.launch {
        _strokeBuilder.addPoint(point)
        _points.value = _strokeBuilder.build().points
    }

    fun onSubmitReport() = viewModelScope.launch {
        val question = _question.value
        val response = questionRepository.submitQuestion(
            id = question.questionId.toString(),
            pos = question.id ?: -1,
            correct = question.question == _finalText.value,
            answer = _finalText.value,
            points = _points.value
        )
        _submitResponse.value = response
    }

    override fun onEvent(event: DigitalInkViewModel.Event) {
        when (event) {
            is DigitalInkViewModel.Event.Pointer -> {
                when (val drawEvent = event.event) {
                    is DrawEvent.Down -> {
                        finishRecordingJob?.cancel()
                        _resetCanvas.value = false
                        setPoints(
                            Ink.Point.create(
                                drawEvent.x, drawEvent.y
                            )
                        )
                        mlKitRepository.record(drawEvent.x, drawEvent.y)
                    }

                    is DrawEvent.Move -> {
                        setPoints(
                            Ink.Point.create(
                                drawEvent.x, drawEvent.y
                            )
                        )
                        mlKitRepository.record(drawEvent.x, drawEvent.y)
                    }

                    is DrawEvent.Up -> {
//                        this.finishRecordingJob = viewModelScope.launch {
//                            delay(DEBOUNCE_INTERVAL)
//                            _resetCanvas.value = true
//                            mlKitRepository.finishRecording()
//                        }
                    }
                }
            }

            is DigitalInkViewModel.Event.OnStop -> {
                mlKitRepository.close()
            }

            is DigitalInkViewModel.Event.TextChanged -> {
                setFinalText(event.text)
            }

            is DigitalInkViewModel.Event.PredictionSelected -> {
                setFinalText(text = _finalText.value.dropLast(1).plus(event.prediction))
            }
        }
    }

}


interface DigitalInkViewModel : SingleFlowViewModel<DigitalInkViewModel.Event, QuestionUiState> {

    sealed class Event {
        data class TextChanged(val text: String) : Event()
        data class Pointer(val event: DrawEvent) : Event()
        data class PredictionSelected(val prediction: String) : Event()

        object OnStop : Event()
    }
}

