package com.example.jetpacktutorial.feature.home

import com.example.jetpacktutorial.core.data.model.LeaderboardUser
import com.example.jetpacktutorial.core.data.model.Match
sealed interface HomeUiState {
    object Loading : HomeUiState

    data class Success(
        val todayMatches: List<Match>,
        val topUsers: List<LeaderboardUser>,
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
