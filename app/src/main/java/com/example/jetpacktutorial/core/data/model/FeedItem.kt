package com.example.jetpacktutorial.core.data.model

import java.io.Serializable

data class FeedItem(
    val id: String,
    val type: FeedType,
    val authorName: String,
    val authorAvatar: String,
    val timestamp: String,
    val caption: String,
    val mediaUrl: String? = null,
    val pollOptions: List<String>? = null,
    val pollVotes: List<Int>? = null, // Stores raw vote arrays
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false
) : Serializable
