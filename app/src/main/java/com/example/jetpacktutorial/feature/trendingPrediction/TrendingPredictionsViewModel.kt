package com.example.jetpacktutorial.feature.trendingPrediction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.repository.TrendingPredictionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class TrendingPredictionsViewModel @Inject constructor(
    repository: TrendingPredictionsRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter

    val uiState: StateFlow<TrendingPredictionsUiState> = _selectedFilter
        .flatMapLatest { filter -> repository.getTrendingPredictions(filter) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TrendingPredictionsUiState.Loading
        )

    fun changeFilter(category: String) {
        viewModelScope.launch {
            _selectedFilter.value = category
        }
    }
}