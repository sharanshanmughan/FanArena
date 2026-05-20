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

@Singleton
class MatchesFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    suspend fun getHomeMatches(): List<Match> {
        Log.e("FirestoreError", "Fetch operation failed details: home")
        return try {
            firestore.collection(FirebaseConstants.MATCHES_COLLECTION)
                .whereEqualTo(FirebaseConstants.MATCH_FIELD_SHOW_ON_HOME, true)
                .limit(FirebaseConstants.HOME_MATCHES_LIMIT)
                .get()
                .await()
                .toObjects(Match::class.java)
        } catch (e: Exception) {
            // Prevent crashes if network cuts out or collection doesn't exist yet
            Log.e("FirestoreError", "Fetch operation failed details: ${e.localizedMessage}", e)
            emptyList()
        }
    }

    /**
     * IMPORTANT: This query requires an Index in your Firebase console.
     * If it throws an exception, check Logcat for a direct URL to generate the index automatically.
     */
    suspend fun getTodayMatches(): List<DetailedMatchCard> {
        return try {
            firestore.collection(FirebaseConstants.MATCHES_COLLECTION)
                .orderBy(FirebaseConstants.MATCH_FIELD_SORT_ORDER, Query.Direction.ASCENDING)
                .limit(FirebaseConstants.TODAY_MATCHES_LIMIT)
                .get()
                .await()
                .toObjects(Match::class.java)
                .map { it.toDetailedMatchCard() }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("FirestoreError", "Fetch operation failed details: ${e.localizedMessage}", e)
            emptyList()
        }
    }

    suspend fun getTopLeaderboardUsers(): List<LeaderboardUser> {
        return try {
            firestore.collection(FirebaseConstants.LEADERBOARD_GLOBAL_COLLECTION)
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(FirebaseConstants.HOME_TOP_USERS_LIMIT)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(LeaderboardEntry::class.java)?.let { entry ->
                        LeaderboardUser(
                            rank = 0, // Temporarily initialized, updated sequentially right below
                            username = entry.username,
                            avatarUrl = entry.avatarUrl,
                            points = entry.points,
                        )
                    }
                }
                // Correctly handles rank indexing on the client-side array sequence smoothly
                .mapIndexed { index, user -> user.copy(rank = index + 1) }
        } catch (e: Exception) {
            Log.e("FirestoreError", "Fetch operation failed details: ${e.localizedMessage}", e)
            emptyList()
        }
    }
}