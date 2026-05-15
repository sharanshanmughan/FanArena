package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.DocumentId

// ── User.kt ──────────────────────────────────────────────────────────────────

data class User(
    @DocumentId
    val uid: String = "",
    val displayName: String = "Fan",
    val email: String = "",
    val photoUrl: String = "",
    val isGuest: Boolean = false,
    val favoriteTeamId: String = "",
    val favoriteTeamName: String = "",
    val totalPoints: Int = 0,
    val xp: Int = 0,
    val fanLevel: Int = 1,
    val globalRank: Int = 0,
    val weeklyRank: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
    val fcmToken: String = "",
    val notificationsEnabled: Boolean = true
)

// ── UserStats.kt ─────────────────────────────────────────────────────────────

data class UserStats(
    val userId: String = "",
    val totalPredictions: Int = 0,
    val correctPredictions: Int = 0,
    val totalPoints: Int = 0,
    val weeklyPoints: Int = 0,
    val matchesParticipated: Int = 0,
    val pollsVoted: Int = 0,
    val reactionsGiven: Int = 0,
    val badgesEarned: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
) {
    val predictionAccuracy: Float
        get() = if (totalPredictions > 0)
            (correctPredictions.toFloat() / totalPredictions) * 100f
        else 0f
}

// ── UserBadge.kt ─────────────────────────────────────────────────────────────

data class UserBadge(
    val badgeId: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val earnedAt: Long = System.currentTimeMillis(),
    val isNew: Boolean = true
)

// ── UserStreak.kt ─────────────────────────────────────────────────────────────

data class UserStreak(
    val userId: String = "",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActivityDate: String = "",   // "yyyy-MM-dd"
    val streakFreezeAvailable: Boolean = false,
    val streakFreezeUsedAt: Long = 0L
) {
    val isActive: Boolean get() = currentStreak > 0
}
