package com.example.jetpacktutorial.feature.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.model.LeaderboardTab
import com.example.jetpacktutorial.core.data.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    repository: LeaderboardRepository
) : ViewModel() {

    private val _currentTab = MutableStateFlow(LeaderboardTab.WEEKLY)
    val currentTab: StateFlow<LeaderboardTab> = _currentTab

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<LeaderboardUiState> = _currentTab
        .flatMapLatest { tab -> repository.getRankings(tab) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LeaderboardUiState.Loading
        )

    fun switchTab(tab: LeaderboardTab) {
        viewModelScope.launch {
            _currentTab.value = tab
        }
    }
}