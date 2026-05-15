package com.example.jetpacktutorial.core.data.repository


import com.example.jetpacktutorial.core.common.Resource
import com.example.jetpacktutorial.core.data.model.LeaderboardType
import com.example.jetpacktutorial.core.data.model.LeaderboardUser
import com.example.jetpacktutorial.core.data.remote.firebase.LeaderboardDataSource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepository @Inject constructor(
    private val leaderboardDataSource: LeaderboardDataSource
) {

    fun observeLeaderboard(
        type: LeaderboardType,
        matchId: String? = null,
        limit: Long = 50
    ): Flow<Resource<List<LeaderboardUser>>> {

        return leaderboardDataSource
            .observeLeaderboard(type, matchId, limit)
            .map<List<LeaderboardUser>, Resource<List<LeaderboardUser>>> {
                Resource.Success(it)
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch {
                emit(
                    Resource.Error(
                        it.message ?: "Failed to load leaderboard"
                    )
                )
            }
    }

    fun updateUserPoints(
        userId: String,
        pointsDelta: Int,
        type: LeaderboardType
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        leaderboardDataSource.updateUserPoints(userId, pointsDelta, type)
            .onSuccess { emit(Resource.Success(Unit)) }
            .onFailure { emit(Resource.Error(it.message ?: "Failed to update points")) }
    }
}
