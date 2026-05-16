package com.example.jetpacktutorial.feature.teams.teamProfile

import com.example.jetpacktutorial.core.data.model.TeamProfileDetails

sealed interface TeamProfileUiState {
    object Loading : TeamProfileUiState
    data class Success(val data: TeamProfileDetails) : TeamProfileUiState
    data class Error(val message: String) : TeamProfileUiState
}