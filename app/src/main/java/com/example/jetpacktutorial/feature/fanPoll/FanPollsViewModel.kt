package com.example.jetpacktutorial.feature.fanPoll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.repository.FanPollsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FanPollsViewModel @Inject constructor(repository: FanPollsRepository) : ViewModel() {

    val uiState: StateFlow<FanPollsUiState> = repository.getActiveFanPolls()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FanPollsUiState.Loading
        )

    fun submitPollVote(pollId: String, selectedOptionIndex: Int) {
        viewModelScope.launch {
            // Fires mutation calls down to network nodes to increment telemetry databases
        }
    }
}