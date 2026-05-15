package com.example.jetpacktutorial.core.data.model

data class FanPoll(
    val id: String,
    val question: String,
    val totalVotes: Int,
    val options: List<String>
)
