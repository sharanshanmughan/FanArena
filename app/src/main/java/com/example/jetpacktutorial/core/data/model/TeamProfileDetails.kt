package com.example.jetpacktutorial.core.data.model

data class TeamProfileDetails(
    val teamInfo: IplTeamCard,
    val isUserInFanCamp: Boolean,
    val squadList: List<ProfilePlayer>,
    val pastResults: List<TeamPastResult>
)