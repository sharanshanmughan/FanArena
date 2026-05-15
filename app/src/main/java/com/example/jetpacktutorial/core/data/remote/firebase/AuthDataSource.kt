package com.example.jetpacktutorial.core.data.remote.firebase

import com.example.jetpacktutorial.core.constants.FirebaseConstants
import com.example.jetpacktutorial.core.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    val currentUser: FirebaseUser? get() = auth.currentUser

    /** Observe auth state changes */
    fun observeAuthState(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser != null) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /** Sign in with Google ID token */
    suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val firebaseUser = result.user ?: error("Firebase user is null after sign-in")

        val user = User(
            uid        = firebaseUser.uid,
            displayName = firebaseUser.displayName ?: "Fan",
            email       = firebaseUser.email ?: "",
            photoUrl    = firebaseUser.photoUrl?.toString() ?: "",
            isGuest     = false
        )
        upsertUserDocument(user)
        user
    }

    /** Anonymous guest sign-in */
    suspend fun signInAsGuest(): Result<User> = runCatching {
        val result = auth.signInAnonymously().await()
        val firebaseUser = result.user ?: error("Firebase user is null")
        User(uid = firebaseUser.uid, displayName = "Guest", isGuest = true)
    }

    /** Sign out */
    suspend fun signOut() {
        auth.signOut()
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private suspend fun upsertUserDocument(user: User) {
        val ref = firestore.collection(FirebaseConstants.USERS_COLLECTION).document(user.uid)
        val snapshot = ref.get().await()
        if (!snapshot.exists()) {
            ref.set(user).await()
        }
    }
}
