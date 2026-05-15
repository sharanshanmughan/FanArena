package com.example.jetpacktutorial.feature.trendingPrediction

import com.example.jetpacktutorial.core.data.model.PredictionInsightCard

sealed interface TrendingPredictionsUiState {
    object Loading : TrendingPredictionsUiState
    data class Success(val insights: List<PredictionInsightCard>) : TrendingPredictionsUiState
    data class Error(val message: String) : TrendingPredictionsUiState
}