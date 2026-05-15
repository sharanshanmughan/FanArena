package com.example.jetpacktutorial.feature.home

import com.example.jetpacktutorial.core.data.model.FanPoll
import com.example.jetpacktutorial.core.data.model.LeaderboardUser
import com.example.jetpacktutorial.core.data.model.Match
import com.example.jetpacktutorial.core.data.model.TrendingPrediction


sealed interface HomeUiState {
    object Loading : HomeUiState

    data class Success(
        val todayMatches: List<Match>,
        val trendingPredictions: List<TrendingPrediction>,
        val fanPolls: List<FanPoll>,
        val topUsers: List<LeaderboardUser>
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
