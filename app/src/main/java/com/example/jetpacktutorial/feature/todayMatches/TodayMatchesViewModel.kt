package com.example.jetpacktutorial.feature.todayMatches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.repository.TodayMatchesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TodayMatchesViewModel @Inject constructor(repository: TodayMatchesRepository) : ViewModel() {

    val uiState: StateFlow<TodayMatchesUiState> = repository.getTodayMatches()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TodayMatchesUiState.Loading
        )
}