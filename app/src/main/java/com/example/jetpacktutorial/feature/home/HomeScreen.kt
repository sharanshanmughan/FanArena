package com.example.jetpacktutorial.feature.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpacktutorial.core.data.model.FanPoll
import com.example.jetpacktutorial.core.data.model.LeaderboardUser
import com.example.jetpacktutorial.core.data.model.Match
import com.example.jetpacktutorial.core.data.model.TrendingPrediction
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.core.ui.theme.IPLPurpleGradient
import com.example.jetpacktutorial.core.ui.theme.SurfaceGlass
import com.example.jetpacktutorial.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(padding: PaddingValues, switchTab: (String) -> Unit,
               seeAllTodayMatch: () -> Unit,
               seeAllTrendingPredictions:()-> Unit,
               seeAllFanPoll:()-> Unit,
               seeAllTopMasters:()-> Unit
               ) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(

        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Fan Arena", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Background subtle gradient glow blur
            Box(modifier = Modifier.size(300.dp)
                .align(Alignment.TopEnd).blur(100.dp).background(Color(0xFFFF5722).copy(alpha = 0.15f)))

            when (val currentState = state) {
                is HomeUiState.Loading -> {
                    LoadingSkeleton()
                }
                is HomeUiState.Success -> {
                    HomeScreenContent(
                        matches = currentState.todayMatches,
                        predictions = currentState.trendingPredictions,
                        polls = currentState.fanPolls,
                        users = currentState.topUsers,
                        onPredictionClick = {
                            switchTab(BottomNavItem.Match.route)
                        },
                        seeAllTodayMatch = {seeAllTodayMatch()},
                        seeAllTrendingPredictions={seeAllTrendingPredictions()},
                        seeAllFanPoll={
                            seeAllFanPoll()
                        },
                        seeAllTopMasters={
                            seeAllTopMasters()
                        }

                    )
                }
                is HomeUiState.Error -> {
                    ErrorMessage(message = currentState.message)
                }
            }

        }
    }
}

@Composable
fun ErrorMessage(message: String){
    Text(message)
}

@Composable
fun HomeScreenContent(
    matches: List<Match>,
    predictions: List<TrendingPrediction>,
    polls: List<FanPoll>,
    users: List<LeaderboardUser>,
    onPredictionClick: () -> Unit,
    seeAllTodayMatch: () -> Unit,
    seeAllTrendingPredictions:()-> Unit,
    seeAllFanPoll: () -> Unit,
    seeAllTopMasters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, bottom = 60.dp, top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),

    ) {
        TodayMatchesSection(matches, onClick = {onPredictionClick()}, seeAllTodayMatch = {seeAllTodayMatch()})
        TrendingPredictionsSection(predictions,seeAllTrendingPredictions={
            seeAllTrendingPredictions()
        })
        FanPollsSection(polls,seeAllFanPoll={seeAllFanPoll()})
        LeaderboardWidget(users,seeAllTopMasters={seeAllTopMasters()})
        Spacer(modifier = Modifier.height(32.dp))
    }
}

fun Modifier.glassmorphicCard(): Modifier = this
    .clip(RoundedCornerShape(20.dp))
    .background(SurfaceGlass)
    .border(1.dp, BorderGlass, RoundedCornerShape(20.dp))

@Composable
fun TodayMatchesSection(matches: List<Match>,onClick:()-> Unit,seeAllTodayMatch: () -> Unit) {
    SectionHeader("Today's Matches",onClick={seeAllTodayMatch()})
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(matches) { match ->
            Column(
                modifier = Modifier
                    .width(260.dp)
                    .glassmorphicCard()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamBadge(match.team1)
                    Text("VS", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                    TeamBadge(match.team2)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(match.time, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onClick() },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(IPLOrangeGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Predict Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TrendingPredictionsSection(predictions: List<TrendingPrediction>,seeAllTrendingPredictions:()-> Unit) {
    SectionHeader("Trending Predictions",onClick={seeAllTrendingPredictions()})
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(predictions) { item ->
            Row(
                modifier = Modifier
                    .width(200.dp)
                    .glassmorphicCard()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(item.category, color = Color.Gray, fontSize = 11.sp)
                    Text(item.prediction, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun FanPollsSection(polls: List<FanPoll>,seeAllFanPoll: () -> Unit) {
    SectionHeader("Fan Polls",onClick={seeAllFanPoll()})
    polls.forEach { poll ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphicCard()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(poll.question, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            poll.options.forEach { option ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .clickable { /* Handle Vote action */ }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(option, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
fun LeaderboardWidget(users: List<LeaderboardUser>,seeAllTopMasters: () -> Unit) {
    SectionHeader("Top Arena Masters",onClick={seeAllTopMasters()})
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCard()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // Arrange items to show 2nd, 1st, 3rd inside a podium display style
        val podiumOrder = listOf(users.getOrNull(1), users.getOrNull(0), users.getOrNull(2))
        podiumOrder.filterNotNull().forEach { user ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                val heightModifier = if (user.rank == 1) Modifier.size(56.dp) else Modifier.size(44.dp)
                Box(
                    modifier = heightModifier
                        .clip(CircleShape)
                        .background(if (user.rank == 1) IPLOrangeGradient else IPLPurpleGradient)
                        .padding(2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color.DarkGray))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(user.username, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                Text("${user.points} pts", color = AccentNeonGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TeamBadge(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f)), contentAlignment = Alignment.Center) {
            Text(name.take(1), color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun SectionHeader(title: String,onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Text("See all", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.clickable {
            onClick()
        })
    }
}

@Composable
fun LoadingSkeleton() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .glassmorphicCard()
                    .background(Color.White.copy(alpha = 0.03f))
            )
        }
    }
}