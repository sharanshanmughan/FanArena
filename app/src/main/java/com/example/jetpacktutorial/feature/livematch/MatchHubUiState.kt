package com.example.jetpacktutorial.feature.livematch

import com.example.jetpacktutorial.core.data.model.MatchHubDetails

sealed interface MatchHubUiState {
    object Loading : MatchHubUiState
    data class Success(val data: MatchHubDetails) : MatchHubUiState
    data class Error(val message: String) : MatchHubUiState
}