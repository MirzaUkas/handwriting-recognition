package com.mirz.handwriting.ui.screens.profile

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mirz.handwriting.R
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.common.ShimmerBrushAnimation
import com.mirz.handwriting.domain.entities.LessonEntity
import com.mirz.handwriting.domain.entities.ReportDetailEntity
import com.mirz.handwriting.domain.entities.UserEntity
import com.mirz.handwriting.navigation.BottomNav
import com.mirz.handwriting.navigation.Screens
import com.mirz.handwriting.ui.theme.HandwritingTheme
import com.mirz.handwriting.ui.theme.dashedFamily
import com.mirz.handwriting.ui.theme.typography
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(), navController: NavController
) {
    val state by viewModel.uiState

    Scaffold(bottomBar = {
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            BottomNav(navController)
        }
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = colors.primary)
                .fillMaxSize()
        ) {
            Image(
                painterResource(id = R.drawable.il_profile_footer),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val result = state.resultProfile) {
                    is Response.Success -> result.data?.let {
                        ProfileContent(
                            user = it, onLogout = viewModel::logout
                        )
                    }

                    is Response.Loading -> Unit
                    else -> Unit
                }


                Spacer(modifier = Modifier.height(16.dp))


                when (val result = state.resultReports) {
                    is Response.Success -> result.data?.let { lessons ->
//                        val totalProgress = lessons.filter { it.active == true }.map { lesson ->
//                            lesson.items?.let { questions ->
//                                questions.filter { question ->
//                                    question.report?.correct == true
//                                }.size.toFloat().div(questions.size.toFloat())
//                            }
//                        }

//                        Text(
//                            text = "Total Learning Progress ${totalProgress.times(100).toInt()}%",
//                            style = typography.body1.copy(color = Color.White),
//                            modifier = Modifier.padding(vertical = 16.dp)
//                        )
//
//                        LinearProgressIndicator(
//                            progress = totalProgress,
//                            color = if (totalProgress < 1f) Color(0xFF686BFF) else Color(0xFF59D79A),
//                            backgroundColor = Color(0xFFE0E0E0),
//                            strokeCap = StrokeCap.Round,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(10.dp).padding(bottom = 8.dp),
//                        )

                        lessons.filter { it.active == true }.forEach { lesson ->
                            LessonReportItem(lesson)
                        }
                    }

                    is Response.Loading -> {
                        LessonReportShimmer()
                        Spacer(modifier = Modifier.height(8.dp))
                        LessonReportShimmer()
                        Spacer(modifier = Modifier.height(8.dp))
                        LessonReportShimmer()
                    }

                    else -> Unit
                }
            }


        }

    }

    LaunchedEffect(state.resultLogout) {
        when (state.resultLogout) {
            is Response.Success -> {
                navController.navigate(Screens.Login) {
                    popUpTo(0)
                }
            }

            else -> Unit
        }
    }

}

@Composable
fun ProfileContent(user: UserEntity, onLogout: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(8.dp),
            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                backgroundColor = Color.Red, contentColor = Color.White
            ),
        ) {
            Text(
                text = "Logout", style = typography.body1.copy(color = Color.White)
            )
        }


    }

    Image(
        painter = painterResource(id = R.drawable.ic_profile_placeholder),
        contentDescription = null,
        modifier = Modifier.padding(vertical = 16.dp)
    )
    Text(
        text = user.fullName.toString(), style = typography.h3.copy(color = Color.White)
    )
    Text(
        text = user.email.toString(), style = typography.body1.copy(color = Color.White)
    )
}


