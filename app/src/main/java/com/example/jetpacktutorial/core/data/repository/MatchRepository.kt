package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.common.Resource
import com.example.jetpacktutorial.core.data.model.LiveScore
import com.example.jetpacktutorial.core.data.model.Match
import com.example.jetpacktutorial.core.data.model.MatchEvent
import com.example.jetpacktutorial.core.data.remote.firebase.MatchDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(
    private val matchDataSource: MatchDataSource
) {

    fun observeMatches(): Flow<Resource<List<Match>>> = flow {

        emit(Resource.Loading())

        matchDataSource.observeMatches().collect { matches ->
            emit(Resource.Success(matches))
        }

    }.catch {
        emit(Resource.Error(it.message ?: "Failed to load matches"))
    }

    fun observeMatch(matchId: String): Flow<Resource<Match?>> = flow {

        emit(Resource.Loading())

        matchDataSource.observeMatch(matchId).collect { match ->
            emit(Resource.Success(match))
        }

    }.catch {
        emit(Resource.Error(it.message ?: "Failed to observe match"))
    }

    fun observeLiveScore(matchId: String): Flow<LiveScore?> {
        return matchDataSource.observeLiveScore(matchId)
    }

    fun observeMatchEvents(matchId: String): Flow<List<MatchEvent>> {
        return matchDataSource.observeMatchEvents(matchId)
    }

    fun getUpcomingMatches(): Flow<Resource<List<Match>>> = flow {

        emit(Resource.Loading())

        runCatching {
            matchDataSource.getUpcomingMatches()
        }.onSuccess {
            emit(Resource.Success(it))
        }.onFailure {
            emit(Resource.Error(it.message ?: "Failed to fetch upcoming matches"))
        }
    }
}
