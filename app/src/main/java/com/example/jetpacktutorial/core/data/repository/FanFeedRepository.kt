package com.example.jetpacktutorial.core.data.repository

import com.example.jetpacktutorial.core.data.model.FeedItem
import com.example.jetpacktutorial.core.data.model.FeedType
import com.example.jetpacktutorial.feature.feed.FanFeedUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class FanFeedRepository @Inject constructor() {
    fun getFanFeed(): Flow<FanFeedUiState> = flow {
        emit(FanFeedUiState.Loading)
        try {
            delay(1100) // Network latency simulation

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
            emit(FanFeedUiState.Success(mockFeed))
        } catch (e: IOException) {
            emit(FanFeedUiState.Error("Failed to update Fan Feed engine parameters."))
        }
    }
}