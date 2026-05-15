package com.example.jetpacktutorial.core.data.model

import java.io.Serializable

data class LeaderboardPayload(
    val selectedTab: LeaderboardTab,
    val rankingsList: List<ArenaRankedUser>
) : Serializable