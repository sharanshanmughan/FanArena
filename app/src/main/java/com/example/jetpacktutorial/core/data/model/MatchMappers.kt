package com.example.jetpacktutorial.core.data.model

fun Match.toMatchStatus(): MatchStatus = when (status.trim().uppercase()) {
    "LIVE" -> MatchStatus.LIVE
    "COMPLETED" -> MatchStatus.COMPLETED
    else -> MatchStatus.UPCOMING
}

fun Match.displayTimeOrScore(): String {
    if (matchTimeOrScore.isNotBlank()) return matchTimeOrScore
    return if (toMatchStatus() == MatchStatus.UPCOMING && time.isNotBlank()) {
        "Starts at $time"
    } else {
        time
    }
}

fun Match.toDetailedMatchCard(): DetailedMatchCard = DetailedMatchCard(
    matchId = id,
    team1Code = team1,
    team1Name = team1Name.ifBlank { team1 },
    team2Code = team2,
    team2Name = team2Name.ifBlank { team2 },
    status = toMatchStatus(),
    matchTimeOrScore = displayTimeOrScore(),
    fanSupportPredictionRatio = fanSupportPredictionRatio.toFloat(),
)
