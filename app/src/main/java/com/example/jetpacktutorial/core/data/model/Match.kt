package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.DocumentId

data class Match(
    @DocumentId
    val id: String = "",
    val team1: String = "",
    val team1Logo: String = "",
    val team1Name: String = "",
    val team2: String = "",
    val team2Logo: String = "",
    val team2Name: String = "",
    val time: String = "",
    /** Display line for Today screen; falls back to "Starts at {time}" when empty. */
    val matchTimeOrScore: String = "",
    /** UPCOMING, LIVE, or COMPLETED */
    val status: String = "UPCOMING",
    /** When true, the match appears on the Home screen carousel. */
    val showOnHome: Boolean = true,
    val fanSupportPredictionRatio: Double = 0.5,
    /** Lower values appear first on Today's Matches. */
    val sortOrder: Int = 0,
)
