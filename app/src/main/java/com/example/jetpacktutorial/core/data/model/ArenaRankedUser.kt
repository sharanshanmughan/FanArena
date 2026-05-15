package com.example.jetpacktutorial.core.data.model

import java.io.Serializable

data class ArenaRankedUser(
    val rank: Int,
    val username: String,
    val avatarUrl: String,
    val points: Int,
    val badgeName: String,     // e.g., "Grandmaster", "Legend", "Pro"
    val badgeGlowColor: String  // Hex color code representing tier ranking tier status
) : Serializable