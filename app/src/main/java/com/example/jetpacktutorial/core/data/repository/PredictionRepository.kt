package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.PlayerPredictionOption
import com.example.jetpacktutorial.core.data.model.PredictionOptionsPayload
import com.example.jetpacktutorial.feature.prediction.PredictionUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class PredictionRepository @Inject constructor() {
    fun getPredictionOptions(matchId: String): Flow<PredictionUiState> = flow {
        emit(PredictionUiState.Loading)
        try {
            delay(800) // Fast network simulation

            val payload = PredictionOptionsPayload(
                matchId = matchId,
                team1Name = "RCB",
                team2Name = "KKR",
                topScorerOptions = listOf(
                    PlayerPredictionOption("p1", "Virat Kohli", "Batsman", "vk_img", "RCB"),
                    PlayerPredictionOption("p2", "Shubman Gill", "Batsman", "sg_img", "KKR")
                ),
                topBowlerOptions = listOf(
                    PlayerPredictionOption("p3", "Rashid Khan", "Bowler", "rk_img", "RCB"),
                    PlayerPredictionOption("p4", "Y. Chahal", "Bowler", "yc_img", "KKR")
                )
            )
            emit(PredictionUiState.Success(payload))
        } catch (e: IOException) {
            emit(PredictionUiState.Error("Could not retrieve active arena match cards."))
        }
    }
}