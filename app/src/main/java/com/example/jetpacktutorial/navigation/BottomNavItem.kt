package com.example.jetpacktutorial.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    data object Home : BottomNavItem(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    data object Match : BottomNavItem(
        route = "match",
        title = "Match Hub",
        icon = Icons.Filled.Face
    )

    data object Feed : BottomNavItem(
        route = "feed",
        title = "Feed",
        icon = Icons.Filled.DateRange
    )

    data object Leaderboard : BottomNavItem(
        route = "leaderboard",
        title = "Rank",
        icon = Icons.Default.Star
    )

    data object Team : BottomNavItem(
        route = "team",
        title = "Teams",
        icon = Icons.Default.Person
    )
}