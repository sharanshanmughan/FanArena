package com.example.jetpacktutorial.feature.livematch

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.jetpacktutorial.core.data.model.FanPoll
import com.example.jetpacktutorial.core.data.model.MatchHubDetails
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.feature.home.LoadingSkeleton
import com.example.jetpacktutorial.feature.home.glassmorphicCard
import kotlinx.coroutines.delay
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchHubScreen( onBackClicked: () -> Unit, onNavigateToPredict: () -> Unit) {

    val viewModel: MatchHubViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Match Hub", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Ambient design background glow
            Box(modifier = Modifier.size(250.dp)
                .align(Alignment.TopCenter)
                .blur(90.dp).background(Color(0xFF4A148C).copy(alpha = 0.2f)))

            when (val currentState = state) {
                is MatchHubUiState.Loading -> LoadingSkeleton()
                is MatchHubUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(currentState.message, color = Color.Red) }
                is MatchHubUiState.Success -> {
                    MatchHubContent(
                        details = currentState.data,
                        onNavigateToPredict = onNavigateToPredict
                    )
                }
            }
        }
    }
}

@Composable
fun MatchHubContent(details: MatchHubDetails, onNavigateToPredict: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Polls", "Discussion")

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large Team vs Team Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HubTeamBadge(details.team1Name)
                Text("VS", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.4f))
                HubTeamBadge(details.team2Name)
            }

            // Real-time Countdown Timer
            CountdownWidget(targetTimeMillis = details.matchStartTimeMillis)

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Engagement Prediction Call_to Action Button
            Button(
                onClick = onNavigateToPredict,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(IPLOrangeGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MAKE PREDICTIONS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Custom Tab Selector Layout
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

        // Swapping layouts contextually depending on selected tab configuration
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> MatchOverviewTab()
                1 -> MatchPollsTab(details.quickPolls)
                2 -> MatchDiscussionTab(details.discussionComments)
            }
        }
    }
}

@Composable
fun CountdownWidget(targetTimeMillis: Long) {
    var timeLeft by remember { mutableLongStateOf(targetTimeMillis - System.currentTimeMillis()) }

    // Coroutine lifecycle ticker
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
        Text("MATCH STARTS IN", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text(
            text = if (timeLeft > 0) timeString else "MATCH LIVE",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = if (timeLeft > 0) Color.White else AccentNeonGlow
        )
    }
}

@Composable
fun MatchOverviewTab() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().glassmorphicCard().padding(16.dp)) {
            Column {
                Text("Arena Insight", fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("84% of arena fans are backing RCB to break their boundary record tonight at this venue.", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun MatchPollsTab(polls: List<FanPoll>) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        polls.forEach { poll ->
            Column(modifier = Modifier.fillMaxWidth().glassmorphicCard().padding(16.dp)) {
                Text(poll.question, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                poll.options.forEach { option ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, BorderGlass, RoundedCornerShape(12.dp))
                            .clickable {}
                            .padding(12.dp)
                    ) {
                        Text(option, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

@Composable
fun MatchDiscussionTab(comments: List<Comment>) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        comments.forEach { comment ->
            Row(
                modifier = Modifier.fillMaxWidth().glassmorphicCard().padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(comment.username, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        comment.supportTeamBadge?.let { badge ->
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                Text(badge, fontSize = 9.sp, color = AccentNeonGlow, fontWeight = FontWeight.Bold)
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

@Composable
fun HubTeamBadge(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f)).border(1.dp, BorderGlass, CircleShape), contentAlignment = Alignment.Center) {
            Text(name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
        }
    }
}