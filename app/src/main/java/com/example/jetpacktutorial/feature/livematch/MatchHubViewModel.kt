package com.example.jetpacktutorial.feature.livematch

import dagger.hilt.android.lifecycle.HiltViewModel




import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.repository.MatchHubRepository

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MatchHubViewModel @Inject constructor(
    repository: MatchHubRepository ,

) : ViewModel() {
    val matchId: String = "M_RCB_KKR_01"
    val uiState: StateFlow<MatchHubUiState> = repository.getMatchHubDetails(matchId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MatchHubUiState.Loading
        )
}