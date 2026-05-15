package com.example.jetpacktutorial.core.data.remote.firebase

import com.example.jetpacktutorial.core.constants.FirebaseConstants
import com.example.jetpacktutorial.core.data.model.LivePoll
import com.example.jetpacktutorial.core.data.model.PollVote
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PollDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val rtdb: FirebaseDatabase
) {

    /** Stream live polls for a match from RTDB (instant vote count updates) */
    fun observeLivePolls(matchId: String): Flow<List<LivePoll>> = callbackFlow {
        val ref = rtdb.getReference("${FirebaseConstants.POLLS_NODE}/$matchId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val polls = snapshot.children.mapNotNull { it.getValue(LivePoll::class.java) }
                trySend(polls)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /** Cast a vote — atomically increment option count in RTDB */
    suspend fun castVote(vote: PollVote): Result<Unit> = runCatching {
        val optionRef = rtdb
            .getReference("${FirebaseConstants.POLLS_NODE}/${vote.matchId}/${vote.pollId}/options/${vote.optionId}/voteCount")
        optionRef.setValue(ServerValue.increment(1)).await()

        // Record the vote in Firestore for audit / user history
        firestore
            .collection(FirebaseConstants.POLL_VOTES_COLLECTION)
            .document("${vote.userId}_${vote.pollId}")
            .set(vote)
            .await()
    }

    /** Check if user already voted on a poll */
    suspend fun hasUserVoted(userId: String, pollId: String): Boolean {
        val snapshot = firestore
            .collection(FirebaseConstants.POLL_VOTES_COLLECTION)
            .document("${userId}_${pollId}")
            .get()
            .await()
        return snapshot.exists()
    }
}
