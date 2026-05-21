package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Firestore document shape for leaderboard_global.
 * Rank is computed at read time from sort order (points descending).
 */
@IgnoreExtraProperties
data class LeaderboardEntry(
    val username: String = "",
    val avatarUrl: String = "",
    val points: Long = 0L,
)
