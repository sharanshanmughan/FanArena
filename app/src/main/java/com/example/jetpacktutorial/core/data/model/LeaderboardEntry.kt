package com.example.jetpacktutorial.core.data.model

/**
 * Firestore document shape for [FirebaseConstants.LEADERBOARD_GLOBAL_COLLECTION].
 * Rank is computed at read time from sort order (points descending).
 */
data class LeaderboardEntry(
    val username: String = "",
    val avatarUrl: String = "",
    val points: Int = 0,
)
