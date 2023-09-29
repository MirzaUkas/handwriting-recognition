package com.mirz.handwriting.ui.screens.home

import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mirz.handwriting.R
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.navigation.BottomNav
import com.mirz.handwriting.navigation.Screens
import com.mirz.handwriting.ui.components.LessonItem
import com.mirz.handwriting.ui.components.LessonShimmer
import com.mirz.handwriting.ui.theme.HandwritingTheme
import com.mirz.handwriting.ui.theme.NeutralGrey
import com.mirz.handwriting.ui.theme.PurpleGrey40
import com.mirz.handwriting.ui.theme.typography


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
    navigateToLesson: (String) -> Unit
) {
    val uiState by viewModel.uiState

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                BottomNav(navController)
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                )

                Text(
                    text = "Belajar Menulis",
                    style = typography.h3.copy(color = PurpleGrey40),
                )

            }

            LaunchedEffect(uiState.resultLessons){
                Log.e("HERE", "RES: ${uiState.resultLessons}")
            }
            when (val data = uiState.resultLessons) {
                is Response.Success -> {
                    data.data?.let { lessons ->
                        lessons.forEach { lesson ->
                            LessonItem(
                                name = lesson.title.orEmpty(),
                                level = lesson.level ?: 0,
                                questions = lesson.items.orEmpty(),
                                onStart = if (lesson.active == true) {
                                    {
                                        navigateToLesson(lesson.id.toString())
                                    }
                                } else null
                            )
                        }

                    }
                }

                is Response.Loading -> {
                    LessonShimmer()
                    LessonShimmer()
                    LessonShimmer()
                }

                is Response.Failure -> Toast.makeText(
                    LocalContext.current,
                    data.e.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()

                else -> Unit
            }

        }
    }
}


