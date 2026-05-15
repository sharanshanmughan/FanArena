package com.example.jetpacktutorial.core.data.model

import com.google.firebase.database.IgnoreExtraProperties

// ── ReactionType.kt ───────────────────────────────────────────────────────────

enum class ReactionType(val emoji: String, val label: String) {
    FIRE("🔥", "Fire"),
    GOAT("🐐", "GOAT"),
    CRYING("😭", "Crying"),
    SHOCKED("😱", "Shocked"),
    LOVE("❤️", "Love"),
    CLAP("👏", "Applause"),
    ANGRY("😤", "Angry"),
    LAUGHING("😂", "LOL"),
    HUNDRED("💯", "100"),
    SKULL("💀", "Dead")
}

// ── Reaction.kt ───────────────────────────────────────────────────────────────

@IgnoreExtraProperties
data class Reaction(
    val id: String = "",
    val userId: String = "",
    val matchId: String = "",
    val eventId: String = "",           // tied to a MatchEvent, or "" for general
    val type: ReactionType = ReactionType.FIRE,
    val timestamp: Long = System.currentTimeMillis(),
    val x: Float = 0f,                 // 0-1 relative position on screen (for floating animation)
    val y: Float = 0f
)

// ── Aggregated counts stored in RTDB per match ────────────────────────────────

@IgnoreExtraProperties
data class ReactionCount(
    val matchId: String = "",
    val fire: Int = 0,
    val goat: Int = 0,
    val crying: Int = 0,
    val shocked: Int = 0,
    val love: Int = 0,
    val clap: Int = 0,
    val angry: Int = 0,
    val laughing: Int = 0,
    val hundred: Int = 0,
    val skull: Int = 0
) {
    fun countFor(type: ReactionType): Int = when (type) {
        ReactionType.FIRE     -> fire
        ReactionType.GOAT     -> goat
        ReactionType.CRYING   -> crying
        ReactionType.SHOCKED  -> shocked
        ReactionType.LOVE     -> love
        ReactionType.CLAP     -> clap
        ReactionType.ANGRY    -> angry
        ReactionType.LAUGHING -> laughing
        ReactionType.HUNDRED  -> hundred
        ReactionType.SKULL    -> skull
    }

    val total: Int
        get() = fire + goat + crying + shocked + love + clap + angry + laughing + hundred + skull
}

// ── MemeReaction.kt ───────────────────────────────────────────────────────────

data class MemeReaction(
    val id: String = "",
    val userId: String = "",
    val matchId: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val likes: Int = 0,
    val postedAt: Long = System.currentTimeMillis(),
    val isApproved: Boolean = false     // moderation gate
)
