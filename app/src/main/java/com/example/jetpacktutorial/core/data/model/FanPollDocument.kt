package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

/** Firestore document in fan_polls collection. */
@IgnoreExtraProperties
data class FanPollDocument(
    val question: String = "",
    val category: String = "",
    val options: List<String> = emptyList(),
    val voteCounts: List<Long> = emptyList(),
    /** Empty = arena/home polls; non-empty = match hub polls for that match. */
    val matchId: String = "",
    val sortOrder: Long = 0L,
    val isActive: Boolean = true,
)

fun FanPollDocument.toDefinition(pollId: String): FanPollDefinition = FanPollDefinition(
    pollId = pollId,
    question = question,
    category = category,
    options = options,
    initialVoteCounts = voteCounts.map { it.toInt() },
    matchId = matchId.takeIf { it.isNotBlank() },
)
