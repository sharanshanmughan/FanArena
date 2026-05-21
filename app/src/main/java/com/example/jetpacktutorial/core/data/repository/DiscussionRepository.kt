package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.Comment
import com.example.jetpacktutorial.core.data.remote.firebase.DiscussionFirestoreDataSource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscussionRepository @Inject constructor(
    private val discussionDataSource: DiscussionFirestoreDataSource,
    private val authRepository: AuthRepository,
) {

    fun observeMatchDiscussion(matchId: String): Flow<List<Comment>> =
        discussionDataSource.observeComments(matchId)

    suspend fun postComment(
        matchId: String,
        text: String,
        supportTeamBadge: String = "",
    ): Result<Unit> {
        val user = authRepository.currentUser
            ?: return Result.failure(IllegalStateException("Sign in to join the discussion."))

        val username = user.displayName?.takeIf { it.isNotBlank() }
            ?: if (user.isAnonymous) "Guest Fan" else "Arena Fan"

        return discussionDataSource.postComment(
            matchId = matchId,
            userId = user.uid,
            username = username,
            avatarUrl = user.photoUrl?.toString().orEmpty(),
            text = text,
            supportTeamBadge = supportTeamBadge,
        )
    }

    fun currentUser(): FirebaseUser? = authRepository.currentUser
}
