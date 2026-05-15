package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.IplTeamCard
import com.example.jetpacktutorial.feature.teams.TeamsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class TeamsRepository @Inject constructor() {
    fun getIplTeamsList(): Flow<TeamsUiState> = flow {
        emit(TeamsUiState.Loading)
        try {
            delay(750) // Direct disk caching latency simulation

            val mockTeamsList = listOf(
                IplTeamCard("t1", "Royal Challengers Bengaluru", "RCB", "4.8M Fans", "#EC1C24", "#2B2A29"),
                IplTeamCard("t2", "Mumbai Indians", "MI", "5.2M Fans", "#004B87", "#00A0E2"),
                IplTeamCard("t3", "Chennai Super Kings", "CSK", "5.1M Fans", "#FFFF00", "#F26522"),
                IplTeamCard(
                    "t4",
                    "Kolkata Knight Riders",
                    "KKR",
                    "3.6M Fans",
                    "#3A225D",
                    "#B3A123"
                ),
                IplTeamCard("t5", "Rajasthan Royals", "RR", "2.9M Fans", "#EA1A85", "#254AA5"),
                IplTeamCard("t6", "Delhi Capitals", "DC", "2.4M Fans", "#00008B", "#FF0000"),
                IplTeamCard("t7", "Gujarat Titans", "GT", "2.1M Fans", "#0B1E3F", "#D1AB66"),
                IplTeamCard("t8", "Sunrisers Hyderabad", "SRH", "2.5M Fans", "#FF822A", "#000000")
            )

            emit(TeamsUiState.Success(mockTeamsList))
        } catch (e: IOException) {
            emit(TeamsUiState.Error("Failed to initialize active IPL team registries."))
        }
    }
}