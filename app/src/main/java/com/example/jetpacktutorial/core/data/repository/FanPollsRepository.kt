package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.FanPollDefinition
import com.example.jetpacktutorial.core.data.model.InteractivePollCard
import com.example.jetpacktutorial.feature.fanPoll.FanPollsUiState
import com.example.jetpacktutorial.core.constants.MatchConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FanPollsRepository @Inject constructor(
    private val pollVotesRepository: PollVotesRepository,
) {

    private val allPollDefinitions = listOf(
        FanPollDefinition(
            pollId = "FP_101",
            question = "Will MSD push himself up the batting order during run chases this week?",
            category = "Thala Corner",
            options = listOf(
                "Yes, definitely needed",
                "No, stay at No. 8",
                "Depends on required run rate",
            ),
            initialVoteCounts = listOf(139_200, 33_600, 67_200),
            matchId = null,
        ),
        FanPollDefinition(
            pollId = "FP_102",
            question = "Should RCB bench an overseas batsman to bring in an extra specialist death bowler?",
            category = "Squad Strategy",
            options = listOf("Yes, bowling is leaking runs", "No, back the batting line-up"),
            initialVoteCounts = listOf(82_080, 31_920),
            matchId = null,
        ),
        FanPollDefinition(
            pollId = "MH_p1",
            question = "Who wins the toss?",
            category = "Match Hub",
            options = listOf("RCB", "KKR"),
            initialVoteCounts = listOf(7_440, 4_960),
            matchId = MatchConstants.RCB_KKR_HUB,
        ),
        FanPollDefinition(
            pollId = "MH_p2",
            question = "How many maximum sixes will be hit?",
            category = "Match Hub",
            options = listOf("0-5", "6-12", "13+"),
            initialVoteCounts = listOf(2_670, 4_450, 1_780),
            matchId = MatchConstants.RCB_KKR_HUB,
        ),
    )

    init {
        pollVotesRepository.ensureInitialized(allPollDefinitions)
    }

    fun submitVote(pollId: String, optionIndex: Int): Boolean =
        pollVotesRepository.submitVote(pollId, optionIndex)

    fun observeArenaPolls(): Flow<FanPollsUiState> =
        observePolls(allPollDefinitions.filter { it.matchId == null })

    fun observeMatchPolls(matchId: String): Flow<FanPollsUiState> =
        observePolls(allPollDefinitions.filter { it.matchId == matchId })

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
        }
            .map { polls: List<InteractivePollCard> ->
                val state: FanPollsUiState = FanPollsUiState.Success(polls)
                state
            }
            .onStart { emit(FanPollsUiState.Loading) }
            .catch {
                emit(FanPollsUiState.Error("Failed to update active fan poll streams."))
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
