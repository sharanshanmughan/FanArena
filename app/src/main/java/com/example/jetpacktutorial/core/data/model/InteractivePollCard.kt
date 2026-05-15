package com.example.jetpacktutorial.core.data.model

import java.io.Serializable

data class InteractivePollCard(
    val pollId: String,
    val question: String,
    val category: String,              // e.g., "Squad Strategy", "Thala Corner", "Captaincy"
    val optionsList: List<String>,
    val voteDistribution: List<Int>,   // Percentage distribution matching optionsList order (e.g., [65, 20, 15])
    val totalVotesFormatted: String,   // e.g., "142.8K Votes"
    val userSelectedOptionIndex: Int? = null // Nullable to check if the user has already voted
) : Serializable