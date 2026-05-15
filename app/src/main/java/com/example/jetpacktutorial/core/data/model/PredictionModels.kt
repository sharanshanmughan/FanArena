package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.DocumentId

// ── PredictionType.kt ─────────────────────────────────────────────────────────

enum class PredictionType {
    MATCH_WINNER,           // Who wins the match?
    TOSS_WINNER,            // Who wins the toss?
    TOP_BATSMAN,            // Who scores the most runs?
    TOP_BOWLER,             // Who takes the most wickets?
    FIRST_GOAL_SCORER,      // Who scores first?
    TOTAL_GOALS,            // Over/Under goals?
    CORRECT_SCORE,          // Exact scoreline
    PLAYER_PERFORMANCE,     // Will player X score > N runs?
    BOTH_TEAMS_TO_SCORE,    // Yes / No
    CUSTOM                  // Freeform question from admin
}

// ── PredictionOption.kt ───────────────────────────────────────────────────────

data class PredictionOption(
    val id: String = "",
    val label: String = "",             // "India", "Over 2.5", "Yes"
    val iconUrl: String = "",
    val teamId: String = "",            // for team-based options
    val playerId: String = "",          // for player-based options
    val odds: Float = 1.0f,            // multiplier for bonus points
    val fanPickPercentage: Float = 0f   // live % of fans who chose this
)

// ── Prediction.kt ─────────────────────────────────────────────────────────────

data class Prediction(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val matchId: String = "",
    val questionId: String = "",        // unique ID for the prediction question
    val question: String = "",          // "Who will win the match?"
    val type: PredictionType = PredictionType.MATCH_WINNER,
    val options: List<PredictionOption> = emptyList(),
    val selectedOptionId: String = "",
    val isLocked: Boolean = false,
    val pointsAwarded: Int = 0,
    val isCorrect: Boolean = false,
    val isResolved: Boolean = false,
    val submittedAt: Long = System.currentTimeMillis(),
    val lockedAt: Long = 0L,
    val resolvedAt: Long = 0L
) {
    val selectedOption: PredictionOption?
        get() = options.firstOrNull { it.id == selectedOptionId }

    val canEdit: Boolean
        get() = !isLocked && !isResolved
}

// ── PredictionResult.kt ───────────────────────────────────────────────────────

data class PredictionResult(
    val id: String = "",
    val userId: String = "",
    val matchId: String = "",
    val questionId: String = "",
    val question: String = "",
    val correctOptionId: String = "",
    val userOptionId: String = "",
    val isCorrect: Boolean = false,
    val basePoints: Int = 0,
    val bonusPoints: Int = 0,
    val totalPoints: Int = 0,
    val resolvedAt: Long = System.currentTimeMillis()
) {
    val correctOption: String get() = correctOptionId
    val earnedPoints: Int get() = if (isCorrect) totalPoints else 0
}
