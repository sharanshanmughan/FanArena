package com.example.jetpacktutorial.feature.leaderboard

import com.example.jetpacktutorial.core.data.model.LeaderboardPayload

sealed interface LeaderboardUiState {
    object Loading : LeaderboardUiState
    data class Success(val payload: LeaderboardPayload) : LeaderboardUiState
    data class Error(val message: String) : LeaderboardUiState
}