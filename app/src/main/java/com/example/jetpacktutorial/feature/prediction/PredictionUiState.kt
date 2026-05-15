package com.example.jetpacktutorial.feature.prediction

import com.example.jetpacktutorial.core.data.model.PredictionOptionsPayload

sealed interface PredictionUiState {
    object Loading : PredictionUiState
    data class Success(val options: PredictionOptionsPayload) : PredictionUiState
    data class Error(val message: String) : PredictionUiState
}