package com.example.jetpacktutorial.feature.leaderboard

import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.common.BaseViewModel
import com.example.jetpacktutorial.core.common.Resource

import com.example.jetpacktutorial.core.data.model.LeaderboardType
import com.example.jetpacktutorial.core.data.model.LeaderboardUser
import com.example.jetpacktutorial.core.data.repository.AuthRepository
import com.example.jetpacktutorial.core.data.repository.LeaderboardRepository
import com.google.protobuf.LazyStringArrayList.emptyList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    // Currently active tab
    private val _selectedType = MutableStateFlow(LeaderboardType.GLOBAL)

    init {
        observeLeaderboard()
    }

    fun onEvent(event: LeaderboardEvent) {
        when (event) {
            is LeaderboardEvent.TabSelected -> {
                _selectedType.value = event.type
                _uiState.update { it.copy(selectedType = event.type) }
                observeLeaderboard()
            }
            is LeaderboardEvent.Refresh -> observeLeaderboard()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeLeaderboard() {
        viewModelScope.launch {
            leaderboardRepository
                .observeLeaderboard(_selectedType.value)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success -> {

                            val entries = resource.data

                            val myUserId = authRepository.currentUser?.uid

                            val myEntry = entries?.firstOrNull {
                                it.userId == myUserId
                            }

                            _uiState.update {
                                it.copy(
                                    isLoading    = false,
                                    entries      = entries as List<LeaderboardUser>,
                                    myEntry      = myEntry as LeaderboardUser?,
                                    error        = null
                                )
                            }
                        }
                        is Resource.Error -> _uiState.update {
                            it.copy(isLoading = false, error = resource.message)
                        }
                    }
                }
        }
    }
}