@Composable
fun LessonReportItem(lesson: LessonEntity) {
    val (isExpanded, setIsExpanded) = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { setIsExpanded(!isExpanded) }, elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Image(
                    painterResource(
                        id = when (lesson.level) {
                            1 -> R.drawable.il_basic_alphabet
                            2 -> R.drawable.il_food_talk
                            3 -> R.drawable.il_family_talk
                            else -> R.drawable.il_basic_alphabet
                        }
                    ),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = null,
                    modifier = Modifier
                        .width(100.dp)
                        .background(
                            color = when (lesson.level) {
                                1 -> Color(0xFF686BFF)
                                2 -> Color(0xFFEE97BC)
                                3 -> Color(0xFF7ADAAB)
                                else -> Color(0xFF466CFF)
                            }, shape = RoundedCornerShape(4.dp)
                        )
                        .padding(top = 8.dp, start= 8.dp, end = 8.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = lesson.title.orEmpty(),
                        style = typography.body1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Level ${lesson.level}",
                        style = typography.body2.copy(color = Color.Gray),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }



            lesson.items?.let { questions ->
                val progress = questions.filter { question ->
                    question.report?.correct == true
                }.size.toFloat().div(questions.size.toFloat())

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    LinearProgressIndicator(
                        progress = progress,
                        color = if (progress < 1f) Color(0xFF686BFF) else Color(0xFF59D79A),
                        backgroundColor = Color(0xFFE0E0E0),
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier
                            .weight(0.8f)
                            .height(5.dp),
                    )
                    Text(
                        text = "${progress.times(100).toInt()}%",
                        style = typography.body2.copy(color = Color.Gray),
                        modifier = Modifier
                            .weight(0.2f)
                            .padding(start = 8.dp)
                    )
                }

            }


            Icon(
                imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    lesson.items?.forEach { question ->
                        question.report?.let { report ->
                            AnswerReportItem(question.question.orEmpty(), report)
                        }
                    }
                }

            }
        }
    }
}


@Composable
fun LessonReportShimmer() {
    Card(
        backgroundColor = Color(0xFFF2F2F2), modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .width(100.dp)
                        .height(100.dp)
                        .background(ShimmerBrushAnimation())
                )
                Column {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .width(100.dp)
                            .height(20.dp)
                            .background(ShimmerBrushAnimation())
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .width(150.dp)
                            .height(20.dp)
                            .background(ShimmerBrushAnimation())
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(ShimmerBrushAnimation())
            )
        }
    }
}


@Composable
fun AnswerReportItem(
    question: String, report: ReportDetailEntity
) {

    val path = remember { Path() }
    val horizontalPadding = with(LocalDensity.current) {
        20.dp.toPx()
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (report.correct == true) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                tint = Color(0xFF59D79A),
                contentDescription = null,
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Close,
                tint = Color(0xFFEB4242),
                contentDescription = null,
            )
        }


        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp, color = Color(0xFFE0E0E0), shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp)
            ) {
                val (text, canvas) = createRefs()


                Text(text = question,
                    style = typography.h2,
                    fontFamily = dashedFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(text) {
                            linkTo(parent.top, parent.bottom)
                            linkTo(parent.start, parent.end)
                        })

                Canvas(modifier = Modifier.constrainAs(canvas) {
                    linkTo(text.top, text.bottom)
                    linkTo(text.start, text.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }, onDraw = {

                    report.points?.let {
                        path.reset()
                        it.forEachIndexed { index, point ->
                            val pointX = (point.x ?: 0f) / 2
                            val pointY = (point.y ?: 0f) / 2
                            if (index == 0) {
                                path.moveTo(pointX, pointY)
                            } else {
                                path.lineTo(pointX, pointY)
                            }
                        }
                    }
                    path.translate(Offset((size.width / 4) - horizontalPadding, 5f))
                    clipRect {
                        drawPath(
                            path = path,
                            color = if (report.correct == true) Color(0xFF59D79A) else Color.Red,
                            style = Stroke(width = 2f)
                        )
                    }

                    Log.e("PATH", "onDraw: ${(size.width / 4) - horizontalPadding}")


                })
            }

            Text(
                text = "Total Percobaan: ${report.retryCount}",
                fontStyle = FontStyle.Italic,
                style = typography.caption.copy(color = Color.Gray),
            )
        }

    }
}


inline fun DrawScope.clipRect(
    left: Float = 0.0f,
    top: Float = 0.0f,
    right: Float = size.width,
    bottom: Float = size.height,
    clipOp: ClipOp = ClipOp.Intersect,
    block: DrawScope.() -> Unit
) = withTransform({ clipRect(left, top, right, bottom, clipOp) }, block)