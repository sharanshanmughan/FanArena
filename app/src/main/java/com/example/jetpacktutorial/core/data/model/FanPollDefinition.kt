package com.example.jetpacktutorial.core.data.model

/**
 * Source-of-truth poll template. Vote counts are mutated locally via [PollVotesRepository].
 */
data class FanPollDefinition(
    val pollId: String,
    val question: String,
    val category: String,
    val options: List<String>,
    val initialVoteCounts: List<Int>,
    val matchId: String? = null,
)
