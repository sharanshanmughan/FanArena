package com.example.jetpacktutorial.core.data.remote.firebase

import android.util.Log
import com.example.jetpacktutorial.core.constants.FirebaseConstants
import com.example.jetpacktutorial.core.data.model.TrendingPredictionDefinition
import com.example.jetpacktutorial.core.data.model.TrendingPredictionDocument
import com.example.jetpacktutorial.core.data.model.toDefinition
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirestoreTrending"

@Singleton
class TrendingPredictionsFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    suspend fun getActiveTrendingPredictions(): List<TrendingPredictionDefinition> = try {
        val snapshot = firestore.collection(FirebaseConstants.TRENDING_PREDICTIONS_COLLECTION)
            .whereEqualTo(FirebaseConstants.TRENDING_FIELD_IS_ACTIVE, true)
            .limit(FirebaseConstants.TRENDING_PREDICTIONS_LIMIT)
            .get()
            .await()

        Log.d(TAG, "Raw document count: ${snapshot.size()}")

        val parsed = snapshot.documents
            .sortedBy { it.getLong(FirebaseConstants.TRENDING_FIELD_SORT_ORDER) ?: 0L }
            .mapNotNull { doc ->
                runCatching {
                    doc.toObject(TrendingPredictionDocument::class.java)?.toDefinition(doc.id)
                }.onFailure { error ->
                    Log.w(TAG, "Parse failed for '${doc.id}': ${error.message}")
                }.getOrNull()
            }

        Log.d(TAG, "Parsed trending count: ${parsed.size}")
        parsed
    } catch (e: Exception) {
        Log.e(TAG, "Fetch FAILED: ${e.message}", e)
        emptyList()
    }
}
