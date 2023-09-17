package com.mirz.handwriting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.mlkit.vision.digitalink.WritingArea
import com.mirz.handwriting.common.AutoSizeText
import com.mirz.handwriting.common.DrawEvent
import com.mirz.handwriting.common.DrawSpace
import com.mirz.handwriting.common.use
import com.mirz.handwriting.navigation.MainNavGraph
import com.mirz.handwriting.ui.theme.HandwritingTheme
import com.mirz.handwriting.ui.theme.dashedFamily
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HandwritingTheme {
                MainNavGraph()
            }
        }
    }
}

@Composable
fun MainContent(
    state: DigitalInkViewModel.State,
    onDrawEvent: (DrawEvent) -> Unit,
    onClearCanvas: (WritingArea) -> Unit,
    onSubmit: (WritingArea) -> Unit,
) {

    val configuration = LocalConfiguration.current
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.showModelStatusProgress) {
            ModelStatusProgress(
                statusText = "Checking models...", modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Text(
                text = state.finalText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            )
            DrawSpace(
                reset = state.resetCanvas,
                onDrawEvent = onDrawEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 16.dp)
                    .border(BorderStroke(1.dp, Color.Red))
                    .align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Center),
            ) {
                AutoSizeText(
                    text = "SEPEDA",
                    fontSize = 100.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = dashedFamily,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),

                    )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(0.5f),
                    onClick = {
                        onClearCanvas(
                            WritingArea(
                                configuration.screenWidthDp.dp.value,
                                100.dp.value,
                            )
                        )
                    },
                ) {
                    Text("Clear")
                }
                Button(
                    modifier = Modifier.weight(0.5f),
                    onClick = {
                        onSubmit(
                            WritingArea(
                                configuration.screenWidthDp.dp.value,
                                100.dp.value,
                            )
                        )
                    },
                ) {
                    Text("Submit")
                }

            }

        }


    }
}


@Composable
fun DigitalInkScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val (state, event) = use(viewModel, DigitalInkViewModel.State())

    DisposableEffect(Unit) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) event(DigitalInkViewModel.Event.OnStop)
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycleOwner.lifecycle.removeObserver(lifecycleObserver) }
    }

    MainContent(
        state = state,
        onDrawEvent = {
            event(DigitalInkViewModel.Event.Pointer(it))
        },
        onClearCanvas = viewModel::clearCanvas,
        onSubmit = viewModel::submit,
    )
}


@Composable
fun ModelStatusProgress(
    statusText: String, modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {

        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 24.dp)
        )

        Text(
            text = statusText, textAlign = TextAlign.Center, fontSize = 18.sp
        )
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_2)
@Composable
fun GreetingPreview() {
    HandwritingTheme {
        MainContent(state = DigitalInkViewModel.State(),
            onDrawEvent = {},
            onClearCanvas = {},
            onSubmit = {})
    }
}