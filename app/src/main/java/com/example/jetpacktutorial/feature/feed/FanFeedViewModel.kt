package com.example.jetpacktutorial.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktutorial.core.data.model.FeedItem
import com.example.jetpacktutorial.core.data.model.FeedType
import com.example.jetpacktutorial.core.data.repository.FanFeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FanFeedViewModel @Inject constructor(
    private val repository: FanFeedRepository
) : ViewModel() {

    val uiState: StateFlow<FanFeedUiState> = repository.getFanFeed()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FanFeedUiState.Loading
        )

    fun toggleLike(itemId: String) {
        // Handle database synchronization logic or telemetry event dispatch mapping
    }

    fun castFeedVote(itemId: String, optionIndex: Int) {
        // Register decentralized node fan polling mutations
    }

    fun publishFanPost(captionText: String, postType: FeedType, options: List<String>? = null) {
        val newItem = FeedItem(
            id = UUID.randomUUID().toString(),
            authorName = "You (Arena Fan)", // Dynamic fallback profile identity representation
            timestamp = "Just Now",
            caption = captionText,
            authorAvatar = "",
            type = postType,
            likesCount = 0,
            commentsCount = 0,
            isLiked = false,
            pollOptions = if (postType == FeedType.POLL) options?.filter { it.isNotBlank() } else null
        )

        // Push onto repository stream pipeline sequence
        repository.addNewFeedItem(newItem)
    }
}