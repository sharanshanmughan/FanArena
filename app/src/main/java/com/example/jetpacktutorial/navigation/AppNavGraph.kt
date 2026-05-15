package com.example.jetpacktutorial.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktutorial.feature.auth.LoginScreen
import com.example.jetpacktutorial.feature.dashboard.DashboardScreen
import com.example.jetpacktutorial.feature.home.HomeScreen
import com.example.jetpacktutorial.feature.leaderboard.LeaderboardScreen
import com.example.jetpacktutorial.feature.livematch.MatchDetailsScreen
import com.example.jetpacktutorial.feature.profile.ProfileScreen
import com.example.jetpacktutorial.feature.spash.SplashScreen

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

            ){
                navController.navigate(Routes.Login.route)
            }
        }

        composable(
            Routes.Login.route
        ) {

            LoginScreen(

            ){

                navController.navigate(Routes.Dashboard.route)
            }
        }

        composable(
            Routes.Dashboard.route
        ) {

            DashboardScreen()
        }

        composable(
            route =
                Routes.MatchDetails.route,


        ) { backStackEntry ->



            MatchDetailsScreen()
        }


    }
}