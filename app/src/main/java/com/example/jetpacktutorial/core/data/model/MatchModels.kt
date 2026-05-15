package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.DocumentId

// ── MatchStatus.kt ────────────────────────────────────────────────────────────

enum class MatchStatus {
    UPCOMING,
    LIVE,
    COMPLETED,
    POSTPONED,
    CANCELLED
}

// ── Team.kt ───────────────────────────────────────────────────────────────────

data class Team(
    val id: String = "",
    val name: String = "",
    val shortName: String = "",
    val logoUrl: String = "",
    val primaryColor: String = "#000000",
    val secondaryColor: String = "#FFFFFF",
    val city: String = "",
    val country: String = ""
)

// ── Match.kt ──────────────────────────────────────────────────────────────────

data class Match(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val sport: String = "",              // "cricket", "football", etc.
    val matchType: String = "",          // "T20", "ODI", "Test", "League"
    val teamA: Team = Team(),
    val teamB: Team = Team(),
    val venue: String = "",
    val city: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val status: MatchStatus = MatchStatus.UPCOMING,
    val predictionDeadline: Long = 0L,  // epoch ms – predictions lock at this time
    val streamUrl: String = "",
    val thumbnailUrl: String = "",
    val totalPredictions: Int = 0,
    val totalViewers: Int = 0,
    val isHighlighted: Boolean = false,
    val tournamentId: String = "",
    val tournamentName: String = "",
    val matchweek: Int = 0
) {
    val isLive: Boolean get() = status == MatchStatus.LIVE
    val isUpcoming: Boolean get() = status == MatchStatus.UPCOMING
    val isCompleted: Boolean get() = status == MatchStatus.COMPLETED
    val isPredictionOpen: Boolean
        get() = System.currentTimeMillis() < predictionDeadline && isUpcoming
}

// ── LiveScore.kt ──────────────────────────────────────────────────────────────

data class LiveScore(
    val matchId: String = "",
    val teamAScore: String = "",        // "245/6 (38.4 ov)" or "2"
    val teamBScore: String = "",
    val currentInning: Int = 1,
    val currentOver: String = "",       // "38.4" for cricket
    val requiredRunRate: Float = 0f,
    val currentRunRate: Float = 0f,
    val lastEvent: String = "",         // "FOUR by Kohli"
    val status: String = "",            // "India need 48 off 68 balls"
    val updatedAt: Long = System.currentTimeMillis()
)

// ── MatchEvent.kt ─────────────────────────────────────────────────────────────

data class MatchEvent(
    val id: String = "",
    val matchId: String = "",
    val type: MatchEventType = MatchEventType.GENERAL,
    val title: String = "",             // "WICKET!", "GOAL!", "FOUR"
    val description: String = "",
    val teamId: String = "",
    val playerName: String = "",
    val minute: Int = 0,               // football minute or cricket over
    val timestamp: Long = System.currentTimeMillis(),
    val iconEmoji: String = ""
)

enum class MatchEventType {
    GOAL,
    WICKET,
    BOUNDARY_FOUR,
    BOUNDARY_SIX,
    PENALTY,
    RED_CARD,
    YELLOW_CARD,
    SUBSTITUTION,
    HALF_TIME,
    FULL_TIME,
    OVER_COMPLETE,
    DRINKS_BREAK,
    GENERAL
}

// ── MomentumData.kt ───────────────────────────────────────────────────────────

data class MomentumData(
    val matchId: String = "",
    val dataPoints: List<MomentumPoint> = emptyList(),
    val currentMomentumTeamId: String = ""
)

data class MomentumPoint(
    val over: Float = 0f,              // cricket over or football minute
    val teamAScore: Float = 0f,        // normalized 0-100
    val teamBScore: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)
