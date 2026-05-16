package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.UserMatchPrediction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmittedPredictionsRepository @Inject constructor() {

    private val _predictions = MutableStateFlow<Map<String, UserMatchPrediction>>(emptyMap())
    val predictions: StateFlow<Map<String, UserMatchPrediction>> = _predictions.asStateFlow()

    fun getPrediction(matchId: String): UserMatchPrediction? = _predictions.value[matchId]

    fun save(prediction: UserMatchPrediction) {
        _predictions.update { current -> current + (prediction.matchId to prediction) }
    }
}
