package com.mirz.handwriting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirz.handwriting.common.DrawEvent
import com.mirz.handwriting.common.MLKitModelStatus
import com.mirz.handwriting.common.SingleFlowViewModel
import com.mirz.handwriting.data.DigitalInkProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
class MainViewModel @Inject constructor(
    private val digitalInkProvider: DigitalInkProvider,
) : ViewModel(), DigitalInkViewModel {

    private var finishRecordingJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _digitalInkModelStatus = digitalInkProvider.checkIfModelIsDownloaded()
        .flatMapLatest { status ->
            if (status == MLKitModelStatus.Downloaded)
                flowOf(status)
            else
                digitalInkProvider.downloadModel()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, MLKitModelStatus.NotDownloaded)

//    private val _translatorModelStatus = translatorProvider.checkIfModelIsDownloaded()
//        .stateIn(viewModelScope, SharingStarted.Lazily, MLKitModelStatus.NotDownloaded)

    private val _predictions = digitalInkProvider.predictions
        .consumeAsFlow()
        .onEach {
            if (it.isEmpty())
                return@onEach

            setFinalText(text = _finalText.value.plus(it[0]))
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

//    private val _translation = translatorProvider.translation
//        .consumeAsFlow()
//        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    private val _finalText = MutableStateFlow<String>("")
    private val _resetCanvas = MutableStateFlow<Boolean>(false)


    override val state: StateFlow<DigitalInkViewModel.State>
        get() = combine(
            _digitalInkModelStatus,
//    _translatorModelStatus,
            _resetCanvas,
            _predictions,
//    _translation,
            _finalText
        ) { result ->
            val digitalInkModelStatus = result[0] as MLKitModelStatus
//        val translatorModelStatus = result[1] as MLKitModelStatus
            val resetCanvas = result[1] as Boolean
            val predictions = result[2] as List<String>
//        val translation = result[4] as String
            val finalText = result[3] as String
            val areModelsDownloaded = digitalInkModelStatus == MLKitModelStatus.Downloaded
//                && translatorModelStatus == MLKitModelStatus.Downloaded

            DigitalInkViewModel.State(
                resetCanvas = resetCanvas,
                showModelStatusProgress = !areModelsDownloaded,
                finalText = finalText,
//            translation = translation,
                predictions = predictions
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, DigitalInkViewModel.State())

    override fun onEvent(event: DigitalInkViewModel.Event) {
        when (event) {
            is DigitalInkViewModel.Event.Pointer -> {

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

            is DigitalInkViewModel.Event.OnStop -> {
                digitalInkProvider.close()
//                translatorProvider.close()
            }

            is DigitalInkViewModel.Event.TextChanged -> {
                setFinalText(event.text)
            }

            is DigitalInkViewModel.Event.PredictionSelected -> {
                setFinalText(text = _finalText.value.dropLast(1).plus(event.prediction))
            }
        }
    }

    fun clearCanvas() {
        this.finishRecordingJob = viewModelScope.launch {
            _resetCanvas.value = true
            digitalInkProvider.finishRecording()
        }
        setFinalText("")
    }

    fun submit(){
        this.finishRecordingJob = viewModelScope.launch {
//            _resetCanvas.value = true
            digitalInkProvider.finishRecording()
        }
    }

    private fun setFinalText(text: String) {
        _finalText.value = text

//        if (text.isNotEmpty())
//            translatorProvider.translate(text)
    }

}


interface DigitalInkViewModel :
    SingleFlowViewModel<DigitalInkViewModel.Event, DigitalInkViewModel.State> {

    data class State(
        val resetCanvas: Boolean = false,
        val showModelStatusProgress: Boolean = false,
        val finalText: String = "",
        val translation: String = "",
        val predictions: List<String> = emptyList(),
    )

    sealed class Event {
        data class TextChanged(val text: String) : Event()
        data class Pointer(val event: DrawEvent) : Event()
        data class PredictionSelected(val prediction: String) : Event()

        object OnStop : Event()
    }
}


//val LocalDigitalInkViewModel = compositionLocalOf<DigitalInkViewModel> {
//    error("LocalDigitalViewModelFactory not provided")
//}
//
//@Composable
//fun provideDigitalInkViewModel(viewModelFactory: @Composable () -> DigitalInkViewModel)
//        = LocalDigitalInkViewModel provides viewModelFactory.invoke()