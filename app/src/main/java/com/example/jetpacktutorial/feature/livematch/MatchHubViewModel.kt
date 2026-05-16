package com.example.jetpacktutorial.feature.livematch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.constants.MatchConstants
import com.example.jetpacktutorial.core.data.model.InteractivePollCard
import com.example.jetpacktutorial.core.data.model.UserMatchPrediction
import com.example.jetpacktutorial.core.data.repository.FanPollsRepository
import com.example.jetpacktutorial.core.data.repository.MatchHubRepository
import com.example.jetpacktutorial.core.data.repository.SubmittedPredictionsRepository
import com.example.jetpacktutorial.feature.fanPoll.FanPollsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MatchHubViewModel @Inject constructor(
    repository: MatchHubRepository,
   private val fanPollsRepository: FanPollsRepository,
    submittedPredictionsRepository: SubmittedPredictionsRepository,
) : ViewModel() {

    val matchId: String = MatchConstants.RCB_KKR_HUB

    val uiState: StateFlow<MatchHubUiState> = repository.getMatchHubDetails(matchId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MatchHubUiState.Loading,
        )

    val matchPolls: StateFlow<List<InteractivePollCard>> =
        fanPollsRepository.observeMatchPolls(matchId)
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

    val userPrediction: StateFlow<UserMatchPrediction?> =
        submittedPredictionsRepository.predictions
            .map { predictions -> predictions[matchId] }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = submittedPredictionsRepository.getPrediction(matchId),
            )

    fun submitPollVote(pollId: String, optionIndex: Int) {
        fanPollsRepository.submitVote(pollId, optionIndex)
    }

    companion object {
        const val DEFAULT_MATCH_ID = MatchConstants.RCB_KKR_HUB
    }
}
