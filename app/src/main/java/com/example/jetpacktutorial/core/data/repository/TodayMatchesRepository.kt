package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.DetailedMatchCard
import com.example.jetpacktutorial.core.data.model.MatchStatus
import com.example.jetpacktutorial.feature.todayMatches.TodayMatchesUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class TodayMatchesRepository @Inject constructor() {
    fun getTodayMatches(): Flow<TodayMatchesUiState> = flow {
        emit(TodayMatchesUiState.Loading)
        try {
            delay(850) // Dynamic network stream simulation

            val mockMatches = listOf(
                DetailedMatchCard(
                    matchId = "TM_01",
                    team1Code = "RCB",
                    team1Name = "Royal Challengers Bengaluru",
                    team2Code = "KKR",
                    team2Name = "Kolkata Knight Riders",
                    status = MatchStatus.UPCOMING,
                    matchTimeOrScore = "Starts at 7:30 PM",
                    fanSupportPredictionRatio = 0.64f
                ),
                DetailedMatchCard(
                    matchId = "TM_02",
                    team1Code = "MI",
                    team1Name = "Mumbai Indians",
                    team2Code = "CSK",
                    team2Name = "Chennai Super Kings",
                    status = MatchStatus.LIVE,
                    matchTimeOrScore = "MI: 164/4 (16.2) • CSK chasing",
                    fanSupportPredictionRatio = 0.48f
                ),
                DetailedMatchCard(
                    matchId = "TM_03",
                    team1Code = "SRH",
                    team1Name = "Sunrisers Hyderabad",
                    team2Code = "DC",
                    team2Name = "Delhi Capitals",
                    status = MatchStatus.COMPLETED,
                    matchTimeOrScore = "SRH won by 24 runs",
                    fanSupportPredictionRatio = 0.70f
                )
            )
            emit(TodayMatchesUiState.Success(mockMatches))
        } catch (e: IOException) {
            emit(TodayMatchesUiState.Error("Could not retrieve today's match fixtures."))
        }
    }
}