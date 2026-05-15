package com.example.jetpacktutorial.feature.home


import com.example.jetpacktutorial.core.common.Event
import com.example.jetpacktutorial.core.data.model.Match

// ── UI State ─────────────────────────────────────────────────────────────────

data class HomeUiState(
    val isLoading: Boolean       = false,
    val liveMatches: List<Match> = emptyList(),
    val upcomingMatches: List<Match> = emptyList(),
    val userRank: Int?           = null,
    val userPoints: Int          = 0,
    val error: String?           = null
)

// ── UI Events (user actions → ViewModel) ─────────────────────────────────────

sealed class HomeEvent {
    data object RefreshMatches            : HomeEvent()
    data class  MatchClicked(val match: Match) : HomeEvent()
    data object LeaderboardTabClicked     : HomeEvent()
    data class  NavigateToLiveMatch(val matchId: String) : HomeEvent()
    data class  NavigateToMatchDetails(val matchId: String) : HomeEvent()
}

// ── One-shot ViewModel → UI events ───────────────────────────────────────────

sealed class HomeUiEvent : Event() {
    data object NavigateToLeaderboard : HomeUiEvent()
}
