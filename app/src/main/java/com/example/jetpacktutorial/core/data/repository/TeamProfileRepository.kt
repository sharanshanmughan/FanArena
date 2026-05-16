package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.IplTeamCard
import com.example.jetpacktutorial.core.data.model.ProfilePlayer
import com.example.jetpacktutorial.core.data.model.TeamPastResult
import com.example.jetpacktutorial.core.data.model.TeamProfileDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TeamProfileRepository @Inject constructor(){
    fun getTeamProfileDetails(teamId: String): Flow<TeamProfileDetails> = flow {
        // Here you would normally fetch data from your Room database or Retrofit API
        emit(
            TeamProfileDetails(
                teamInfo = IplTeamCard(
                    teamId = teamId,
                    name = when(teamId) {
                        "RCB" -> "Royal Challengers Bengaluru"
                        "KKR" -> "Kolkata Knight Riders"
                        else -> "IPL Arena Franchise"
                    },
                    shortCode = teamId,
                    primaryColorHex = if (teamId == "RCB") "#CC0000" else "#4A148C",
                    secondaryColorHex = "#000000",
                    fanCountFormatted = "18.4M Fans"
                ),
                isUserInFanCamp = false,
                squadList = listOf(
                    ProfilePlayer("1", "Virat Kohli", "Batsman", isCaptain = false),
                    ProfilePlayer("2", "Faf du Plessis", "Batsman", isCaptain = true),
                    ProfilePlayer("3", "Mohammed Siraj", "Bowler"),
                    ProfilePlayer("4", "Glenn Maxwell", "All-Rounder")
                ),
                pastResults = listOf(
                    TeamPastResult("KKR", "Won by 7 wickets", true),
                    TeamPastResult("MI", "Lost by 14 runs", false),
                    TeamPastResult("CSK", "Won by 5 wickets", true)
                )
            )
        )
    }
}