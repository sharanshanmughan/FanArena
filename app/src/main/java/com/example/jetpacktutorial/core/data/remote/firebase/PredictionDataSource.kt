package com.example.jetpacktutorial.core.data.remote.firebase

import com.example.jetpacktutorial.core.constants.FirebaseConstants
import com.example.jetpacktutorial.core.data.model.Prediction
import com.example.jetpacktutorial.core.data.model.PredictionResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PredictionDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    /** Submit or update a prediction */
    suspend fun submitPrediction(prediction: Prediction): Result<Unit> = runCatching {
        val ref = firestore
            .collection(FirebaseConstants.PREDICTIONS_COLLECTION)
            .document("${prediction.userId}_${prediction.matchId}_${prediction.questionId}")
        ref.set(prediction).await()
    }

    /** Observe all predictions made by a user for a match */
    fun observeUserPredictions(userId: String, matchId: String): Flow<List<Prediction>> =
        callbackFlow {
            val subscription = firestore
                .collection(FirebaseConstants.PREDICTIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("matchId", matchId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { close(error); return@addSnapshotListener }
                    val predictions = snapshot?.documents
                        ?.mapNotNull { it.toObject(Prediction::class.java) }
                        ?: emptyList()
                    trySend(predictions)
                }
            awaitClose { subscription.remove() }
        }

    /** Lock a prediction (no more edits after match starts) */
    suspend fun lockPrediction(predictionId: String): Result<Unit> = runCatching {
        firestore
            .collection(FirebaseConstants.PREDICTIONS_COLLECTION)
            .document(predictionId)
            .update("isLocked", true)
            .await()
    }

    /** Fetch prediction results after match ends */
    suspend fun getPredictionResults(matchId: String, userId: String): List<PredictionResult> {
        val snapshot = firestore
            .collection(FirebaseConstants.PREDICTION_RESULTS_COLLECTION)
            .whereEqualTo("matchId", matchId)
            .whereEqualTo("userId", userId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(PredictionResult::class.java) }
    }
}
