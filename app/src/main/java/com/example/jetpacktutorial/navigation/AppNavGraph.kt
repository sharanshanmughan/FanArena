package com.example.jetpacktutorial.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpacktutorial.feature.arenaMasters.TopMastersScreen
import com.example.jetpacktutorial.feature.auth.LoginScreen
import com.example.jetpacktutorial.feature.dashboard.DashboardScreen
import com.example.jetpacktutorial.feature.fanPoll.FanPollsScreen
import com.example.jetpacktutorial.feature.prediction.PredictionScreen
import com.example.jetpacktutorial.feature.spash.SplashScreen
import com.example.jetpacktutorial.feature.teams.teamProfile.TeamProfileScreen
import com.example.jetpacktutorial.feature.todayMatches.TodayMatchesScreen
import com.example.jetpacktutorial.feature.trendingPrediction.TrendingPredictionsScreen

@Composable
fun AppNavGraph() {

    val navController =
        rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {

        composable(
            Routes.Splash.route
        ) {

            SplashScreen(

            ) {
                navController.navigate(Routes.Login.route)
            }
        }

        composable(
            Routes.Login.route
        ) {

            LoginScreen(

            ) {

                navController.navigate(Routes.Dashboard.route)
            }
        }

        composable(
            Routes.Dashboard.route + "?tab={tab}",
            listOf(navArgument("tab") {
                type = NavType.StringType
                defaultValue = BottomNavItem.Home.route
            })
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab")
            DashboardScreen(
                tab,
                onNavigateToPredict = { matchId ->
                    navController.navigate(Routes.Prediction.createRoute(matchId))
                },
                onNavigateToTodayMatch = {
                    navController.navigate(Routes.TodayMatches.route)
                },
                onNavigateToTrendingPrediction = {
                    navController.navigate(Routes.TrendingPrediction.route)
                },
                onNavigateToFanPoll = {
                    navController.navigate(Routes.FanPoll.route)
                },
                onNavigateTopMasters = {
                    navController.navigate(Routes.TopMasters.route)
                },
                onNavigateToTeamProfile = { teamId ->
                    navController.navigate(Routes.TeamProfile.createRoute(teamId))
                },
            )
        }

        composable(
            route = Routes.Prediction.route,
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType },
            ),
        ) {
            PredictionScreen(
                onBackClicked = { navController.popBackStack() },
                onSubmitComplete = { navController.popBackStack() },
            )
        }

        composable(Routes.TodayMatches.route) {
            TodayMatchesScreen(
                onBackClicked = { navController.popBackStack() },
                onMatchSelected = { matchId ->
                    navController.navigate(Routes.Prediction.createRoute(matchId))
                },
            )
        }

        composable(Routes.TrendingPrediction.route) {
            TrendingPredictionsScreen(
                onBackClicked = { navController.popBackStack() },
                onOpenMatchPrediction = { matchId ->
                    navController.navigate(Routes.Prediction.createRoute(matchId))
                },
            )
        }

        composable(Routes.FanPoll.route) {
            FanPollsScreen(onBackClicked = { navController.popBackStack() })
        }

        composable(Routes.TopMasters.route) {
            TopMastersScreen(onProfileClicked = {})
        }

        composable(
            route = Routes.TeamProfile.route, // Evaluates cleanly to "team_profile/{team_id}"
            arguments = listOf(
                navArgument("teamId") { // Match the underscore notation from your Routes configuration
                    type = NavType.StringType
                }
            )
        ) {
            // Render your production decoupled profile screen component structure
            TeamProfileScreen(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }


    }
}