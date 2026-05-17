

package com.example.jetpacktutorial.feature.livematch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.jetpacktutorial.core.data.model.Comment
import com.example.jetpacktutorial.core.data.model.InteractivePollCard
import com.example.jetpacktutorial.core.data.model.MatchHubDetails
import com.example.jetpacktutorial.core.ui.components.InteractivePollCardRow
import com.example.jetpacktutorial.core.data.model.UserMatchPrediction
import com.example.jetpacktutorial.core.ui.components.YourPredictionSummary
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.core.ui.theme.SurfaceGlass
import com.example.jetpacktutorial.feature.home.LoadingSkeleton
import com.example.jetpacktutorial.feature.home.glassmorphicCard
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchHubScreen(
    onBackClicked: () -> Unit,
    onNavigateToPredict: (String) -> Unit,
    padding: PaddingValues
) {
    val viewModel: MatchHubViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val userPrediction by viewModel.userPrediction.collectAsState()
    val matchPolls by viewModel.matchPolls.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Match Hub",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(bottom = padding.calculateBottomPadding() * 0.8f)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.TopCenter)
                    .blur(90.dp)
                    .background(Color(0xFF4A148C).copy(alpha = 0.2f))
            )

            when (val currentState = state) {
                is MatchHubUiState.Loading -> LoadingSkeleton()
                is MatchHubUiState.Error -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(currentState.message, color = Color.Red) }

                is MatchHubUiState.Success -> {
                    MatchHubContent(
                        details = currentState.data,
                        userPrediction = userPrediction,
                        matchPolls = matchPolls,
                        onNavigateToPredict = { onNavigateToPredict(viewModel.matchId) },
                        onPollVote = { pollId, index ->
                            viewModel.submitPollVote(pollId, index)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun MatchHubContent(
    details: MatchHubDetails,
    userPrediction: UserMatchPrediction?,
    matchPolls: List<InteractivePollCard>,
    onNavigateToPredict: () -> Unit,
    onPollVote: (String, Int) -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Polls", "Discussion")

    var liveCommentsList by remember(details.discussionComments) { mutableStateOf(details.discussionComments) }
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Automatically snaps views downward whenever a comment arrives
    LaunchedEffect(key1 = liveCommentsList.size) {
        if (liveCommentsList.isNotEmpty() && selectedTab == 2) {
            listState.animateScrollToItem(liveCommentsList.size + 1) // +2 offsets for headers layout items
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // SINGLE MASTER TIMELINE SCROLL LOOP
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ITEM 1: Massive Stat Header Modules. Will scroll out of view nicely.
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HubTeamBadge(details.team1Name)
                        Text(
                            "VS",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        HubTeamBadge(details.team2Name)
                    }

                    CountdownWidget(targetTimeMillis = details.matchStartTimeMillis)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (userPrediction != null) {
                        YourPredictionSummary(prediction = userPrediction)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Button(
                        onClick = onNavigateToPredict,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(IPLOrangeGradient),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = if (userPrediction != null) "VIEW PREDICTION" else "MAKE PREDICTIONS",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp,
                            )
                        }
                    }
                }
            }

            // ITEM 2: Content Segment Control Tab row bar section
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFFFF5722)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        )
                    }
                }
            }

            // ITEM 3: Dynamic multi-conditional context sub-element inject streams
            when (selectedTab) {
                0 -> { // OVERVIEW CARD TAB LAYOUT
                    val topPoll = matchPolls.firstOrNull()
                    val dynamicInsightText =
                        if (topPoll != null && topPoll.voteDistribution.isNotEmpty()) {
                            val highestVotePct = topPoll.voteDistribution.maxOrNull() ?: 0
                            val leadingOptionIndex = topPoll.voteDistribution.indexOf(highestVotePct)
                            val leadingOptionName = topPoll.optionsList.getOrNull(leadingOptionIndex) ?: "one side"

                            "$highestVotePct% of live arena fans are confidently predicting: '$leadingOptionName' in response to '${topPoll.question}'."
                        } else {
                            "84% of arena fans are backing ${details.team1Name} to break their boundary record tonight at this venue."
                        }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .glassmorphicCard()
                                .padding(16.dp)
                        ) {
                            Column {
                                Text("Arena Insight", fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = dynamicInsightText,
                                    color = Color.Gray,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                1 -> { // POLLS COLLECTION ROW ITEMS
                    items(matchPolls, key = { it.pollId }) { poll ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            InteractivePollCardRow(
                                poll = poll,
                                onVoteClicked = { index -> onPollVote(poll.pollId, index) },
                            )
                        }
                    }
                }

                2 -> { // DISCUSSION GROUPED MESSAGES ITEMS
                    items(liveCommentsList, key = { it.id }) { comment ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .glassmorphicCard()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    comment.username.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        comment.username,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    comment.supportTeamBadge?.let { badge ->
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Color.White.copy(alpha = 0.15f),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 2.dp),
                                        ) {
                                            Text(
                                                badge,
                                                fontSize = 9.sp,
                                                color = AccentNeonGlow,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(comment.timestamp, color = Color.Gray, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(comment.text, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // FOOTER TEXT-FIELD OVERLAY STRIP (Renders smoothly under tab 2 context focus)
        if (selectedTab == 2) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, BorderGlass, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp)),
                        placeholder = {
                            Text(
                                "Send banter to the arena...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceGlass,
                            unfocusedContainerColor = SurfaceGlass.copy(alpha = 0.6f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    val canSend = messageText.isNotBlank()
                    IconButton(
                        onClick = {
                            if (canSend) {
                                val userPostItem = Comment(
                                    id = java.util.UUID.randomUUID().toString(),
                                    username = "You (Arena Fan)",
                                    text = messageText.trim(),
                                    avatarUrl = "",
                                    timestamp = "Just Now",
                                    supportTeamBadge = details.team1Name.take(3).uppercase()
                                )
                                liveCommentsList = liveCommentsList + userPostItem
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (canSend) Color(0xFFFF5722) else Color.White.copy(alpha = 0.05f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Message",
                            tint = if (canSend) Color.White else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CountdownWidget(targetTimeMillis: Long) {
    var timeLeft by remember { mutableLongStateOf(targetTimeMillis - System.currentTimeMillis()) }

    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            delay(1000)
            timeLeft = targetTimeMillis - System.currentTimeMillis()
        }
    }

    val hours = (timeLeft / (1000 * 60 * 60)) % 24
    val minutes = (timeLeft / (1000 * 60)) % 60
    val seconds = (timeLeft / 1000) % 60
    val timeString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "MATCH STARTS IN",
            fontSize = 11.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = if (timeLeft > 0) timeString else "MATCH LIVE",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = if (timeLeft > 0) Color.White else AccentNeonGlow
        )
    }
}

@Composable
fun HubTeamBadge(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, BorderGlass, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
        }
    }
}

