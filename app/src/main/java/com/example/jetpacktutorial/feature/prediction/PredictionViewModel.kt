package com.example.jetpacktutorial.feature.prediction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.repository.PredictionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PredictionViewModel @Inject constructor(
     repository: PredictionRepository,

) : ViewModel() {

    val uiState: StateFlow<PredictionUiState> = repository.getPredictionOptions("M_RCB_KKR_01")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PredictionUiState.Loading
        )

    private val _submissionResult = MutableSharedFlow<Boolean>()
    val submissionResult: SharedFlow<Boolean> = _submissionResult

    fun submitPredictions(winner: String?, scorerId: String?, bowlerId: String?) {
        viewModelScope.launch {
            // Check if all forms are populated before pushing backend tracking updates
            if (winner != null && scorerId != null && bowlerId != null) {
                // Mock network post latency
                delay(500)
                _submissionResult.emit(true)
            }
        }
    }
}