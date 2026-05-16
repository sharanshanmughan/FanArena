package com.example.jetpacktutorial.core.data.model

/**
 * Source template for trending 2-way forecasts. Vote counts live in [TrendingVotesRepository].
 */
data class TrendingPredictionDefinition(
    val predictionId: String,
    val category: String,
    val question: String,
    val option1Name: String,
    val option2Name: String,
    val initialVoteCounts: List<Int>,
    val trendingTag: String? = null,
    /** When set, this trend links to the full match [PredictionScreen] for that fixture. */
    val matchId: String? = null,
)
