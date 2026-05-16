package com.example.jetpacktutorial.feature.prediction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.model.UserMatchPrediction
import com.example.jetpacktutorial.core.data.repository.PredictionRepository
import com.example.jetpacktutorial.core.data.repository.SubmittedPredictionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PredictionViewModel @Inject constructor(
    repository: PredictionRepository,
    private val submittedPredictionsRepository: SubmittedPredictionsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val matchId: String = savedStateHandle.get<String>("matchId") ?: DEFAULT_MATCH_ID

    val uiState: StateFlow<PredictionUiState> = repository.getPredictionOptions(matchId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PredictionUiState.Loading,
        )

    val existingPrediction: StateFlow<UserMatchPrediction?> =
        submittedPredictionsRepository.predictions
            .map { predictions -> predictions[matchId] }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = submittedPredictionsRepository.getPrediction(matchId),
            )

    private val _submissionResult = MutableSharedFlow<Boolean>()
    val submissionResult: SharedFlow<Boolean> = _submissionResult

    fun submitPredictions(
        winner: String?,
        scorerId: String?,
        scorerName: String?,
        bowlerId: String?,
        bowlerName: String?,
    ) {
        viewModelScope.launch {
            if (
                winner != null &&
                scorerId != null &&
                scorerName != null &&
                bowlerId != null &&
                bowlerName != null
            ) {
                delay(500)
                submittedPredictionsRepository.save(
                    UserMatchPrediction(
                        matchId = matchId,
                        winnerTeam = winner,
                        topScorerId = scorerId,
                        topScorerName = scorerName,
                        topBowlerId = bowlerId,
                        topBowlerName = bowlerName,
                    ),
                )
                _submissionResult.emit(true)
            }
        }
    }

    companion object {
        const val DEFAULT_MATCH_ID = "M_RCB_KKR_01"
    }
}
