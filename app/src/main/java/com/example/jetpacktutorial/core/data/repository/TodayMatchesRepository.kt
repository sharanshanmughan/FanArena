package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.remote.firebase.MatchesFirestoreDataSource
import com.example.jetpacktutorial.feature.todayMatches.TodayMatchesUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TodayMatchesRepository @Inject constructor(
    private val matchesFirestoreDataSource: MatchesFirestoreDataSource,
) {
    fun getTodayMatches(): Flow<TodayMatchesUiState> = flow {
        emit(TodayMatchesUiState.Loading)

        try {
            val matches = matchesFirestoreDataSource.getTodayMatches()
            if (matches.isEmpty()) {
                emit(
                    TodayMatchesUiState.Error(
                        "No matches found. Add documents to the matches collection in Firestore.",
                    ),
                )
            } else {
                emit(TodayMatchesUiState.Success(matches))
            }
        } catch (e: Exception) {
            emit(
                TodayMatchesUiState.Error(
                    "Could not retrieve today's match fixtures. Check Firestore data and indexes.",
                ),
            )
        }
    }
}
