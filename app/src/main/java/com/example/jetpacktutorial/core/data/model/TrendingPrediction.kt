package com.example.jetpacktutorial.core.data.model

data class TrendingPrediction(
    val id: String,
    val category: String, // e.g., "Match Winner", "Top Scorer"
    val prediction: String,
    val imageUrl: String
)
