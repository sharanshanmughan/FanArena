package com.example.jetpacktutorial.feature.leaderboard

import com.example.jetpacktutorial.core.data.model.LeaderboardType
import com.example.jetpacktutorial.core.data.model.LeaderboardUser

data class LeaderboardUiState(
    val isLoading: Boolean                = false,
    val selectedType: LeaderboardType     = LeaderboardType.GLOBAL,
    val entries: List<LeaderboardUser>    = emptyList(),
    val myEntry: LeaderboardUser?         = null,
    val error: String?                    = null
)

sealed class LeaderboardEvent {
    data class TabSelected(val type: LeaderboardType) : LeaderboardEvent()
    data object Refresh : LeaderboardEvent()
}
