package com.example.jetpacktutorial.core.data.repository





import com.example.jetpacktutorial.core.data.model.FanPoll
import com.example.jetpacktutorial.core.data.model.LeaderboardUser
import com.example.jetpacktutorial.core.data.model.Match
import com.example.jetpacktutorial.core.data.model.TrendingPrediction
import com.example.jetpacktutorial.feature.home.HomeUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class HomeRepository @Inject constructor() {
    fun getHomeData(): Flow<HomeUiState> = flow {
        // 1. Immediately emit the Loading state to the UI
        emit(HomeUiState.Loading)

        try {
            // Simulate network latency (1.2 seconds)
            delay(1200)

            // Mocking successful data fetch
            val matches = listOf(
                Match("1", "RCB", "rcb_logo", "MI", "mi_logo", "7:30 PM"),
                Match("2", "CSK", "csk_logo", "KKR", "kkr_logo", "7:30 PM")
            )
            val predictions = listOf(
                TrendingPrediction("1", "Match Winner", "Mumbai Indians", "mi_flag"),
                TrendingPrediction("2", "Top Scorer", "Virat Kohli", "kohli_profile")
            )
            val polls = listOf(
                FanPoll(
                    "1",
                    "Will MSD play next season?",
                    45200,
                    listOf("Yes", "No", "Definitely Yes!")
                )
            )
            val topUsers = listOf(
                LeaderboardUser(1, "CricketGuru", "avatar_1", 2450),
                LeaderboardUser(2, "Hitman_Fan", "avatar_2", 2310),
                LeaderboardUser(3, "Thala_07", "avatar_3", 2290)
            )

            // 2. Emit Success state with the populated data payload
            emit(HomeUiState.Success(
                todayMatches = matches,
                trendingPredictions = predictions,
                fanPolls = polls,
                topUsers = topUsers
            ))

        } catch (e: IOException) {
            // 3. Emit Error state if an exception occurs (e.g., no internet connection)
            emit(HomeUiState.Error(message = "Failed to load Arena. Check your internet connection."))
        }
    }
}