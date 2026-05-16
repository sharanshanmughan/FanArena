package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.FanPollDefinition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PollVotesRepository @Inject constructor() {

    private val _userVotes = MutableStateFlow<Map<String, Int>>(emptyMap())
    val userVotes: StateFlow<Map<String, Int>> = _userVotes.asStateFlow()

    private val _voteCounts = MutableStateFlow<Map<String, List<Int>>>(emptyMap())
    val voteCounts: StateFlow<Map<String, List<Int>>> = _voteCounts.asStateFlow()

    fun ensureInitialized(definitions: List<FanPollDefinition>) {
        if (_voteCounts.value.isNotEmpty()) return
        _voteCounts.value = definitions.associate { it.pollId to it.initialVoteCounts }
    }

    fun submitVote(pollId: String, optionIndex: Int): Boolean {
        if (_userVotes.value.containsKey(pollId)) return false

        val counts = _voteCounts.value[pollId] ?: return false
        if (optionIndex !in counts.indices) return false

        _userVotes.update { current -> current + (pollId to optionIndex) }
        _voteCounts.update { current ->
            val updatedCounts = current.toMutableMap()
            val mutable = counts.toMutableList()
            mutable[optionIndex] = mutable[optionIndex] + 1
            updatedCounts[pollId] = mutable
            updatedCounts
        }
        return true
    }
}
