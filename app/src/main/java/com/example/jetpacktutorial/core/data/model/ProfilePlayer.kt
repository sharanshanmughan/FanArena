package com.example.jetpacktutorial.core.data.model

data class ProfilePlayer(
    val playerId: String,
    val name: String,
    val role: String, // "Batsman", "Bowler", "All-Rounder", "Wicketkeeper"
    val isCaptain: Boolean = false
)