package com.mirz.handwriting.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mirz.handwriting.R
import com.mirz.handwriting.ui.theme.Purple40


@Composable
fun BottomNav(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile
    )
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Purple40
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->

            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp
                    )
                },
                selectedContentColor = Purple40,
                unselectedContentColor = Color(0xFF5C657C),
                alwaysShowLabel = true,
                selected = item.route == currentDestination?.route,
                onClick = {
                    navController.navigate(item.route)
                }
            )
        }
    }
}
