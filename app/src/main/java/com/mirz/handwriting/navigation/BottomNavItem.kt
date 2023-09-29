package com.mirz.handwriting.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {

    object Home : BottomNavItem("Home", Icons.Rounded.Home, Screens.Home)
    object Profile : BottomNavItem("Profile", Icons.Rounded.PersonOutline, Screens.Profile)

}