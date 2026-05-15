package com.example.jetpacktutorial.feature.todayMatches

import com.example.jetpacktutorial.core.data.model.DetailedMatchCard

sealed interface TodayMatchesUiState {
    object Loading : TodayMatchesUiState
    data class Success(val matches: List<DetailedMatchCard>) : TodayMatchesUiState
    data class Error(val message: String) : TodayMatchesUiState
}