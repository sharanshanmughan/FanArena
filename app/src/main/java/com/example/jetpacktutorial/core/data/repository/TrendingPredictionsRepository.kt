package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.constants.MatchConstants
import com.example.jetpacktutorial.core.data.model.PredictionInsightCard
import com.example.jetpacktutorial.core.data.model.TrendingPredictionDefinition
import com.example.jetpacktutorial.feature.trendingPrediction.TrendingPredictionsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrendingPredictionsRepository @Inject constructor(
    private val trendingVotesRepository: TrendingVotesRepository,
) {

    private val allDefinitions = listOf(
        TrendingPredictionDefinition(
            predictionId = "TP_01",
            category = "Match Multiplier",
            question = "Which team will score more than 70 runs in their Powerplay?",
            option1Name = "SRH Arena",
            option2Name = "MI Camp",
            initialVoteCounts = listOf(65_970, 23_130),
            trendingTag = "🔥 ACCELERATING",
        ),
        TrendingPredictionDefinition(
            predictionId = "TP_02",
            category = "Player Performance",
            question = "Who will pick up more wickets during death overs tonight?",
            option1Name = "J. Bumrah",
            option2Name = "P. Cummins",
            initialVoteCounts = listOf(68_320, 43_680),
            trendingTag = "👑 HEAD-TO-HEAD",
        ),
        TrendingPredictionDefinition(
            predictionId = "TP_03",
            category = "Boundaries",
            question = "Total match sixes boundary count estimation baseline:",
            option1Name = "Over 15.5 Sixes",
            option2Name = "Under 15.5 Sixes",
            initialVoteCounts = listOf(18_810, 15_390),
        ),
        TrendingPredictionDefinition(
            predictionId = "TP_MATCH",
            category = "Match Multiplier",
            question = "Who wins tonight — RCB or KKR?",
            option1Name = "RCB",
            option2Name = "KKR",
            initialVoteCounts = listOf(52_400, 36_600),
            trendingTag = "🏟 MATCH NIGHT",
            matchId = MatchConstants.RCB_KKR_HUB,
        ),
    )

    init {
        trendingVotesRepository.ensureInitialized(allDefinitions)
    }

    fun submitVote(predictionId: String, optionIndex: Int): Boolean =
        trendingVotesRepository.submitVote(predictionId, optionIndex)

    fun observeTrending(filterCategory: String): Flow<TrendingPredictionsUiState> {
        val definitions = if (filterCategory == FILTER_ALL) {
            allDefinitions
        } else {
            allDefinitions.filter { it.category == filterCategory }
        }
        return observeDefinitions(definitions)
    }

    fun observeAllTrending(): Flow<List<PredictionInsightCard>> =
        observeDefinitions(allDefinitions)
            .map { state ->
                when (state) {
                    is TrendingPredictionsUiState.Success -> state.insights
                    else -> emptyList()
                }
            }

    private fun observeDefinitions(
        definitions: List<TrendingPredictionDefinition>,
    ): Flow<TrendingPredictionsUiState> =
        combine(
            trendingVotesRepository.userVotes,
            trendingVotesRepository.voteCounts,
        ) { userVotes, voteCounts ->
            definitions.map { definition ->
                definition.toInsightCard(
                    userVotes = userVotes,
                    voteCounts = voteCounts[definition.predictionId] ?: definition.initialVoteCounts,
                )
            }
        }
            .map { insights: List<PredictionInsightCard> ->
                val state: TrendingPredictionsUiState = TrendingPredictionsUiState.Success(insights)
                state
            }
            .onStart { emit(TrendingPredictionsUiState.Loading) }
            .catch {
                emit(TrendingPredictionsUiState.Error("Failed to sync centralized trending ledger matrices."))
            }

    private fun TrendingPredictionDefinition.toInsightCard(
        userVotes: Map<String, Int>,
        voteCounts: List<Int>,
    ): PredictionInsightCard {
        val total = voteCounts.sum().coerceAtLeast(1)
        val option1Pct = ((voteCounts[0].toFloat() / total) * 100).toInt()
        val option2Pct = 100 - option1Pct
        return PredictionInsightCard(
            predictionId = predictionId,
            category = category,
            question = question,
            option1Name = option1Name,
            option1Percentage = option1Pct,
            option2Name = option2Name,
            option2Percentage = option2Pct,
            totalVotersCount = formatVoteTotal(total),
            trendingTag = trendingTag,
            matchId = matchId,
            userSelectedOptionIndex = userVotes[predictionId],
        )
    }

    private fun formatVoteTotal(total: Int): String = when {
        total >= 1_000_000 -> String.format(Locale.US, "%.1fM Fans Voted", total / 1_000_000f)
        total >= 1_000 -> String.format(Locale.US, "%.1fK Fans Voted", total / 1_000f)
        else -> "$total Fans Voted"
    }

    companion object {
        const val FILTER_ALL = "All"
    }
}
