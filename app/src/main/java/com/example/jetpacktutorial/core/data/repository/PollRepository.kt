package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.common.Resource
import com.example.jetpacktutorial.core.data.remote.firebase.PollDataSource
import com.example.jetpacktutorial.core.data.model.LivePoll
import com.example.jetpacktutorial.core.data.model.PollVote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PollRepository @Inject constructor(
    private val pollDataSource: PollDataSource
) {

    fun observeLivePolls(
        matchId: String
    ): Flow<Resource<List<LivePoll>>> {

        return pollDataSource
            .observeLivePolls(matchId)
            .map<List<LivePoll>, Resource<List<LivePoll>>> {
                Resource.Success(it)
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch {
                emit(
                    Resource.Error(
                        it.message ?: "Failed to load polls"
                    )
                )
            }
    }
    fun castVote(vote: PollVote): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        pollDataSource.castVote(vote)
            .onSuccess { emit(Resource.Success(Unit)) }
            .onFailure { emit(Resource.Error(it.message ?: "Failed to cast vote")) }
    }

    suspend fun hasUserVoted(userId: String, pollId: String): Boolean =
        pollDataSource.hasUserVoted(userId, pollId)
}
