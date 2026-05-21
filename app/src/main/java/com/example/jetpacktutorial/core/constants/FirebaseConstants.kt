package com.example.jetpacktutorial.core.constants

object FirebaseConstants {

    // ── Firestore Collections ─────────────────────────────────────────────────
    const val USERS_COLLECTION                 = "users"
    const val MATCHES_COLLECTION               = "matches"
    const val PREDICTIONS_COLLECTION           = "predictions"
    const val TRENDING_PREDICTIONS_COLLECTION  = "trending_predictions"
    const val PREDICTION_RESULTS_COLLECTION    = "predictionResults"
    const val POLL_VOTES_COLLECTION            = "pollVotes"
    const val LEADERBOARD_GLOBAL_COLLECTION    = "leaderboard_global"
    const val LEADERBOARD_WEEKLY_COLLECTION    = "leaderboard_weekly"
    const val LEADERBOARD_MATCH_COLLECTION     = "leaderboard_match"
    const val BADGES_COLLECTION                = "badges"
    const val NOTIFICATIONS_COLLECTION         = "notifications"

    // ── Home screen queries ─────────────────────────────────────────────────────
    const val MATCH_FIELD_SHOW_ON_HOME = "showOnHome"
    const val MATCH_FIELD_SORT_ORDER   = "sortOrder"
    const val HOME_MATCHES_LIMIT       = 5L
    const val HOME_TOP_USERS_LIMIT     = 3L
    const val TODAY_MATCHES_LIMIT      = 20L

    // ── Trending predictions ────────────────────────────────────────────────────
    const val TRENDING_FIELD_IS_ACTIVE = "isActive"
    const val TRENDING_FIELD_SORT_ORDER = "sortOrder"
    const val TRENDING_PREDICTIONS_LIMIT = 20L

    // ── Fan polls ─────────────────────────────────────────────────────────────
    const val FAN_POLLS_COLLECTION       = "fan_polls"
    const val FAN_POLL_FIELD_IS_ACTIVE   = "isActive"
    const val FAN_POLL_FIELD_SORT_ORDER  = "sortOrder"
    const val FAN_POLLS_LIMIT            = 30L

    // ── Match hub live discussion ───────────────────────────────────────────────
    const val MATCH_DISCUSSIONS_COLLECTION = "match_discussions"
    const val DISCUSSION_COMMENTS_SUBCOLLECTION = "comments"
    const val DISCUSSION_FIELD_CREATED_AT = "createdAt"
    const val DISCUSSION_COMMENTS_LIMIT = 100L
    const val DISCUSSION_MAX_TEXT_LENGTH = 500

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
