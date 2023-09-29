package com.mirz.handwriting.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mirz.handwriting.common.parcelable
import com.mirz.handwriting.domain.entities.QuestionEntity
import com.mirz.handwriting.ui.screens.home.HomeScreen
import com.mirz.handwriting.ui.screens.lesson.LessonScreen
import com.mirz.handwriting.ui.screens.login.LoginScreen
import com.mirz.handwriting.ui.screens.profile.ProfileScreen
import com.mirz.handwriting.ui.screens.question.QuestionScreen
import com.mirz.handwriting.ui.screens.question.QuestionViewModel
import com.mirz.handwriting.ui.screens.splash.SplashScreen


@Composable
fun MainNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.Splash,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {

        composable(Screens.Splash) {
            SplashScreen(
                navigateToHome = {
                    navController.navigate(Screens.Home)
                },
                navigateToLogin = {
                    navController.navigate(Screens.Login)
                },
            )
        }
        composable(Screens.Profile) {
            ProfileScreen(
                navController = navController,
            )
        }
        composable(Screens.Home) {
            HomeScreen(
                navController = navController,
                navigateToLesson = { id ->
                    navController.navigate("${Screens.Lesson}/$id")
                }
            )
        }
        composable(Screens.Login) {
            LoginScreen(
                navigateToHome = {
                    navController.navigate(Screens.Home)
                }
            )
        }

        composable(
            route = "${Screens.Lesson}/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) {
            LessonScreen(
                id = it.arguments?.getString("id") ?: "",
                navigateBack = navController::navigateUp,
                navigateToQuestion = navController::navigateToQuestion
            )
        }

        composable(
            route = "${Screens.Question}/{question}",
            arguments = listOf(
                navArgument("question") {
                    type = QuestionType()
                }
            )
        ) { entry ->
            val viewModel = hiltViewModel<QuestionViewModel>()

            LaunchedEffect(Unit){
                entry.arguments?.parcelable<QuestionEntity>("question")?.let { question ->
                    viewModel.setQuestionData(
                        question = question,
                    )
                }
            }

            QuestionScreen(
                viewModel = viewModel,
                navigateBack = navController::navigateUp,
            )
        }
    }
}