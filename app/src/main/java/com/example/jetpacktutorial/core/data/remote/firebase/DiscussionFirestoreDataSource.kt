package com.example.jetpacktutorial.core.data.remote.firebase

import android.util.Log
import com.example.jetpacktutorial.core.constants.FirebaseConstants
import com.example.jetpacktutorial.core.data.model.Comment
import com.example.jetpacktutorial.core.data.model.DiscussionCommentDocument
import com.example.jetpacktutorial.core.data.model.toComment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirestoreDiscussion"

@Singleton
class DiscussionFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    fun observeComments(matchId: String): Flow<List<Comment>> = callbackFlow {
        val registration = commentsQuery(matchId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Listen failed for $matchId: ${error.message}", error)
                    close(error)
                    return@addSnapshotListener
                }
                val comments = snapshot?.documents?.mapNotNull { doc ->
                    runCatching {
                        doc.toObject(DiscussionCommentDocument::class.java)?.toComment(doc.id)
                    }.onFailure { parseError ->
                        Log.w(TAG, "Parse failed '${doc.id}': ${parseError.message}")
                    }.getOrNull()
                }.orEmpty()

                Log.d(TAG, "Live comments for $matchId: ${comments.size}")
                trySend(comments)
            }

        awaitClose { registration.remove() }
    }

    suspend fun postComment(
        matchId: String,
        userId: String,
        username: String,
        avatarUrl: String,
        text: String,
        supportTeamBadge: String,
    ): Result<Unit> = runCatching {
        val trimmed = text.trim()
        require(trimmed.isNotEmpty()) { "Comment cannot be empty" }
        require(trimmed.length <= FirebaseConstants.DISCUSSION_MAX_TEXT_LENGTH) {
            "Comment is too long (max ${FirebaseConstants.DISCUSSION_MAX_TEXT_LENGTH} characters)"
        }

        val document = DiscussionCommentDocument(
            userId = userId,
            username = username,
            avatarUrl = avatarUrl,
            text = trimmed,
            supportTeamBadge = supportTeamBadge,
            createdAt = System.currentTimeMillis(),
        )

        commentsCollection(matchId)
            .add(document)
            .await()
    }

    private fun commentsCollection(matchId: String) =
        firestore.collection(FirebaseConstants.MATCH_DISCUSSIONS_COLLECTION)
            .document(matchId)
            .collection(FirebaseConstants.DISCUSSION_COMMENTS_SUBCOLLECTION)

    private fun commentsQuery(matchId: String) =
        commentsCollection(matchId)
            .orderBy(FirebaseConstants.DISCUSSION_FIELD_CREATED_AT, Query.Direction.ASCENDING)
            .limit(FirebaseConstants.DISCUSSION_COMMENTS_LIMIT)
}
