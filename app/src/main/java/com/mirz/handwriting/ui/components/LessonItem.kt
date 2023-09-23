package com.mirz.handwriting.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirz.handwriting.R
import com.mirz.handwriting.domain.entities.QuestionEntity
import com.mirz.handwriting.ui.theme.NeutralGrey
import com.mirz.handwriting.ui.theme.typography


@Composable
fun LessonItem(
    name: String,
    level: Int,
    questions: List<QuestionEntity>,
    onStart: (() -> Unit)? = null
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
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(0.5f)
            ) {
                Text(
                    text = name,
                    style = typography.body1.copy(color = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(shape = RoundedCornerShape(20.dp),
                    enabled = onStart != null,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    onClick = onStart ?: {}) {
                    Text(
                        text = if (onStart != null) "Mulai Belajar" else "Belum Tersedia",
                        style = typography.button.copy(
                            color = if (onStart != null) colors.primary else NeutralGrey,
                            fontSize = 12.sp
                        )
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .weight(0.5f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${questions.filter { !it.answer.isNullOrEmpty() }.size}/${questions.size}",
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

@Preview
@Composable
fun LessonItemPrev() {
    LessonItem(
        name = "Basic Grammar",
        level = 1,
        questions = listOf()
    )
}