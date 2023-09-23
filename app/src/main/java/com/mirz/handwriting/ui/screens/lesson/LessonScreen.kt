package com.mirz.handwriting.ui.screens.lesson

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mirz.handwriting.R
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.domain.entities.QuestionEntity
import com.mirz.handwriting.ui.components.LessonItem
import com.mirz.handwriting.ui.theme.Grey7F
import com.mirz.handwriting.ui.theme.HandwritingTheme
import com.mirz.handwriting.ui.theme.NeutralGrey
import com.mirz.handwriting.ui.theme.typography

@Composable
fun LessonScreen(
    viewModel: LessonViewModel = hiltViewModel(),
    id: String,
    navigateBack: () -> Unit,
    navigateToQuestion: (QuestionEntity) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.getLesson(id)
    }

    val state by viewModel.uiState

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                IconButton(
                    onClick = navigateBack,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChevronLeft,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = Grey7F
                    )
                }
                Text(text = "Lessons", style = typography.h3)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (val data = state.resultLesson) {
                is Response.Success -> {
                    LessonSection(
                        name = data.data?.title.orEmpty(),
                        level = data.data?.level ?: 0,
                        answered = data.data?.items?.filter { question ->
                            !question.lastAnswer.isNullOrEmpty() }?.size ?: 0,
                        questions = data.data?.items?.size ?: 0,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    data.data?.items?.forEachIndexed { index, item ->
                        QuestionItem(
                            title = "Pertanyaan ${index + 1}",
                            desc = item.question.orEmpty(),
                            isAnswered = !item.lastAnswer.isNullOrEmpty(),
                            isLastItem = index == (data.data.items.size - 1),
                            onClick = {
                                navigateToQuestion(item.copy(id = index, questionId = data.data.id))
                            }
                        )
                    }
                }

                else -> {}
            }

        }
    }
}

@Composable
fun LessonSection(
    name: String,
    level: Int,
    answered: Int,
    questions: Int,
) {
    Card(
        shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.background(
                color = when (level) {
                    1 -> Color(0xFF686BFF)
                    2 -> Color(0xFFEE97BC)
                    3 -> Color(0xFF7ADAAB)
                    else -> Color(0xFF466CFF)
                }
            ),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .padding(16.dp)
                    .weight(0.5f)
            ) {
                Text(text = name, style = typography.body1.copy(color = Color.White))
            }
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .weight(0.5f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "$answered/$questions",
                    style = typography.body2.copy(color = Color.White),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(70.dp)
                        .background(
                            color = Color(0x80FFFFFF), shape = RoundedCornerShape(16.dp)
                        )
                        .padding(4.dp)
                )
                Image(
                    painterResource(
                        id = when (level) {
                            1 -> R.drawable.il_basic_alphabet
                            2 -> R.drawable.il_food_talk
                            3 -> R.drawable.il_family_talk
                            else -> R.drawable.il_basic_alphabet
                        }
                    ),
                    contentDescription = "",
                )
            }

        }
    }
}

@Composable
fun QuestionItem(
    title: String,
    desc: String,
    isAnswered: Boolean,
    isLastItem: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp)
                        .background(
                            color = if (isAnswered) Color(0xFFCDF7E3) else Color(0xFFEEF0F7),
                            shape = CircleShape
                        )
                ) {
                    if (isAnswered)
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color(0xFF59D79A),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(24.dp)
                        )

                }
                Column {
                    Text(
                        text = title,
                        style = typography.body1.copy(color = Grey7F, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = desc,
                        style = typography.body2.copy(color = Color(0xFFA1B2CF))
                    )
                }
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Grey7F
            )
        }

        if (!isLastItem)
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(6.dp)
                        .background(if (isAnswered) Color(0xFFCDF7E3) else Color(0xFFEEF0F7))
                        .align(Alignment.Center)
                )
            }
    }


}
