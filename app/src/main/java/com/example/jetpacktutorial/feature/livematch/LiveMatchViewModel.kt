package com.example.jetpacktutorial.feature.livematch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.common.BaseViewModel
import com.example.jetpacktutorial.core.common.Resource
import com.example.jetpacktutorial.core.data.model.PollVote
import com.example.jetpacktutorial.core.data.repository.MatchRepository
import com.example.jetpacktutorial.core.data.repository.PollRepository
import com.example.jetpacktutorial.core.data.model.Prediction
import com.example.jetpacktutorial.core.data.repository.AuthRepository

import com.example.jetpacktutorial.core.data.repository.PredictionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MATCH_ID_KEY = "matchId"

@HiltViewModel
class LiveMatchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository,
    private val predictionRepository: PredictionRepository,
    private val pollRepository: PollRepository,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val matchId: String = checkNotNull(savedStateHandle[MATCH_ID_KEY])

    private val _uiState = MutableStateFlow(LiveMatchUiState())
    val uiState: StateFlow<LiveMatchUiState> = _uiState.asStateFlow()

    init {
        observeMatch()
        observeLiveScore()
        observeMatchEvents()
        observePolls()
        loadUserPredictions()
    }

    // ── Public event handler ─────────────────────────────────────────────────

    fun onEvent(event: LiveMatchEvent) {
        when (event) {
            is LiveMatchEvent.SubmitPrediction -> submitPrediction(event.prediction)
            is LiveMatchEvent.CastPollVote     -> castVote(event.vote)
            is LiveMatchEvent.DismissError     -> _uiState.update { it.copy(error = null) }
        }
    }

    // ── Private observers ────────────────────────────────────────────────────

    private fun observeMatch() {
        viewModelScope.launch {
            matchRepository.observeMatch(matchId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> _uiState.update { it.copy(isLoading = false, match = resource.data) }
                    is Resource.Error   -> _uiState.update { it.copy(isLoading = false, error = resource.message) }
                }
            }
        }
    }

    private fun observeLiveScore() {
        viewModelScope.launch {
            matchRepository.observeLiveScore(matchId)
                .catch { _uiState.update { s -> s.copy(error = it.message) } }
                .collect { score -> _uiState.update { it.copy(liveScore = score) } }
        }
    }

    private fun observeMatchEvents() {
        viewModelScope.launch {
            matchRepository.observeMatchEvents(matchId)
                .catch { /* silent */ }
                .collect { events -> _uiState.update { it.copy(matchEvents = events) } }
        }
    }

    private fun observePolls() {
        viewModelScope.launch {
            pollRepository.observeLivePolls(matchId).collect { resource ->
                if (resource is Resource.Success) {
                    _uiState.update { it.copy(polls = resource.data ?: emptyList()) }
                }
            }
        }
    }

    private fun loadUserPredictions() {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            predictionRepository.observeUserPredictions(userId, matchId).collect { resource ->
                if (resource is Resource.Success) {
                    _uiState.update { it.copy(userPredictions = resource.data ?: emptyList()) }
                }
            }
        }
    }

    // ── User actions ─────────────────────────────────────────────────────────

    private fun submitPrediction(prediction: Prediction) {
        viewModelScope.launch {
            predictionRepository.submitPrediction(prediction).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isPredicting = true) }
                    is Resource.Success -> _uiState.update { it.copy(isPredicting = false, predictionSuccess = true) }
                    is Resource.Error   -> _uiState.update { it.copy(isPredicting = false, error = resource.message) }
                }
            }
        }
    }

    private fun castVote(vote: PollVote) {
        viewModelScope.launch {
            pollRepository.castVote(vote).collect { resource ->
                if (resource is Resource.Error) {
                    _uiState.update { it.copy(error = resource.message) }
                }
            }
        }
    }
}
