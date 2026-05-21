package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.remote.firebase.MatchesFirestoreDataSource
import com.example.jetpacktutorial.feature.home.HomeUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val matchesFirestoreDataSource: MatchesFirestoreDataSource,
) {
    fun getHomeData(): Flow<HomeUiState> = flow {
        emit(HomeUiState.Loading)

        try {
            val matches = matchesFirestoreDataSource.getHomeMatches()
            val topUsers = matchesFirestoreDataSource.getTopLeaderboardUsers()
            emit(
                HomeUiState.Success(
                    todayMatches = matches,
                    topUsers = topUsers,
                ),
            )
        } catch (e: Exception) {
            emit(
                HomeUiState.Error(
                    message = "Failed to load Arena. Check your internet connection and Firestore data.",
                ),
            )
        }
    }
}
