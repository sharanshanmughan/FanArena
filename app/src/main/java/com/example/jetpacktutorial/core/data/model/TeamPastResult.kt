package com.example.jetpacktutorial.core.data.model

data class TeamPastResult(
    val opponentShortCode: String,
    val resultMessage: String, // e.g., "Won by 7 Wickets"
    val isWinner: Boolean
)