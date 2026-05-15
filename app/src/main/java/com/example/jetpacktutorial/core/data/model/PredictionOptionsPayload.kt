package com.example.jetpacktutorial.core.data.model

data class PredictionOptionsPayload(
    val matchId: String,
    val team1Name: String,
    val team2Name: String,
    val topScorerOptions: List<PlayerPredictionOption>,
    val topBowlerOptions: List<PlayerPredictionOption>
)