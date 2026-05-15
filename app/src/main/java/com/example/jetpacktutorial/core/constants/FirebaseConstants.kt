package com.example.jetpacktutorial.core.constants

object FirebaseConstants {

    // ── Firestore Collections ─────────────────────────────────────────────────
    const val USERS_COLLECTION                 = "users"
    const val MATCHES_COLLECTION               = "matches"
    const val PREDICTIONS_COLLECTION           = "predictions"
    const val PREDICTION_RESULTS_COLLECTION    = "predictionResults"
    const val POLL_VOTES_COLLECTION            = "pollVotes"
    const val LEADERBOARD_GLOBAL_COLLECTION    = "leaderboard_global"
    const val LEADERBOARD_WEEKLY_COLLECTION    = "leaderboard_weekly"
    const val LEADERBOARD_MATCH_COLLECTION     = "leaderboard_match"
    const val BADGES_COLLECTION                = "badges"
    const val NOTIFICATIONS_COLLECTION         = "notifications"

    // ── Realtime Database Nodes ───────────────────────────────────────────────
    const val LIVE_SCORES_NODE   = "liveScores"
    const val MATCH_EVENTS_NODE  = "matchEvents"
    const val POLLS_NODE         = "polls"
    const val REACTIONS_NODE     = "reactions"
    const val MOMENTUM_NODE      = "momentum"

    // ── FCM Topics ────────────────────────────────────────────────────────────
    const val TOPIC_ALL_MATCHES  = "all_matches"
    const val TOPIC_LIVE_ALERTS  = "live_alerts"
    fun matchTopic(matchId: String) = "match_$matchId"
}
