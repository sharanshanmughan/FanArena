package com.example.jetpacktutorial.feature.trendingPrediction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.repository.TrendingPredictionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class TrendingPredictionsViewModel @Inject constructor(
    private val repository: TrendingPredictionsRepository,
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TrendingPredictionsRepository.FILTER_ALL)
    val selectedFilter: StateFlow<String> = _selectedFilter

    val uiState: StateFlow<TrendingPredictionsUiState> = _selectedFilter
        .flatMapLatest { filter -> repository.observeTrending(filter) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TrendingPredictionsUiState.Loading,
        )

    fun changeFilter(category: String) {
        _selectedFilter.value = category
    }

    fun submitTrendingVote(predictionId: String, optionIndex: Int) {
        repository.submitVote(predictionId, optionIndex)
    }
}
