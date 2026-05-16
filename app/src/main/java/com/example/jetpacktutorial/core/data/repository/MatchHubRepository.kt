package com.example.jetpacktutorial.core.data.repository


import com.example.jetpacktutorial.core.data.model.Comment
import com.example.jetpacktutorial.core.data.model.MatchHubDetails
import com.example.jetpacktutorial.feature.livematch.MatchHubUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class MatchHubRepository @Inject constructor() {
    fun getMatchHubDetails(matchId: String): Flow<MatchHubUiState> = flow {
        emit(MatchHubUiState.Loading)
        try {
            delay(1000) // Network simulation

            // Set countdown target to exactly 2 hours, 30 minutes, 15 seconds from now
            val currentEpoch = System.currentTimeMillis()
            val targetTime = currentEpoch + (2 * 3600 + 30 * 60 + 15) * 1000

            val mockDetails = MatchHubDetails(
                matchId = matchId,
                team1Name = "RCB",
                team1Logo = "rcb_logo",
                team2Name = "KKR",
                team2Logo = "kkr_logo",
                matchStartTimeMillis = targetTime,
                quickPolls = emptyList(),
                discussionComments = listOf(
                    Comment(
                        "c1",
                        "KingKohli_Fan",
                        "av_1",
                        "Kohli scoring a century tonight at Chinnaswamy! 💥",
                        "2m ago",
                        "RCB"
                    ),
                    Comment(
                        "c2",
                        "KnightRider99",
                        "av_2",
                        "Narines bowling economy is going to bottleneck their powerplay.",
                        "5m ago",
                        "KKR"
                    )
                )
            )
            emit(MatchHubUiState.Success(mockDetails))
        } catch (e: IOException) {
            emit(MatchHubUiState.Error("Failed to sync Match Hub arena data."))
        }
    }
}