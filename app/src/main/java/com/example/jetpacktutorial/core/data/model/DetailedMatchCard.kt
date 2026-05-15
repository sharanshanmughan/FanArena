package com.example.jetpacktutorial.core.data.model

import java.io.Serializable

data class DetailedMatchCard(
    val matchId: String,
    val team1Code: String,
    val team1Name: String,
    val team2Code: String,
    val team2Name: String,
    val status: MatchStatus,
    val matchTimeOrScore: String,       // Displays "7:30 PM" if upcoming, or "184/3 (18.2)" if live
    val fanSupportPredictionRatio: Float // Percentage backing Team 1 (e.g., 0.62f for 62%)
) : Serializable