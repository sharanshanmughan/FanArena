package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.ArenaMasterProfile
import com.example.jetpacktutorial.feature.arenaMasters.TopMastersUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class TopMastersRepository @Inject constructor(){
    fun getTopArenaMasters(): Flow<TopMastersUiState> = flow {
        emit(TopMastersUiState.Loading)
        try {
            delay(1000) // Simulating calculations for master tier indices

            val mockMasters = listOf(
                ArenaMasterProfile(
                    masterId = "m_01",
                    username = "CricketOracle",
                    overallRank = 1,
                    totalPoints = 89450,
                    winAccuracyPercentage = 87,
                    favoriteTeamToken = "RCB",
                    specialTitle = "Boundary Prophet 👑",
                    avatarUrl = "av_oracle"
                ),
                ArenaMasterProfile(
                    masterId = "m_02",
                    username = "ThalaTactics",
                    overallRank = 2,
                    totalPoints = 86210,
                    winAccuracyPercentage = 82,
                    favoriteTeamToken = "CSK",
                    specialTitle = "Toss Wizard 🪄",
                    avatarUrl = "av_thala"
                ),
                ArenaMasterProfile(
                    masterId = "m_03",
                    username = "HitmanStats",
                    overallRank = 3,
                    totalPoints = 84900,
                    winAccuracyPercentage = 79,
                    favoriteTeamToken = "MI",
                    specialTitle = "Sixes Tactician 💥",
                    avatarUrl = "av_hitman"
                )
            )
            emit(TopMastersUiState.Success(mockMasters))
        } catch (e: IOException) {
            emit(TopMastersUiState.Error("Failed to sync the elite Arena Masters ledger."))
        }
    }
}