package com.example.jetpacktutorial.core.data.model

data class UserMatchPrediction(
    val matchId: String,
    val winnerTeam: String,
    val topScorerId: String,
    val topScorerName: String,
    val topBowlerId: String,
    val topBowlerName: String,
    val submittedAtMillis: Long = System.currentTimeMillis(),
) {
    fun summaryLine(): String = "$winnerTeam · $topScorerName · $topBowlerName"
}
