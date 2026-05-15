package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.common.Resource
import com.example.jetpacktutorial.core.data.model.LeaderboardUser
import com.example.jetpacktutorial.core.data.model.Prediction
import com.example.jetpacktutorial.core.data.model.PredictionResult
import com.example.jetpacktutorial.core.data.remote.firebase.PredictionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PredictionRepository @Inject constructor(
    private val predictionDataSource: PredictionDataSource
) {

    fun submitPrediction(prediction: Prediction): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        predictionDataSource.submitPrediction(prediction)
            .onSuccess { emit(Resource.Success(Unit)) }
            .onFailure { emit(Resource.Error(it.message ?: "Failed to submit prediction")) }
    }

    fun observeUserPredictions(
        userId: String,
        matchId: String
    ): Flow<Resource<List<Prediction>>> {
        return predictionDataSource
            .observeUserPredictions(userId, matchId)
            .map<List<Prediction>, Resource<List<Prediction>>> {
                Resource.Success(it)
            }.onStart { emit(Resource.Loading()) }
            .catch  { emit(Resource.Error(it.message ?: "Failed to observe predictions")) }

    }
    fun lockPrediction(predictionId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        predictionDataSource.lockPrediction(predictionId)
            .onSuccess { emit(Resource.Success(Unit)) }
            .onFailure { emit(Resource.Error(it.message ?: "Failed to lock prediction")) }
    }

    fun getPredictionResults(
        matchId: String,
        userId: String
    ): Flow<Resource<List<PredictionResult>>> = flow {
        emit(Resource.Loading())
        runCatching { predictionDataSource.getPredictionResults(matchId, userId) }
            .onSuccess { emit(Resource.Success(it)) }
            .onFailure { emit(Resource.Error(it.message ?: "Failed to get results")) }
    }
}
