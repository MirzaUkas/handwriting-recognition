package com.mirz.handwriting.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mirz.handwriting.navigation.BottomNav

@Composable
fun ProfileScreen(
    navController: NavController
) {

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
        Box(modifier = Modifier.padding(it)) {
            Text("PROFILE")
        }
    }

}