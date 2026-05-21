package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

/** Firestore document in [com.example.jetpacktutorial.core.constants.FirebaseConstants.TRENDING_PREDICTIONS_COLLECTION]. */
@IgnoreExtraProperties
data class TrendingPredictionDocument(
    val category: String = "",
    val question: String = "",
    val option1Name: String = "",
    val option2Name: String = "",
    val voteCount1: Long = 0L,
    val voteCount2: Long = 0L,
    val trendingTag: String? = null,
    val matchId: String? = null,
    val sortOrder: Long = 0L,
    val isActive: Boolean = true,
)

fun TrendingPredictionDocument.toDefinition(predictionId: String): TrendingPredictionDefinition =
    TrendingPredictionDefinition(
        predictionId = predictionId,
        category = category,
        question = question,
        option1Name = option1Name,
        option2Name = option2Name,
        initialVoteCounts = listOf(voteCount1.toInt(), voteCount2.toInt()),
        trendingTag = trendingTag,
        matchId = matchId?.takeIf { it.isNotBlank() },
    )
