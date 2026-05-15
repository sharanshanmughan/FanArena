package com.example.jetpacktutorial.core.data.model

data class PlayerPredictionOption(
    val id: String,
    val name: String,
    val role: String,       // e.g., "Batsman", "Bowler"
    val avatarUrl: String,
    val teamToken: String   // e.g., "RCB", "MI"
)
