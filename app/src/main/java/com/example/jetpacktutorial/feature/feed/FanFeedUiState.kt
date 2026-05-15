package com.example.jetpacktutorial.feature.feed

import com.example.jetpacktutorial.core.data.model.FeedItem

sealed interface FanFeedUiState {
    object Loading : FanFeedUiState
    data class Success(val feedItems: List<FeedItem>) : FanFeedUiState
    data class Error(val message: String) : FanFeedUiState
}