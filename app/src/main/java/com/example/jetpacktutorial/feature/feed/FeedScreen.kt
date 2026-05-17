import com.example.jetpacktutorial.feature.feed.FanFeedUiState
import com.example.jetpacktutorial.feature.feed.FanFeedViewModel


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpacktutorial.core.data.model.FeedItem
import com.example.jetpacktutorial.core.data.model.FeedType
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.core.ui.theme.SurfaceGlass
import com.example.jetpacktutorial.feature.home.LoadingSkeleton
import com.example.jetpacktutorial.feature.home.glassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FanFeedScreen(
    viewModel: FanFeedViewModel = hiltViewModel(),
    padding: PaddingValues
) {
    val state by viewModel.uiState.collectAsState()
    var showCreatePostSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .background(DarkBackground)
            .padding(bottom = padding.calculateBottomPadding()*.6f),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Fan Arena Feed",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreatePostSheet = true },
                containerColor = Color.Transparent,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(IPLOrangeGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Create Arena Post",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Ensures content wraps inside system bars
        ) {
            // Neon mesh background glow
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.BottomEnd)
                    .blur(110.dp)
                    .background(Color(0xFF8E24AA).copy(alpha = 0.12f))
            )

            when (val currentState = state) {
                is FanFeedUiState.Loading -> LoadingSkeleton()
                is FanFeedUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(currentState.message, color = Color.Red)
                    }
                }

                is FanFeedUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 88.dp) // Gives room for FAB
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

            if (showCreatePostSheet) {
                CreatePostBottomSheet(
                    onDismissRequest = { showCreatePostSheet = false },
                    onPostSubmitted = { captionText, type, pollOptions ->
                        viewModel.publishFanPost(captionText, type, pollOptions)
                    }
                )
            }
        }
    }
}

@Composable
fun FeedCard(
    item: FeedItem,
    onLikeClicked: () -> Unit,
    onVoteSelected: (Int) -> Unit
) {
    var localIsLiked by remember(item.id) { mutableStateOf(item.isLiked) }
    var localLikesCount by remember(item.id) { mutableIntStateOf(item.likesCount) }
    var selectedPollIndex by remember(item.id) { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCard()
            .padding(14.dp)
    ) {
        // Author Info Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.authorName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    item.authorName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(item.timestamp, color = Color.Gray, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Post Caption
        Text(
            text = item.caption,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Conditional Media/Attachment Body Renderers
        when (item.type) {
            FeedType.MEME -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .border(1.dp, BorderGlass, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔥", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "[ High-Res Arena Meme Display ]",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            FeedType.POLL -> {
                item.pollOptions?.forEachIndexed { index, option ->
                    val isVoted = selectedPollIndex != null
                    val isThisOptionSelected = selectedPollIndex == index
                    val targetProgress =
                        if (isVoted) (if (isThisOptionSelected) 0.68f else 0.32f) else 0.0f

                    val animatedProgress by animateFloatAsState(
                        targetValue = targetProgress,
                        animationSpec = tween(durationMillis = 500),
                        label = "PollFillAnimation"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.03f))
                            .border(
                                width = 1.dp,
                                color = if (isThisOptionSelected) Color(0xFFFF5722) else BorderGlass,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = !isVoted) {
                                selectedPollIndex = index
                                onVoteSelected(index)
                            }
                    ) {
                        if (isVoted) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedProgress)
                                    .background(
                                        if (isThisOptionSelected) Color(0xFFFF5722).copy(alpha = 0.2f)
                                        else Color.White.copy(alpha = 0.06f)
                                    )
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                color = if (isThisOptionSelected) Color(0xFFFF5722) else Color.White,
                                fontSize = 13.sp,
                                fontWeight = if (isThisOptionSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isVoted) {
                                Text(
                                    text = "${(animatedProgress * 100).toInt()}%",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            FeedType.TEXT_UPDATE -> {}
        }

        // Quick Reaction Emoji Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("🔥", "😂", "👏", "👑").forEach { emoji ->
                var count by remember { mutableIntStateOf((12..88).random()) }
                var isSelected by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .background(
                            color = if (isSelected) Color(0xFFFF5722).copy(alpha = 0.15f) else Color.White.copy(
                                alpha = 0.05f
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFFFF5722) else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable {
                            isSelected = !isSelected
                            if (isSelected) count++ else count--
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(emoji, fontSize = 12.sp)
                        Text(
                            text = count.toString(),
                            color = if (isSelected) Color(0xFFFF5722) else Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = BorderGlass
        )

        // Lower Footer Actions Panel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = {
                            localIsLiked = !localIsLiked
                            if (localIsLiked) localLikesCount++ else localLikesCount--
                            onLikeClicked()
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (localIsLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (localIsLiked) Color.Red else Color.Gray
                        )
                    }
                    Text("$localLikesCount", color = Color.Gray, fontSize = 13.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💬", fontSize = 14.sp)
                    }
                    Text("${item.commentsCount}", color = Color.Gray, fontSize = 13.sp)
                }
            }

            IconButton(onClick = { }, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostBottomSheet(
    onDismissRequest: () -> Unit,
    onPostSubmitted: (String, FeedType, List<String>?) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(FeedType.TEXT_UPDATE) }
    var pollOption1 by remember { mutableStateOf("") }
    var pollOption2 by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color(0xFF1A122C),
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = { BottomSheetDefaults.DragHandle(color = BorderGlass) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .navigationBarsPadding()
        ) {
            Text(
                "Hype the Arena",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Choice Segments
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    FeedType.TEXT_UPDATE to "💬 Text",
                    FeedType.POLL to "📊 Poll",
                    FeedType.MEME to "🔥 Meme"
                ).forEach { (type, label) ->
                    val isCurrent = selectedType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isCurrent) Color(0xFFFF5722) else SurfaceGlass)
                            .border(
                                1.dp,
                                if (isCurrent) Color.Transparent else BorderGlass,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedType = type },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Input Field
            TextField(
                value = caption,
                onValueChange = { caption = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .border(1.dp, BorderGlass, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                placeholder = {
                    Text(
                        text = when (selectedType) {
                            FeedType.POLL -> "Ask the crowd an arena question..."
                            FeedType.MEME -> "Add a spicy caption for your meme..."
                            else -> "What is your banter for tonight's game?"
                        },
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceGlass,
                    unfocusedContainerColor = SurfaceGlass.copy(alpha = 0.5f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Dynamic Option Field Subsections for Poll Creators
            if (selectedType == FeedType.POLL) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    "Poll Choices",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = pollOption1,
                    onValueChange = { pollOption1 = it },
                    placeholder = { Text("Option 1", color = Color.Gray, fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceGlass,
                        unfocusedContainerColor = SurfaceGlass.copy(alpha = 0.4f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                TextField(
                    value = pollOption2,
                    onValueChange = { pollOption2 = it },
                    placeholder = { Text("Option 2", color = Color.Gray, fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceGlass,
                        unfocusedContainerColor = SurfaceGlass.copy(alpha = 0.4f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val canPublish =
                caption.isNotBlank() && (selectedType != FeedType.POLL || (pollOption1.isNotBlank() && pollOption2.isNotBlank()))
            Button(
                onClick = {
                    if (canPublish) {
                        onPostSubmitted(caption, selectedType, listOf(pollOption1, pollOption2))
                        onDismissRequest()
                    }
                },
                enabled = canPublish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (canPublish) Modifier.background(IPLOrangeGradient) else Modifier.background(
                                Color.White.copy(alpha = 0.05f)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "BROADCAST TO ARENA",
                        color = if (canPublish) Color.White else Color.Gray,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

