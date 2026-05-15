package com.example.jetpacktutorial.feature.arenaMasters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.repository.TopMastersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TopMastersViewModel @Inject constructor(repository: TopMastersRepository) : ViewModel() {

    val uiState: StateFlow<TopMastersUiState> = repository.getTopArenaMasters()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TopMastersUiState.Loading
        )
}