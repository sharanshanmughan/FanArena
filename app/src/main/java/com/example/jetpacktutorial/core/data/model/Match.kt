package com.example.jetpacktutorial.core.data.model

data class Match(
    val id: String,
    val team1: String,
    val team1Logo: String,
    val team2: String,
    val team2Logo: String,
    val time: String,
    val status: String = "Upcoming"
)
