package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.FanPollDefinition
import com.example.jetpacktutorial.core.data.model.InteractivePollCard
import com.example.jetpacktutorial.core.data.remote.firebase.FanPollsFirestoreDataSource
import com.example.jetpacktutorial.feature.fanPoll.FanPollsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FanPollsRepository @Inject constructor(
    private val firestoreDataSource: FanPollsFirestoreDataSource,
    private val pollVotesRepository: PollVotesRepository,
) {

    private val loadMutex = Mutex()
    private var cachedDefinitions: List<FanPollDefinition>? = null

    fun submitVote(pollId: String, optionIndex: Int): Boolean =
        pollVotesRepository.submitVote(pollId, optionIndex)

    fun observeArenaPolls(): Flow<FanPollsUiState> = observeFilteredPolls(
        label = "arena",
        filter = { it.matchId == null },
    )

    fun observeMatchPolls(matchId: String): Flow<FanPollsUiState> = observeFilteredPolls(
        label = "match:$matchId",
        filter = { it.matchId == matchId },
    )

    private fun observeFilteredPolls(
        label: String,
        filter: (FanPollDefinition) -> Boolean,
    ): Flow<FanPollsUiState> = flow {
        emit(FanPollsUiState.Loading)
        val definitions = loadDefinitions().filter(filter)
        if (definitions.isEmpty()) {
            emit(
                FanPollsUiState.Error(
                    "No fan polls found ($label). Add documents to fan_polls in Firestore.",
                ),
            )
            return@flow
        }
        observePolls(definitions).collect { emit(it) }
    }

    private fun observePolls(definitions: List<FanPollDefinition>): Flow<FanPollsUiState> =
        combine(
            pollVotesRepository.userVotes,
            pollVotesRepository.voteCounts,
        ) { userVotes, voteCounts ->
            definitions.map { definition ->
                definition.toInteractivePollCard(
                    userVotes = userVotes,
                    voteCounts = voteCounts[definition.pollId] ?: definition.initialVoteCounts,
                )
            }
        }.map { polls -> polls.asFanPollsSuccess() }

    private fun List<InteractivePollCard>.asFanPollsSuccess(): FanPollsUiState =
        FanPollsUiState.Success(this)

    private suspend fun loadDefinitions(): List<FanPollDefinition> = loadMutex.withLock {
        cachedDefinitions?.let { return it }
        val loaded = firestoreDataSource.getActiveFanPolls()
        if (loaded.isNotEmpty()) {
            pollVotesRepository.ensureInitialized(loaded)
            cachedDefinitions = loaded
        }
        loaded
    }

    suspend fun refreshFromFirestore() {
        loadMutex.withLock {
            cachedDefinitions = null
            loadDefinitions()
        }
    }

    private fun FanPollDefinition.toInteractivePollCard(
        userVotes: Map<String, Int>,
        voteCounts: List<Int>,
    ): InteractivePollCard {
        val total = voteCounts.sum().coerceAtLeast(1)
        val distribution = voteCounts.map { count ->
            ((count.toFloat() / total) * 100).toInt()
        }
        return InteractivePollCard(
            pollId = pollId,
            question = question,
            category = category,
            optionsList = options,
            voteDistribution = distribution,
            totalVotesFormatted = formatVoteTotal(total),
            userSelectedOptionIndex = userVotes[pollId],
        )
    }

    private fun formatVoteTotal(total: Int): String = when {
        total >= 1_000_000 -> String.format(Locale.US, "%.1fM Votes", total / 1_000_000f)
        total >= 1_000 -> String.format(Locale.US, "%.1fK Votes", total / 1_000f)
        else -> "$total Votes"
    }
}
