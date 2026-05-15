package com.example.jetpacktutorial.feature.home

import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.common.BaseViewModel
import com.example.jetpacktutorial.core.data.repository.MatchRepository
import com.example.jetpacktutorial.core.common.Resource
import com.example.jetpacktutorial.core.data.model.Match
import com.example.jetpacktutorial.core.data.repository.AuthRepository
import com.example.jetpacktutorial.core.data.repository.LeaderboardRepository
import com.example.jetpacktutorial.core.data.model.LeaderboardType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val leaderboardRepository: LeaderboardRepository,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeMatches()
        loadUserRank()
    }

    // ── Public event handler ─────────────────────────────────────────────────

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.RefreshMatches   -> observeMatches()
            is HomeEvent.MatchClicked     -> handleMatchClick(event.match)
            is HomeEvent.LeaderboardTabClicked -> navigateToLeaderboard()
            else -> {}
        }
    }

    // ── Private functions ────────────────────────────────────────────────────

    private fun observeMatches() {
        viewModelScope.launch {
            matchRepository.observeMatches()
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success -> {
                            val all = resource.data ?: emptyList()
                            _uiState.update {
                                it.copy(
                                    isLoading      = false,
                                    liveMatches    = all.filter { m -> m.isLive },
                                    upcomingMatches = all.filter { m -> !m.isLive },
                                    error          = null
                                )
                            }
                        }
                        is Resource.Error -> _uiState.update {
                            it.copy(isLoading = false, error = resource.message)
                        }
                    }
                }
        }
    }

    private fun loadUserRank() {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            leaderboardRepository.observeLeaderboard(LeaderboardType.GLOBAL, limit = 100)
                .collect { resource ->
                    if (resource is Resource.Success) {
                        val userEntry = resource.data?.firstOrNull { it.userId == userId }
                        _uiState.update { it.copy(userRank = userEntry?.rank, userPoints = userEntry?.points ?: 0) }
                    }
                }
        }
    }

    private fun handleMatchClick(match: Match) {
        viewModelScope.launch {
            val event = if (match.isLive)
                HomeEvent.NavigateToLiveMatch(match.id)
            else
                HomeEvent.NavigateToMatchDetails(match.id)
            sendEvent(event as com.example.jetpacktutorial.core.common.Event)
        }
    }

    private fun navigateToLeaderboard() {
        viewModelScope.launch {
            sendEvent(HomeUiEvent.NavigateToLeaderboard)
        }
    }
}
