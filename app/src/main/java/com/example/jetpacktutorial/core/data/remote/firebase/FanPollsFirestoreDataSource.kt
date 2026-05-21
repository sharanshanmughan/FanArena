package com.example.jetpacktutorial.core.data.remote.firebase

import android.util.Log
import com.example.jetpacktutorial.core.constants.FirebaseConstants
import com.example.jetpacktutorial.core.data.model.FanPollDefinition
import com.example.jetpacktutorial.core.data.model.FanPollDocument
import com.example.jetpacktutorial.core.data.model.toDefinition
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirestoreFanPolls"

@Singleton
class FanPollsFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    suspend fun getActiveFanPolls(): List<FanPollDefinition> = try {
        val snapshot = firestore.collection(FirebaseConstants.FAN_POLLS_COLLECTION)
            .whereEqualTo(FirebaseConstants.FAN_POLL_FIELD_IS_ACTIVE, true)
            .limit(FirebaseConstants.FAN_POLLS_LIMIT)
            .get()
            .await()

        Log.d(TAG, "Raw document count: ${snapshot.size()}")

        val parsed = snapshot.documents
            .sortedBy { it.getLong(FirebaseConstants.FAN_POLL_FIELD_SORT_ORDER) ?: 0L }
            .mapNotNull { doc ->
                runCatching {
                    doc.toObject(FanPollDocument::class.java)?.toDefinition(doc.id)
                }.onFailure { error ->
                    Log.w(TAG, "Parse failed for '${doc.id}': ${error.message}")
                }.getOrNull()
            }

        Log.d(TAG, "Parsed fan poll count: ${parsed.size}")
        parsed
    } catch (e: Exception) {
        Log.e(TAG, "Fetch FAILED: ${e.message}", e)
        emptyList()
    }
}
