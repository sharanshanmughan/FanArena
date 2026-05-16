package com.example.jetpacktutorial.core.data.model

import java.io.Serializable

data class PredictionInsightCard(
    val predictionId: String,
    val category: String,
    val question: String,
    val option1Name: String,
    val option1Percentage: Int,
    val option2Name: String,
    val option2Percentage: Int,
    val totalVotersCount: String,
    val trendingTag: String? = null,
    /** Links to full match prediction slip when non-null. */
    val matchId: String? = null,
    val userSelectedOptionIndex: Int? = null,
) : Serializable