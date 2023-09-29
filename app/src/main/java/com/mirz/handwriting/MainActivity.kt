package com.mirz.handwriting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mirz.handwriting.navigation.MainNavGraph
import com.mirz.handwriting.ui.screens.splash.SplashViewModel
import com.mirz.handwriting.ui.theme.HandwritingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        installSplashScreen().setKeepOnScreenCondition {
//            viewModel.uiState.value.isLoggedIn == null
//        }
        setContent {
            HandwritingTheme {
                MainNavGraph()
            }
        }
    }
}



