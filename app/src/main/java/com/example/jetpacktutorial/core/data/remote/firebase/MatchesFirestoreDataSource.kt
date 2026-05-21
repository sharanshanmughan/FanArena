package com.example.jetpacktutorial.core.data.remote.firebase

import android.util.Log
import com.example.jetpacktutorial.core.constants.FirebaseConstants
import com.example.jetpacktutorial.core.data.model.DetailedMatchCard
import com.example.jetpacktutorial.core.data.model.LeaderboardEntry
import com.example.jetpacktutorial.core.data.model.LeaderboardUser
import com.example.jetpacktutorial.core.data.model.Match
import com.example.jetpacktutorial.core.data.model.toDetailedMatchCard
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirestoreMatches"

@Singleton
class MatchesFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    suspend fun getHomeMatches(): List<Match> = try {
        firestore.collection(FirebaseConstants.MATCHES_COLLECTION)
            .whereEqualTo(FirebaseConstants.MATCH_FIELD_SHOW_ON_HOME, true)
            .limit(FirebaseConstants.HOME_MATCHES_LIMIT)
            .get()
            .await()
            .toMatches()
            .also { Log.d(TAG, "home: loaded ${it.size} matches") }
    } catch (e: Exception) {
        Log.e(TAG, "home FAILED: ${e.message}", e)
        emptyList()
    }

    /**
     * Loads matches without server-side orderBy, then sorts by sortOrder on the client.
     * Documents missing sortOrder still load (default 0).
     */
    suspend fun getTodayMatches(): List<DetailedMatchCard> = try {
        firestore.collection(FirebaseConstants.MATCHES_COLLECTION)
            .limit(FirebaseConstants.TODAY_MATCHES_LIMIT)
            .get()
            .await()
            .toMatches()
            .sortedBy { it.sortOrder }
            .map { it.toDetailedMatchCard() }
            .also { Log.d(TAG, "today: loaded ${it.size} matches") }
    } catch (e: Exception) {
        Log.e(TAG, "today FAILED: ${e.message}", e)
        emptyList()
    }

    suspend fun getTopLeaderboardUsers(): List<LeaderboardUser> = try {
        firestore.collection(FirebaseConstants.LEADERBOARD_GLOBAL_COLLECTION)
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(FirebaseConstants.HOME_TOP_USERS_LIMIT)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                doc.toObject(LeaderboardEntry::class.java)?.let { entry ->
                    LeaderboardUser(
                        rank = 0,
                        username = entry.username,
                        avatarUrl = entry.avatarUrl,
                        points = entry.points.toInt(),
                    )
                }
            }
            .mapIndexed { index, user -> user.copy(rank = index + 1) }
            .also { Log.d(TAG, "leaderboard: loaded ${it.size} users") }
    } catch (e: Exception) {
        Log.e(TAG, "leaderboard FAILED: ${e.message}", e)
        emptyList()
    }
}
