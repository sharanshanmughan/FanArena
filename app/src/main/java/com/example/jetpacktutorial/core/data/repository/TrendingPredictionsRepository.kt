package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.PredictionInsightCard
import com.example.jetpacktutorial.core.data.model.TrendingPredictionDefinition
import com.example.jetpacktutorial.core.data.remote.firebase.TrendingPredictionsFirestoreDataSource
import com.example.jetpacktutorial.feature.trendingPrediction.TrendingPredictionsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrendingPredictionsRepository @Inject constructor(
    private val firestoreDataSource: TrendingPredictionsFirestoreDataSource,
    private val trendingVotesRepository: TrendingVotesRepository,
) {

    private val loadMutex = Mutex()
    private var cachedDefinitions: List<TrendingPredictionDefinition>? = null

    fun submitVote(predictionId: String, optionIndex: Int): Boolean =
        trendingVotesRepository.submitVote(predictionId, optionIndex)

    fun observeTrending(filterCategory: String): Flow<TrendingPredictionsUiState> = flow {
        emit(TrendingPredictionsUiState.Loading)
        val definitions = loadDefinitions()
        if (definitions.isEmpty()) {
            emit(TrendingPredictionsUiState.Error("No trending predictions in Firestore. Add documents to trending_predictions."))
            return@flow
        }
        val filtered = if (filterCategory == FILTER_ALL) {
            definitions
        } else {
            definitions.filter { it.category == filterCategory }
        }
        observeDefinitions(filtered).collect { emit(it) }
    }

    fun observeAllTrending(): Flow<List<PredictionInsightCard>> =
        loadDefinitionsFlow()
            .flatMapLatest { definitions ->
                if (definitions.isEmpty()) {
                    flow { emit(emptyList()) }
                } else {
                    observeDefinitions(definitions).map { state ->
                        when (state) {
                            is TrendingPredictionsUiState.Success -> state.insights
                            else -> emptyList()
                        }
                    }
                }
            }

    private fun loadDefinitionsFlow(): Flow<List<TrendingPredictionDefinition>> = flow {
        emit(loadDefinitions())
    }

    private suspend fun loadDefinitions(): List<TrendingPredictionDefinition> = loadMutex.withLock {
        cachedDefinitions?.let { return it }
        val loaded = firestoreDataSource.getActiveTrendingPredictions()
        if (loaded.isNotEmpty()) {
            trendingVotesRepository.ensureInitialized(loaded)
            cachedDefinitions = loaded
        }
        loaded
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
        }.map { insights -> insights.asTrendingSuccess() }

    private fun List<PredictionInsightCard>.asTrendingSuccess(): TrendingPredictionsUiState =
        TrendingPredictionsUiState.Success(this)



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

    /** Call after admin updates Firestore to refresh the list. */
    suspend fun refreshFromFirestore() {
        loadMutex.withLock {
            cachedDefinitions = null
            loadDefinitions()
        }
    }

    companion object {
        const val FILTER_ALL = "All"
    }
}
