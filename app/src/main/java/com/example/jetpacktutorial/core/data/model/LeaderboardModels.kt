package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.DocumentId

// ── LeaderboardType.kt ────────────────────────────────────────────────────────

enum class LeaderboardType {
    GLOBAL,     // All-time across all matches
    WEEKLY,     // Resets every Monday
    MATCH       // Scoped to a single match
}

// ── RankMovement.kt ───────────────────────────────────────────────────────────

enum class RankMovement {
    UP,
    DOWN,
    SAME,
    NEW_ENTRY
}

// ── LeaderboardUser.kt ────────────────────────────────────────────────────────

data class LeaderboardUser(
    @DocumentId
    val userId: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val favoriteTeamId: String = "",
    val favoriteTeamName: String = "",
    val points: Int = 0,
    val xp: Int = 0,
    val rank: Int = 0,
    val previousRank: Int = 0,
    val predictionAccuracy: Float = 0f,
    val matchesPlayed: Int = 0,
    val fanLevel: Int = 1,
    val badgeCount: Int = 0,
    val matchId: String = "",           // populated for LeaderboardType.MATCH only
    val weekId: String = ""             // "2025-W21" format for LeaderboardType.WEEKLY
) {
    val rankMovement: RankMovement
        get() = when {
            previousRank == 0        -> RankMovement.NEW_ENTRY
            rank < previousRank      -> RankMovement.UP
            rank > previousRank      -> RankMovement.DOWN
            else                     -> RankMovement.SAME
        }

    val rankDelta: Int get() = previousRank - rank   // positive = moved up
}
