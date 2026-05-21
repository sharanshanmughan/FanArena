package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.MatchHubDetails
import com.example.jetpacktutorial.feature.livematch.MatchHubUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MatchHubRepository @Inject constructor() {
    fun getMatchHubDetails(matchId: String): Flow<MatchHubUiState> = flow {
        emit(MatchHubUiState.Loading)
        try {
            val currentEpoch = System.currentTimeMillis()
            val targetTime = currentEpoch + (2 * 3600 + 30 * 60 + 15) * 1000

            val details = MatchHubDetails(
                matchId = matchId,
                team1Name = "RCB",
                team1Logo = "rcb_logo",
                team2Name = "KKR",
                team2Logo = "kkr_logo",
                matchStartTimeMillis = targetTime,
            )
            emit(MatchHubUiState.Success(details))
        } catch (e: Exception) {
            emit(MatchHubUiState.Error("Failed to sync Match Hub arena data."))
        }
    }
}
