package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.DocumentId

// ── BadgeType.kt ──────────────────────────────────────────────────────────────

enum class BadgeType {
    // Prediction-based
    PREDICTION_NOVICE,          // First correct prediction
    PREDICTION_PRO,             // 10 correct predictions
    PREDICTION_MASTER,          // 50 correct predictions
    PERFECT_WEEK,               // 100% accuracy in a week
    ORACLE,                     // Predicted a major upset correctly

    // Streak-based
    STREAK_3,                   // 3-day login streak
    STREAK_7,                   // 7-day streak
    STREAK_30,                  // 30-day streak

    // Engagement-based
    POLL_VOTER,                 // Voted in 10 polls
    REACTION_KING,              // 50 reactions sent
    MATCH_WATCHER,              // Watched 5 live matches

    // Social
    FAN_FAVORITE,               // Reached top 100 on leaderboard
    CHAMPION,                   // Reached #1 on weekly leaderboard
    LOYAL_FAN,                  // Supported same team for 1 month

    // Special / Event
    EARLY_ADOPTER,              // Joined in first month
    TOURNAMENT_WINNER,          // Topped a tournament leaderboard
    CUSTOM                      // Admin-issued badge
}

// ── Badge.kt ──────────────────────────────────────────────────────────────────

data class Badge(
    @DocumentId
    val id: String = "",
    val type: BadgeType = BadgeType.PREDICTION_NOVICE,
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val lottieUrl: String = "",         // celebratory animation on earn
    val xpReward: Int = 0,
    val pointsReward: Int = 0,
    val rarity: BadgeRarity = BadgeRarity.COMMON,
    val isSecret: Boolean = false,      // hidden until earned
    val requiredCount: Int = 1,         // threshold to unlock
    val category: String = ""
)

enum class BadgeRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}

// ── Achievement.kt ────────────────────────────────────────────────────────────

data class Achievement(
    val id: String = "",
    val userId: String = "",
    val badge: Badge = Badge(),
    val progress: Int = 0,              // current progress toward requiredCount
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L,
    val isNew: Boolean = true,          // show celebration once, then mark false
    val notified: Boolean = false
) {
    val progressPercent: Float
        get() = if (badge.requiredCount > 0)
            (progress.toFloat() / badge.requiredCount).coerceIn(0f, 1f)
        else if (isUnlocked) 1f else 0f
}
