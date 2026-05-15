package com.example.jetpacktutorial.core.data.remote.firebase

import com.example.jetpacktutorial.core.constants.FirebaseConstants
import com.example.jetpacktutorial.core.data.model.LeaderboardType
import com.example.jetpacktutorial.core.data.model.LeaderboardUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.java

@Singleton
class LeaderboardDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    /** Live-stream leaderboard for a given type (global / weekly / match) */
    fun observeLeaderboard(
        type: LeaderboardType,
        matchId: String? = null,
        limit: Long = 50
    ): Flow<List<LeaderboardUser>> = callbackFlow {

        val collection = when (type) {
            LeaderboardType.GLOBAL -> FirebaseConstants.LEADERBOARD_GLOBAL_COLLECTION
            LeaderboardType.WEEKLY -> FirebaseConstants.LEADERBOARD_WEEKLY_COLLECTION
            LeaderboardType.MATCH  -> FirebaseConstants.LEADERBOARD_MATCH_COLLECTION
        }

        var query: Query = firestore.collection(collection)
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(limit)

        if (type == LeaderboardType.MATCH && matchId != null) {
            query = firestore.collection(collection)
                .whereEqualTo("matchId", matchId)
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(limit)
        }

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val users = snapshot?.documents
                ?.mapNotNull { it.toObject(LeaderboardUser::class.java)?.copy(rank = 0) }
                ?.mapIndexed { index, user -> user.copy(rank = index + 1) }
                ?: emptyList()
            trySend(users)
        }
        awaitClose { subscription.remove() }
    }

    /** Update a user's points on the leaderboard (called from backend/cloud function ideally) */
    suspend fun updateUserPoints(
        userId: String,
        pointsDelta: Int,
        type: LeaderboardType
    ): Result<Unit> = runCatching {
        val collection = when (type) {
            LeaderboardType.GLOBAL -> FirebaseConstants.LEADERBOARD_GLOBAL_COLLECTION
            LeaderboardType.WEEKLY -> FirebaseConstants.LEADERBOARD_WEEKLY_COLLECTION
            LeaderboardType.MATCH  -> FirebaseConstants.LEADERBOARD_MATCH_COLLECTION
        }
        val ref = firestore.collection(collection).document(userId)
        firestore.runTransaction { tx ->
            val snapshot = tx.get(ref)
            val current = snapshot.getLong("points") ?: 0L
            tx.update(ref, "points", current + pointsDelta)
        }.await()
    }
}
