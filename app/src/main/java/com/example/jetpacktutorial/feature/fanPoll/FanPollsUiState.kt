package com.example.jetpacktutorial.feature.fanPoll

import com.example.jetpacktutorial.core.data.model.InteractivePollCard

sealed interface FanPollsUiState {
    object Loading : FanPollsUiState
    data class Success(val activePolls: List<InteractivePollCard>) : FanPollsUiState
    data class Error(val message: String) : FanPollsUiState
}