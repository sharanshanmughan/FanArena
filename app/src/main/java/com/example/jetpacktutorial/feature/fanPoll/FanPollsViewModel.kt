package com.example.jetpacktutorial.feature.fanPoll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.repository.FanPollsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FanPollsViewModel @Inject constructor(
    private val repository: FanPollsRepository,
) : ViewModel() {

    val uiState: StateFlow<FanPollsUiState> = repository.observeArenaPolls()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FanPollsUiState.Loading,
        )

    fun submitPollVote(pollId: String, selectedOptionIndex: Int) {
        repository.submitVote(pollId, selectedOptionIndex)
    }
}
