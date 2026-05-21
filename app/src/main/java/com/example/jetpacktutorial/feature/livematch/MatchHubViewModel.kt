package com.example.jetpacktutorial.feature.livematch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.constants.MatchConstants
import com.example.jetpacktutorial.core.data.model.InteractivePollCard
import com.example.jetpacktutorial.core.data.model.UserMatchPrediction
import com.example.jetpacktutorial.core.data.repository.DiscussionRepository
import com.example.jetpacktutorial.core.data.repository.FanPollsRepository
import com.example.jetpacktutorial.core.data.repository.MatchHubRepository
import com.example.jetpacktutorial.core.data.repository.SubmittedPredictionsRepository
import com.example.jetpacktutorial.feature.fanPoll.FanPollsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchHubViewModel @Inject constructor(
    repository: MatchHubRepository,
    private val fanPollsRepository: FanPollsRepository,
    private val discussionRepository: DiscussionRepository,
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

    val discussionComments: StateFlow<List<com.example.jetpacktutorial.core.data.model.Comment>> =
        discussionRepository.observeMatchDiscussion(matchId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

    private val _discussionPostState = MutableStateFlow<DiscussionPostState>(DiscussionPostState.Idle)
    val discussionPostState: StateFlow<DiscussionPostState> = _discussionPostState.asStateFlow()

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

    fun postDiscussionComment(text: String, supportTeamBadge: String) {
        if (discussionRepository.currentUser() == null) {
            _discussionPostState.value = DiscussionPostState.Error("Sign in to join the discussion.")
            return
        }

        viewModelScope.launch {
            _discussionPostState.value = DiscussionPostState.Sending
            discussionRepository.postComment(
                matchId = matchId,
                text = text,
                supportTeamBadge = supportTeamBadge,
            ).onSuccess {
                _discussionPostState.value = DiscussionPostState.Sent
                _discussionPostState.value = DiscussionPostState.Idle
            }.onFailure { error ->
                _discussionPostState.value = DiscussionPostState.Error(
                    error.message ?: "Could not post comment.",
                )
            }
        }
    }

    fun clearDiscussionPostError() {
        if (_discussionPostState.value is DiscussionPostState.Error) {
            _discussionPostState.value = DiscussionPostState.Idle
        }
    }

    companion object {
        const val DEFAULT_MATCH_ID = MatchConstants.RCB_KKR_HUB
    }
}

sealed interface DiscussionPostState {
    data object Idle : DiscussionPostState
    data object Sending : DiscussionPostState
    data object Sent : DiscussionPostState
    data class Error(val message: String) : DiscussionPostState
}
