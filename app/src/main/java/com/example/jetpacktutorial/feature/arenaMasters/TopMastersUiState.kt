package com.example.jetpacktutorial.feature.arenaMasters

import com.example.jetpacktutorial.core.data.model.ArenaMasterProfile

sealed interface TopMastersUiState {
    object Loading : TopMastersUiState
    data class Success(val masters: List<ArenaMasterProfile>) : TopMastersUiState
    data class Error(val message: String) : TopMastersUiState
}