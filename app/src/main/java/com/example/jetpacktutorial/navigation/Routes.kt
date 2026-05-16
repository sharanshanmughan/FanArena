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

    data object FanPoll :
        Routes("fan_poll")

    data object Leaderboard :
        Routes("leaderboard")

    data object TeamProfile :
        Routes("team_profile/{teamId}") {
            fun createRoute(teamId: String) = "team_profile/$teamId"
        }


    data object Profile : Routes("profile")

    data object TopMasters : Routes("top_masters")

    data object Prediction : Routes("prediction/{matchId}") {
        fun createRoute(matchId: String) = "prediction/$matchId"
    }

    data object TodayMatches : Routes("today_matches")

    data object TrendingPrediction : Routes("trending_prediction")
}