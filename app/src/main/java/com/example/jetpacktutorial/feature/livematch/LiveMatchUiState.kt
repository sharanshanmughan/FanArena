package com.example.jetpacktutorial.feature.livematch

import com.example.jetpacktutorial.core.data.model.LiveScore
import com.example.jetpacktutorial.core.data.model.Match
import com.example.jetpacktutorial.core.data.model.MatchEvent
import com.example.jetpacktutorial.core.data.model.LivePoll
import com.example.jetpacktutorial.core.data.model.PollVote
import com.example.jetpacktutorial.core.data.model.Prediction

// ── UI State ─────────────────────────────────────────────────────────────────

data class LiveMatchUiState(
    val isLoading: Boolean               = false,
    val match: Match?                    = null,
    val liveScore: LiveScore?            = null,
    val matchEvents: List<MatchEvent>    = emptyList(),
    val polls: List<LivePoll>            = emptyList(),
    val userPredictions: List<Prediction> = emptyList(),
    val isPredicting: Boolean            = false,
    val predictionSuccess: Boolean       = false,
    val error: String?                   = null
)

// ── User-triggered events ─────────────────────────────────────────────────────

sealed class LiveMatchEvent {
    data class SubmitPrediction(val prediction: Prediction) : LiveMatchEvent()
    data class CastPollVote(val vote: PollVote)             : LiveMatchEvent()
    data object DismissError                                : LiveMatchEvent()
}
