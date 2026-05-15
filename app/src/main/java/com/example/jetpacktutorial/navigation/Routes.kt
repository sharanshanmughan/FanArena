package com.example.jetpacktutorial.navigation

sealed class Routes(
    val route: String
) {

    data object Splash :
        Routes("splash")

    data object Login :
        Routes("login")

    data object Dashboard :
        Routes("dashboard")

    data object Home :
        Routes("home")

    data object Leaderboard :
        Routes("leaderboard")

    data object Profile :
        Routes("profile")

    data object MatchDetails :
        Routes("match_details/{matchId}") {

        fun createRoute(
            matchId: String
        ) = "match_details/$matchId"
    }
}