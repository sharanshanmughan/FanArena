package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.concurrent.TimeUnit

@IgnoreExtraProperties
data class DiscussionCommentDocument(
    val userId: String = "",
    val username: String = "",
    val avatarUrl: String = "",
    val text: String = "",
    val supportTeamBadge: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

fun DiscussionCommentDocument.toComment(commentId: String): Comment = Comment(
    id = commentId,
    username = username,
    avatarUrl = avatarUrl,
    text = text,
    timestamp = formatDiscussionTimestamp(createdAt),
    supportTeamBadge = supportTeamBadge.takeIf { it.isNotBlank() },
)

fun formatDiscussionTimestamp(createdAtMillis: Long): String {
    val diffMs = (System.currentTimeMillis() - createdAtMillis).coerceAtLeast(0L)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        minutes < 24 * 60 -> "${minutes / 60}h ago"
        else -> "${minutes / (24 * 60)}d ago"
    }
}
