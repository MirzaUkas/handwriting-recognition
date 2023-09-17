package com.mirz.handwriting.navigation

import android.net.Uri
import androidx.navigation.NavController
import com.google.gson.Gson
import com.mirz.handwriting.domain.entities.QuestionEntity

object Screens {
    const val Home = "home"
    const val Splash = "splash"
    const val Login = "login"
    const val Lesson = "lessons"
    const val Question = "question"
}

fun NavController.navigateToQuestion(questionEntity: QuestionEntity) {
    val json = Uri.encode(Gson().toJson(questionEntity))
    navigate("${Screens.Question}/$json")
}