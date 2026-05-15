package com.example.jetpacktutorial.feature.teams

import com.example.jetpacktutorial.core.data.model.IplTeamCard

sealed interface TeamsUiState {
    object Loading : TeamsUiState
    data class Success(val teamsList: List<IplTeamCard>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}