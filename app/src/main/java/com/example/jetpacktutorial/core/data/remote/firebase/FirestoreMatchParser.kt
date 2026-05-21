package com.example.jetpacktutorial.core.data.remote.firebase

import android.util.Log
import com.example.jetpacktutorial.core.data.model.Match
import com.google.firebase.firestore.QuerySnapshot

private const val TAG = "FirestoreMatches"

/**
 * Parses match documents one-by-one so a single bad field does not drop the whole list.
 * Firestore numbers are Long in the SDK — use [Match.sortOrder] as Long, not Int.
 */
fun QuerySnapshot.toMatches(): List<Match> {
    Log.d(TAG, "Raw document count: ${size()}")
    return documents.mapNotNull { doc ->
        runCatching {
            doc.toObject(Match::class.java)?.copy(id = doc.id)
        }.onFailure { error ->
            Log.w(TAG, "Parse failed for '${doc.id}': ${error.message}")
        }.getOrNull()
    }.also { parsed ->
        Log.d(TAG, "Parsed match count: ${parsed.size}")
    }
}
