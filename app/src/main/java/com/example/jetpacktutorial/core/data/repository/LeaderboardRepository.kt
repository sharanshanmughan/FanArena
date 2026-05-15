package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.ArenaRankedUser
import com.example.jetpacktutorial.core.data.model.LeaderboardPayload
import com.example.jetpacktutorial.core.data.model.LeaderboardTab
import com.example.jetpacktutorial.feature.leaderboard.LeaderboardUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class LeaderboardRepository @Inject constructor() {
    fun getRankings(tab: LeaderboardTab): Flow<LeaderboardUiState> = flow {
        emit(LeaderboardUiState.Loading)
        try {
            delay(900) // Simulating network latency parsing regional global indexes

            val mockData = when (tab) {
                LeaderboardTab.WEEKLY -> listOf(
                    ArenaRankedUser(1, "CricketGuru", "av_1", 2450, "Champion", "#FF9800"),
                    ArenaRankedUser(2, "Hitman_Fan", "av_2", 2310, "Legend", "#00E5FF"),
                    ArenaRankedUser(3, "Thala_07", "av_3", 2290, "Legend", "#E040FB"),
                    ArenaRankedUser(4, "SkyWalker", "av_4", 2120, "Elite", "#4CAF50"),
                    ArenaRankedUser(5, "GoatKohli", "av_5", 1980, "Elite", "#4CAF50"),
                    ArenaRankedUser(6, "BoundaryRider", "av_6", 1850, "Pro", "#9E9E9E")
                )
                LeaderboardTab.SEASON -> listOf(
                    ArenaRankedUser(1, "Thala_07", "av_3", 48900, "Grandmaster", "#FF5722"),
                    ArenaRankedUser(2, "CricketGuru", "av_1", 46200, "Grandmaster", "#FF5722"),
                    ArenaRankedUser(3, "GoatKohli", "av_5", 44100, "Legend", "#00E5FF"),
                    ArenaRankedUser(4, "Hitman_Fan", "av_2", 42000, "Legend", "#00E5FF"),
                    ArenaRankedUser(5, "MysterySpinner", "av_7", 39150, "Elite", "#4CAF50")
                )
            }

            emit(LeaderboardUiState.Success(LeaderboardPayload(tab, mockData)))
        } catch (e: IOException) {
            emit(LeaderboardUiState.Error("Failed to sync arena leaderboards from engine nodes."))
        }
    }
}