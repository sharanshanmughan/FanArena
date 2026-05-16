package com.example.jetpacktutorial.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.model.InteractivePollCard
import com.example.jetpacktutorial.core.data.model.PredictionInsightCard
import com.example.jetpacktutorial.core.data.model.UserMatchPrediction
import com.example.jetpacktutorial.core.data.repository.FanPollsRepository
import com.example.jetpacktutorial.core.data.repository.HomeRepository
import com.example.jetpacktutorial.core.data.repository.SubmittedPredictionsRepository
import com.example.jetpacktutorial.core.data.repository.TrendingPredictionsRepository
import com.example.jetpacktutorial.feature.fanPoll.FanPollsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: HomeRepository,
    private val fanPollsRepository: FanPollsRepository,
    private val trendingPredictionsRepository: TrendingPredictionsRepository,
    submittedPredictionsRepository: SubmittedPredictionsRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.getHomeData()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading,
        )

    val arenaPolls: StateFlow<List<InteractivePollCard>> = fanPollsRepository.observeArenaPolls()
        .map { state ->
            when (state) {
                is FanPollsUiState.Success -> state.activePolls
                else -> emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val trendingInsights: StateFlow<List<PredictionInsightCard>> =
        trendingPredictionsRepository.observeAllTrending()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

    val submittedPredictions: StateFlow<Map<String, UserMatchPrediction>> =
        submittedPredictionsRepository.predictions
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap(),
            )

    fun submitPollVote(pollId: String, optionIndex: Int) {
        fanPollsRepository.submitVote(pollId, optionIndex)
    }

    fun submitTrendingVote(predictionId: String, optionIndex: Int) {
        trendingPredictionsRepository.submitVote(predictionId, optionIndex)
    }
}
