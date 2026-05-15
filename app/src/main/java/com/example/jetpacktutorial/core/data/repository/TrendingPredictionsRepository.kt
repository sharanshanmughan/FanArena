package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.PredictionInsightCard
import com.example.jetpacktutorial.feature.trendingPrediction.TrendingPredictionsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class TrendingPredictionsRepository @Inject constructor() {
    fun getTrendingPredictions(filterCategory: String): Flow<TrendingPredictionsUiState> = flow {
        emit(TrendingPredictionsUiState.Loading)
        try {
            delay(800) // Network latency proxy

            val allInsights = listOf(
                PredictionInsightCard(
                    predictionId = "TP_01",
                    category = "Match Multiplier",
                    question = "Which team will score more than 70 runs in their Powerplay?",
                    option1Name = "SRH Arena",
                    option1Percentage = 74,
                    option2Name = "MI Camp",
                    option2Percentage = 26,
                    totalVotersCount = "89.1K Fans Voted",
                    trendingTag = "🔥 ACCELERATING"
                ),
                PredictionInsightCard(
                    predictionId = "TP_02",
                    category = "Player Performance",
                    question = "Who will pick up more wickets during death overs tonight?",
                    option1Name = "J. Bumrah",
                    option1Percentage = 61,
                    option2Name = "P. Cummins",
                    option2Percentage = 39,
                    totalVotersCount = "112K Fans Voted",
                    trendingTag = "👑 HEAD-TO-HEAD"
                ),
                PredictionInsightCard(
                    predictionId = "TP_03",
                    category = "Boundaries",
                    question = "Total match sixes boundary count estimation baseline:",
                    option1Name = "Over 15.5 Sixes",
                    option1Percentage = 55,
                    option2Name = "Under 15.5 Sixes",
                    option2Percentage = 45,
                    totalVotersCount = "34.2K Fans Voted"
                )
            )

            // Dynamic runtime filtering simulation
            val filteredList = if (filterCategory == "All") {
                allInsights
            } else {
                allInsights.filter { it.category == filterCategory }
            }

            emit(TrendingPredictionsUiState.Success(filteredList))
        } catch (e: IOException) {
            emit(TrendingPredictionsUiState.Error("Failed to sync centralized trending ledger matrices."))
        }
    }
}