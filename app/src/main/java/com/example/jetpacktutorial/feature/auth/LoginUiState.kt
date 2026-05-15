package com.example.jetpacktutorial.feature.auth

import android.app.Activity

data class LoginUiState(
    val isLoading: Boolean       = false,
    val isGuestLoading: Boolean       = false,
    val isAuthenticated: Boolean = false,
    val error: String?           = null
)

sealed class LoginEvent {
    data class GoogleSignIn(
        val activity: Activity
    ) : LoginEvent()
    data object GuestSignIn  : LoginEvent()
    data object DismissError : LoginEvent()
}
