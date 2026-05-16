package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.TrendingPredictionDefinition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrendingVotesRepository @Inject constructor() {

    private val _userVotes = MutableStateFlow<Map<String, Int>>(emptyMap())
    val userVotes: StateFlow<Map<String, Int>> = _userVotes.asStateFlow()

    private val _voteCounts = MutableStateFlow<Map<String, List<Int>>>(emptyMap())
    val voteCounts: StateFlow<Map<String, List<Int>>> = _voteCounts.asStateFlow()

    fun ensureInitialized(definitions: List<TrendingPredictionDefinition>) {
        if (_voteCounts.value.isNotEmpty()) return
        _voteCounts.value = definitions.associate { it.predictionId to it.initialVoteCounts }
    }

    fun submitVote(predictionId: String, optionIndex: Int): Boolean {
        if (_userVotes.value.containsKey(predictionId)) return false
        val counts = _voteCounts.value[predictionId] ?: return false
        if (optionIndex !in counts.indices) return false

        _userVotes.update { it + (predictionId to optionIndex) }
        _voteCounts.update { current ->
            val updated = current.toMutableMap()
            val mutable = counts.toMutableList()
            mutable[optionIndex] = mutable[optionIndex] + 1
            updated[predictionId] = mutable
            updated
        }
        return true
    }
}
