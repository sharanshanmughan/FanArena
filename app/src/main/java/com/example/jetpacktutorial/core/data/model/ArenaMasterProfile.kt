package com.example.jetpacktutorial.core.data.model

import java.io.Serializable

data class ArenaMasterProfile(
    val masterId: String,
    val username: String,
    val overallRank: Int,
    val totalPoints: Int,
    val winAccuracyPercentage: Int, // e.g., 84% accuracy in match outcomes
    val favoriteTeamToken: String,  // e.g., "RCB", "CSK"
    val specialTitle: String,       // e.g., "Predictor King", "Toss Oracle"
    val avatarUrl: String
) : Serializable