package com.example.jetpacktutorial.core.utils

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.jetpacktutorial.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.io.IOException
import javax.inject.Inject

class GoogleAuthManager @Inject constructor() {

    suspend fun signIn(
        activity: Activity
    ): Result<String> = runCatching {

        val credentialManager =
            CredentialManager.create(activity)

        val googleIdOption =
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(
                    BuildConfig.WEB_CLIENT_ID
                )
                .build()

        val request =
            GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

        val result = credentialManager.getCredential(
            context = activity,
            request = request
        )

        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            val googleCredential =
                GoogleIdTokenCredential.createFrom(
                    credential.data
                )

            googleCredential.idToken

        } else {

            error("Google credential failed")
        }
    }.recoverCatching { throwable ->
        throw IOException(mapGoogleSignInError(throwable), throwable)
    }

    private fun mapGoogleSignInError(throwable: Throwable): String {
        val message = throwable.message.orEmpty()
        return when {
            throwable is SecurityException ||
                message.contains("Unknown calling package name", ignoreCase = true) ||
                message.contains("Failed to get service from broker", ignoreCase = true) ->
                "Google Play Services is unavailable. Use an emulator or device with the " +
                    "Google Play Store, update Google Play Services, then try again. " +
                    "You can use Guest sign-in to test Firestore in the meantime."

            message.contains("NETWORK_ERROR", ignoreCase = true) ->
                "Network error during Google sign-in. Check your connection."

            else -> throwable.message ?: "Google sign-in failed"
        }
    }
}