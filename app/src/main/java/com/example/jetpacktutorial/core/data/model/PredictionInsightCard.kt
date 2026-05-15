package com.example.jetpacktutorial.core.data.model

import java.io.Serializable

data class PredictionInsightCard(
    val predictionId: String,
    val category: String,          // e.g., "Orange Cap", "Match Multiplier", "Powerplay"
    val question: String,          // e.g., "Who will hit the most sixes tonight?"
    val option1Name: String,       // e.g., "Abhishek Sharma"
    val option1Percentage: Int,    // e.g., 68
    val option2Name: String,       // e.g., "Andre Russell"
    val option2Percentage: Int,    // e.g., 32
    val totalVotersCount: String,  // e.g., "42.5K Fans Voted"
    val trendingTag: String? = null // e.g., "🔥 HOT"
) : Serializable