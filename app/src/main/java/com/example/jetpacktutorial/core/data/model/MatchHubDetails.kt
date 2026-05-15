package com.example.jetpacktutorial.core.data.model

import java.io.Serializable

data class MatchHubDetails(
    val matchId: String,
    val team1Name: String,
    val team1Logo: String,
    val team2Name: String,
    val team2Logo: String,
    val matchStartTimeMillis: Long, // Epoch timestamp for the countdown timer
    val quickPolls: List<FanPoll>,
    val discussionComments: List<Comment>
) : Serializable
