package com.example.jetpacktutorial.core.data.model

import com.google.firebase.firestore.DocumentId

// ── PollOption.kt ─────────────────────────────────────────────────────────────

data class PollOption(
    val id: String = "",
    val label: String = "",             // "India 🇮🇳", "Agree", "Over 2.5"
    val iconUrl: String = "",
    val emoji: String = "",
    val voteCount: Int = 0,
    val teamId: String = ""
)

// ── LivePoll.kt ───────────────────────────────────────────────────────────────

data class LivePoll(
    @DocumentId
    val id: String = "",
    val matchId: String = "",
    val question: String = "",          // "Who will win the next over?"
    val options: List<PollOption> = emptyList(),
    val isActive: Boolean = true,
    val endsAt: Long = 0L,             // epoch ms — auto-close after this
    val createdAt: Long = System.currentTimeMillis(),
    val resultVisible: Boolean = false, // show vote distribution after voting
    val xpRewardForVoting: Int = 5,
    val totalVotes: Int = 0,
    val triggeredByEvent: String = ""   // e.g. "WICKET" — poll auto-created on event
) {
    val isExpired: Boolean get() = System.currentTimeMillis() > endsAt && endsAt > 0
    val isOpen: Boolean get() = isActive && !isExpired

    fun percentageFor(optionId: String): Float {
        if (totalVotes == 0) return 0f
        val option = options.firstOrNull { it.id == optionId } ?: return 0f
        return (option.voteCount.toFloat() / totalVotes) * 100f
    }
}

// ── PollVote.kt ───────────────────────────────────────────────────────────────

data class PollVote(
    val id: String = "",                // "${userId}_${pollId}"
    val userId: String = "",
    val pollId: String = "",
    val matchId: String = "",
    val optionId: String = "",
    val votedAt: Long = System.currentTimeMillis()
)
