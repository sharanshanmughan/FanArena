package com.example.jetpacktutorial.core.data.model

data class Comment(
    val id: String,
    val username: String,
    val avatarUrl: String,
    val text: String,
    val timestamp: String,
    val supportTeamBadge: String? = null
)
