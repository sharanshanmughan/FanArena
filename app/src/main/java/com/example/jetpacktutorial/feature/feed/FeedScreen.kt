package com.example.jetpacktutorial.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpacktutorial.core.data.model.FeedItem
import com.example.jetpacktutorial.core.data.model.FeedType
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.feature.home.LoadingSkeleton
import com.example.jetpacktutorial.feature.home.glassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FanFeedScreen() {
    val viewModel: FanFeedViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Fan Feed", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Background blur accent
            Box(modifier = Modifier.size(280.dp).align(Alignment.BottomEnd).blur(110.dp).background(Color(0xFF8E24AA).copy(alpha = 0.12f)))

            when (val currentState = state) {
                is FanFeedUiState.Loading -> LoadingSkeleton()
                is FanFeedUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(currentState.message, color = Color.Red) }
                is FanFeedUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(currentState.feedItems, key = { it.id }) { item ->
                            FeedCard(
                                item = item,
                                onLikeClicked = { viewModel.toggleLike(item.id) },
                                onVoteSelected = { index -> viewModel.castFeedVote(item.id, index) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedCard(
    item: FeedItem,
    onLikeClicked: () -> Unit,
    onVoteSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCard()
            .padding(14.dp)
    ) {
        // Card Header (User profile details row layout)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f)), contentAlignment = Alignment.Center) {
                Text(item.authorName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(item.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(item.timestamp, color = Color.Gray, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Caption Space
        Text(item.caption, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, lineHeight = 20.sp)

        Spacer(modifier = Modifier.height(12.dp))

        // Contextual Content Layout Distribution Router
        when (item.type) {
            FeedType.MEME -> {
                // Mock Media Image Box layout placeholder representing Instagram-style element
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, BorderGlass, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("[ 🖼️ High-Res Arena Meme Asset ]", color = Color.Gray, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            FeedType.POLL -> {
                item.pollOptions?.forEachIndexed { index, option ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.03f))
                            .border(1.dp, BorderGlass, RoundedCornerShape(10.dp))
                            .clickable { onVoteSelected(index) }
                            .padding(12.dp)
                    ) {
                        Text(option, color = Color.White, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            FeedType.TEXT_UPDATE -> { /* Text updates directly display captions */ }
        }

        // Inline Quick Emoji Reaction Node Widget
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("🔥", "😂", "😮", "👑").forEach { emoji ->
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.06f), CircleShape)
                        .clickable { }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(emoji, fontSize = 12.sp)
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = BorderGlass
        )

        // Action Interactions Bar Footnote
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Like Button layout structure
                IconButton (onClick = onLikeClicked, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (item.isLiked) Color.Red else Color.Gray
                    )
                }
                Text("${item.likesCount}", color = Color.Gray, fontSize = 13.sp)

                // Comment Button interaction mapping frame setup
                Box(modifier = Modifier.size(24.dp).clickable { }, contentAlignment = Alignment.Center) {
                    Text("💬", fontSize = 14.sp)
                }
                Text("${item.commentsCount}", color = Color.Gray, fontSize = 13.sp)
            }

            IconButton(onClick = { }, modifier = Modifier.size(24.dp)) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.Gray)
            }
        }
    }
}

