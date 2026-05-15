package com.example.jetpacktutorial.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.repository.FanFeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FanFeedViewModel @Inject constructor(
    private val repository: FanFeedRepository
) : ViewModel() {

    val uiState: StateFlow<FanFeedUiState> = repository.getFanFeed()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FanFeedUiState.Loading
        )

    fun toggleLike(itemId: String) {
        // Handle database synchronization logic or telemetry event dispatch mapping
    }

    fun castFeedVote(itemId: String, optionIndex: Int) {
        // Register decentralized node fan polling mutations
    }
}