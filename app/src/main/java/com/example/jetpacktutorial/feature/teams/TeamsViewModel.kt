package com.example.jetpacktutorial.feature.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.repository.TeamsRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(repository: TeamsRepository) : ViewModel() {

    val uiState: StateFlow<TeamsUiState> = repository.getIplTeamsList()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TeamsUiState.Loading
        )
}