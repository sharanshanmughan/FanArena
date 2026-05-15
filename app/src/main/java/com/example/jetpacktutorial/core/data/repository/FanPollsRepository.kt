package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.InteractivePollCard
import com.example.jetpacktutorial.feature.fanPoll.FanPollsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class FanPollsRepository @Inject constructor() {
    fun getActiveFanPolls(): Flow<FanPollsUiState> = flow {
        emit(FanPollsUiState.Loading)
        try {
            delay(900) // Simulating network data node lookups

            val mockPolls = listOf(
                InteractivePollCard(
                    pollId = "FP_101",
                    question = "Will MSD push himself up the batting order during run chases this week?",
                    category = "Thala Corner",
                    optionsList = listOf(
                        "Yes, definitely needed",
                        "No, stay at No. 8",
                        "Depends on required run rate"
                    ),
                    voteDistribution = listOf(58, 14, 28),
                    totalVotesFormatted = "240K Votes",
                    userSelectedOptionIndex = null // Unvoted state
                ),
                InteractivePollCard(
                    pollId = "FP_102",
                    question = "Should RCB bench an overseas batsman to bring in an extra specialist death bowler?",
                    category = "Squad Strategy",
                    optionsList = listOf("Yes, bowling is leaking runs", "No, back the batting line-up"),
                    voteDistribution = listOf(72, 28),
                    totalVotesFormatted = "114K Votes",
                    userSelectedOptionIndex = 0 // User already voted for Option 0
                )
            )
            emit(FanPollsUiState.Success(mockPolls))
        } catch (e: IOException) {
            emit(FanPollsUiState.Error("Failed to update active fan poll streams."))
        }
    }
}