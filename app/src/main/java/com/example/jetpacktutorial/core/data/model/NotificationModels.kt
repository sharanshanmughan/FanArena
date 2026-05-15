package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.DocumentId

// ── NotificationType.kt ───────────────────────────────────────────────────────

enum class NotificationType {
    MATCH_STARTING_SOON,        // "Match starts in 30 min — lock your predictions!"
    MATCH_LIVE,                 // "Match is LIVE now!"
    PREDICTION_RESULT,          // "Your prediction was correct! +50 pts"
    LEADERBOARD_UPDATE,         // "You moved up to #12 🔥"
    BADGE_EARNED,               // "You earned the Oracle badge 🏆"
    STREAK_REMINDER,            // "Don't break your 7-day streak!"
    POLL_AVAILABLE,             // "New live poll — vote now!"
    MATCH_COMPLETED,            // "Match ended — see your results"
    RANK_CHANGED,               // "Someone overtook you! Time to predict more"
    SYSTEM                      // Generic app announcements
}

// ── AppNotification.kt ────────────────────────────────────────────────────────

data class AppNotification(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val type: NotificationType = NotificationType.SYSTEM,
    val title: String = "",
    val body: String = "",
    val imageUrl: String = "",
    val deepLink: String = "",          // e.g. "fanarena://match/matchId"
    val matchId: String = "",
    val badgeId: String = "",
    val pointsAwarded: Int = 0,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = 0L           // 0 = never expires
) {
    val isExpired: Boolean
        get() = expiresAt > 0 && System.currentTimeMillis() > expiresAt

    val isVisible: Boolean
        get() = !isExpired
}
