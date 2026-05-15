package com.example.jetpacktutorial.core.data.remote.firebase

import com.example.jetpacktutorial.core.constants.FirebaseConstants
import com.example.jetpacktutorial.core.data.model.LiveScore
import com.example.jetpacktutorial.core.data.model.Match
import com.example.jetpacktutorial.core.data.model.MatchEvent
import com.example.jetpacktutorial.core.data.model.MatchStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val rtdb: FirebaseDatabase
) {

    /** Stream all upcoming + live matches (Firestore) */
    fun observeMatches(): Flow<List<Match>> = callbackFlow {
        val subscription = firestore
            .collection(FirebaseConstants.MATCHES_COLLECTION)
            .whereIn("status", listOf(MatchStatus.UPCOMING.name, MatchStatus.LIVE.name))
            .orderBy("startTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val matches = snapshot?.documents
                    ?.mapNotNull { it.toObject(Match::class.java)?.copy(id = it.id) }
                    ?: emptyList()
                trySend(matches)
            }
        awaitClose { subscription.remove() }
    }

    /** Stream a single match from Firestore */
    fun observeMatch(matchId: String): Flow<Match?> = callbackFlow {
        val subscription = firestore
            .collection(FirebaseConstants.MATCHES_COLLECTION)
            .document(matchId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val match = snapshot?.toObject(Match::class.java)?.copy(id = matchId)
                trySend(match)
            }
        awaitClose { subscription.remove() }
    }

    /** Stream live score from Realtime Database (low-latency) */
    fun observeLiveScore(matchId: String): Flow<LiveScore?> = callbackFlow {
        val ref = rtdb.getReference("${FirebaseConstants.LIVE_SCORES_NODE}/$matchId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(LiveScore::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /** Stream match events (wickets, goals, etc.) from RTDB */
    fun observeMatchEvents(matchId: String): Flow<List<MatchEvent>> = callbackFlow {
        val ref = rtdb.getReference("${FirebaseConstants.MATCH_EVENTS_NODE}/$matchId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull { it.getValue(MatchEvent::class.java) }
                trySend(events.sortedByDescending { it.timestamp })
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /** One-shot fetch for upcoming matches */
    suspend fun getUpcomingMatches(): List<Match> {
        val snapshot = firestore
            .collection(FirebaseConstants.MATCHES_COLLECTION)
            .whereEqualTo("status", MatchStatus.UPCOMING.name)
            .orderBy("startTime", Query.Direction.ASCENDING)
            .limit(20)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Match::class.java)?.copy(id = it.id) }
    }
}
