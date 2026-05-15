package com.example.jetpacktutorial.feature.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.model.User
import com.example.jetpacktutorial.core.data.repository.AuthRepository
import com.example.jetpacktutorial.core.utils.GoogleAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val googleAuthManager: GoogleAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {

        when (event) {

            is LoginEvent.GoogleSignIn -> {
                signInWithGoogle(event.activity)
            }

            LoginEvent.GuestSignIn -> {
                signInAsGuest()
            }

            LoginEvent.DismissError -> {

                _uiState.update {
                    it.copy(error = null)
                }
            }
        }
    }



    private fun signInWithGoogle(
        activity: Activity
    ) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            googleAuthManager
                .signIn(activity)
                .onSuccess { token ->
                    Log.d("TOKEN", token)
                    authRepository
                        .signInWithGoogle(token)
                        .onSuccess {

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isAuthenticated = true
                                )
                            }
                        }
                        .onFailure { error ->
                            Log.d("TOKEN", "onFailure $error")
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = error.message
                                )
                            }
                        }
                }
                .onFailure { error ->
                    Log.d("TOKEN", "onFailure2 $error")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    private fun signInAsGuest() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isGuestLoading = true,
                    error = null
                )
            }

            authRepository
                .signInAsGuest()
                .onSuccess {

                    _uiState.update {
                        it.copy(
                            isGuestLoading = false,
                            isAuthenticated = true
                        )
                    }
                }
                .onFailure { error ->

                    _uiState.update {
                        it.copy(
                            isGuestLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }
}