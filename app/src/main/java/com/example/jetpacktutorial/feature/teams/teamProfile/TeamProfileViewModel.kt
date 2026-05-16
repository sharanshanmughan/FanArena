package com.example.jetpacktutorial.feature.teams

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.model.TeamProfileDetails
import com.example.jetpacktutorial.core.data.repository.TeamProfileRepository
import com.example.jetpacktutorial.feature.teams.teamProfile.TeamProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamProfileViewModel @Inject constructor(
    private val repository: TeamProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 1. Extract navigation parameter string argument
    val teamId: String = checkNotNull(savedStateHandle["teamId"])

    // 2. Track local UI-driven user loyalty state overrides
    private val _isUserInFanCampLocal = MutableStateFlow(false)

    // 3. Combine repository data flow with the local user action interactions state
    val uiState: StateFlow<TeamProfileUiState> = repository.getTeamProfileDetails(teamId)
        .combine(_isUserInFanCampLocal) { remoteDetails, localCampState ->
            // Map our domain data objects dynamically while merging the local camp override state
            TeamProfileUiState.Success(
                TeamProfileDetails(
                    teamInfo = remoteDetails.teamInfo,
                    isUserInFanCamp = localCampState, // Injected local state toggling mutation
                    squadList = remoteDetails.squadList,
                    pastResults = remoteDetails.pastResults
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TeamProfileUiState.Loading
        )

    /**
     * Resolves the "Unresolved reference: toggleFanCamp" issue.
     * Toggles the user's loyalty status for this franchise local state.
     */
    fun toggleFanCamp() {
        viewModelScope.launch {
            _isUserInFanCampLocal.value = !_isUserInFanCampLocal.value

            // NOTE: In a production database setup, you would fire a repository background post here:
            // repository.updateUserFanCampMembership(teamId, _isUserInFanCampLocal.value)
        }
    }
}