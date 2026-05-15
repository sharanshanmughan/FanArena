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
import com.example.jetpacktutorial.feature.todayMatches.TodayMatchesScreen
import com.example.jetpacktutorial.feature.trendingPrediction.TrendingPredictionsScreen

@Composable
fun AppNavGraph() {

    val navController =
        rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard.route
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
                onNavigateToPredict = {
                    navController.navigate(Routes.Prediction.route)
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
                }
            )
        }

        composable(Routes.Prediction.route) {
            PredictionScreen(onBackClicked = {}, onSubmitComplete = {})
        }

        composable(Routes.TodayMatches.route) {
            TodayMatchesScreen() {

            }
        }

        composable(Routes.TrendingPrediction.route) {
            TrendingPredictionsScreen(onPredictionCardClicked = {})
        }

        composable(Routes.FanPoll.route) {
            FanPollsScreen()
        }

        composable(Routes.TopMasters.route) {
            TopMastersScreen(onProfileClicked = {})
        }


    }
}