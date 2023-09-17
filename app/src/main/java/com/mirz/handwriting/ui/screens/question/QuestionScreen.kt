package com.mirz.handwriting.ui.screens.question

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.mlkit.vision.digitalink.WritingArea
import com.mirz.handwriting.ModelStatusProgress
import com.mirz.handwriting.R
import com.mirz.handwriting.common.AutoSizeText
import com.mirz.handwriting.common.DrawSpace
import com.mirz.handwriting.ui.theme.Grey7F
import com.mirz.handwriting.ui.theme.dashedFamily
import com.mirz.handwriting.ui.theme.typography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuestionScreen(
    viewModel: QuestionViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val state = viewModel.uiState.collectAsState().value
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = false
    )


    DisposableEffect(Unit) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) viewModel.onDrawEvent(Event.OnStop)
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycleOwner.lifecycle.removeObserver(lifecycleObserver) }
    }


    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    LaunchedEffect(state.finalText) {
        if (state.finalText.isNotEmpty()) coroutineScope.launch { modalSheetState.show() }
    }


    ModalBottomSheetLayout(sheetState = modalSheetState, sheetContent = {
        if (state.finalText == state.question.question) CorrectAnswerBottomSheet() else TryAgainBottomSheet {
            coroutineScope.launch {
                modalSheetState.hide()
                viewModel.clearCanvas(
                    WritingArea(
                        configuration.screenWidthDp.dp.value,
                        100.dp.value,
                    )
                )
            }
        }
    }) {
        Scaffold(
            topBar = {
                Row() {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                }
            },
        ) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(color = Color.White)
            ) {
                if (state.showModelStatusProgress) {
                    ModelStatusProgress(
                        statusText = "Checking models...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    DrawSpace(
                        reset = state.resetCanvas,
                        onDrawEvent = { event ->
                            viewModel.onDrawEvent(Event.Pointer(event))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 16.dp)
                            .border(BorderStroke(1.dp, Color.Red))
                            .align(Alignment.Center)
                            .padding(bottom = 20.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 16.dp)
                            .align(Alignment.Center)
                            .padding(bottom = 20.dp),
                    ) {
                        AutoSizeText(
                            text = state.question.question.toString(),
                            fontSize = 100.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = dashedFamily,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center),

                            )
                    }

                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Card(
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        backgroundColor = Color.White,
                        elevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .background(
                                        color = Color(0xFFEEF0F7), shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Text(
                                    text = state.question.question.toString(),
                                    color = Grey7F,
                                    style = typography.h3,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.Center)
                                )
                            }

                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                enabled = !state.showModelStatusProgress,
                                contentPadding = PaddingValues(16.dp),
                                onClick = {
                                    viewModel.submit(
                                        WritingArea(
                                            configuration.screenWidthDp.dp.value,
                                            100.dp.value,
                                        )
                                    )
                                }) {
                                Text("Kirim", style = typography.button)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .offset(y = (-16).dp)
                            .background(
                                color = colors.primary, shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 6.dp, horizontal = 16.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        Text(text = "Question 1", color = Color.White, style = typography.body1)
                    }
                }

            }
        }
    }

}

@Composable
fun CorrectAnswerBottomSheet() {
    val config = LocalConfiguration.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(config.screenHeightDp.dp / 2)
            .background(color = Color(0xFFDFFFF0))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)

        ) {
            Image(
                painter = painterResource(R.drawable.ic_stars),
                contentDescription = null,
            )
            Text(text = "Perfect!", style = typography.h3.copy(color = Color.Black))
        }

    }
}

@Composable
fun TryAgainBottomSheet(
    onTryAgain: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        backgroundColor = Color.White,
        elevation = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text(text = "Coba Lagi", style = typography.h3)
            Text(
                text = "Kamu harus menulis sesuai garis yang ditentukan",
                style = typography.body1,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFEB4242), contentColor = Color.White
                ),
                onClick = onTryAgain
            ) {
                Text("Coba Lagi", style = typography.button)
            }
        }
    }
}
