package com.mirz.handwriting.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.mirz.handwriting.R
import com.mirz.handwriting.ui.theme.HandwritingTheme


@Composable
fun SplashScreen(
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState

    Box(
        Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.il_splash),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth,
            contentDescription = "Splash Screen"
        )
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier.align(
                Alignment.Center
            ),
        )

    }

    LaunchedEffect(uiState.isLoggedIn) {
        uiState.isLoggedIn?.let {
            if (it) {
                navigateToHome()
            } else {
                navigateToLogin()
            }
        }

    }
}

@Preview
@Composable
fun SplashScreenPrev() {
    HandwritingTheme {
        SplashScreen(
            navigateToHome = {},
            navigateToLogin = {},
        )
    }
}