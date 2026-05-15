package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.common.Resource
import com.example.jetpacktutorial.core.data.model.User
import com.example.jetpacktutorial.core.data.remote.firebase.AuthDataSource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authDataSource: AuthDataSource
) {

    val currentUser: FirebaseUser?
        get() = authDataSource.currentUser

    fun observeAuthState(): Flow<Boolean> {
        return authDataSource.observeAuthState()
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return authDataSource.signInWithGoogle(idToken)
    }

    suspend fun signInAsGuest(): Result<User> {
        return authDataSource.signInAsGuest()
    }

    suspend fun signOut() {
        authDataSource.signOut()
    }
}