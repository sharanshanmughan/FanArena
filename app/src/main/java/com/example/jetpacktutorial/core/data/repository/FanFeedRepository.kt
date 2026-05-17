package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.FeedItem
import com.example.jetpacktutorial.core.data.model.FeedType
import com.example.jetpacktutorial.feature.feed.FanFeedUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class FanFeedRepository @Inject constructor() {

    // Holding hub for dynamic user-generated posts
    private val customUserFeeds = MutableStateFlow<List<FeedItem>>(emptyList())

    fun getFanFeed(): Flow<FanFeedUiState> = flow {
        // Step 1: Immediately emit the initial Loading state
        emit(FanFeedUiState.Loading)

        try {
            delay(1100) // Network latency simulation

            // Step 2: Establish base static feeds
            val mockFeed = listOf(
                FeedItem(
                    id = "feed_1",
                    type = FeedType.MEME,
                    authorName = "MemeCricket_HQ",
                    authorAvatar = "av_meme",
                    timestamp = "12m ago",
                    caption = "When you realize your team needs to win the remaining 6 matches with a Net Run Rate of +4.5 to qualify... 💀",
                    mediaUrl = "meme_placeholder_image",
                    likesCount = 4210,
                    commentsCount = 380,
                    isLiked = true
                ),
                FeedItem(
                    id = "feed_2",
                    type = FeedType.POLL,
                    authorName = "ArenaStats",
                    authorAvatar = "av_stats",
                    timestamp = "45m ago",
                    caption = "Who nailed the best execution over during death overs last week?",
                    pollOptions = listOf("Bumrah", "Pathirana", "Natarajan"),
                    pollVotes = listOf(14200, 9800, 3100),
                    likesCount = 1890,
                    commentsCount = 920
                ),
                FeedItem(
                    id = "feed_3",
                    type = FeedType.TEXT_UPDATE,
                    authorName = "Thala_Fan_Club",
                    authorAvatar = "av_thala",
                    timestamp = "2h ago",
                    caption = "Chinnaswamy stadium completely painting itself yellow tonight. The atmosphere is absolutely electric! 💛 #IPL2026",
                    likesCount = 8900,
                    commentsCount = 1150
                )
            )

            // Step 3: CRITICAL FIX — Combine our static feeds with the dynamic custom state flow stream!
            // Every time customUserFeeds changes, this combine block triggers and emits a brand-new Success state wrapper.
            customUserFeeds.collect { customList ->
                val combinedList = customList + mockFeed
                emit(FanFeedUiState.Success(combinedList))
            }

        } catch (e: IOException) {
            emit(FanFeedUiState.Error("Failed to update Fan Feed engine parameters."))
        }
    }

    fun addNewFeedItem(newItem: FeedItem) {
        // Prepends newly created custom posts instantly right above the base items sequence
        customUserFeeds.value = listOf(newItem) + customUserFeeds.value
    }
}