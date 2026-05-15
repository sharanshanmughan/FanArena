package com.example.jetpacktutorial.feature.todayMatches

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.jetpacktutorial.core.data.model.DetailedMatchCard
import com.example.jetpacktutorial.core.data.model.MatchStatus
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.core.ui.theme.IPLPurpleGradient
import com.example.jetpacktutorial.feature.home.LoadingSkeleton
import com.example.jetpacktutorial.feature.home.glassmorphicCard

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TodayMatchesScreen( onMatchSelected: (String) -> Unit) {
    val viewModel: TodayMatchesViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Today's Arena Grid", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Ambient design asset background glow
            Box(modifier = Modifier.size(240.dp).align(Alignment.TopCenter).blur(100.dp).background(Color(0xFFFF5722).copy(alpha = 0.08f)))

            when (val currentState = state) {
                is TodayMatchesUiState.Loading -> LoadingSkeleton()
                is TodayMatchesUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(currentState.message, color = Color.Red) }
                is TodayMatchesUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(currentState.matches, key = { it.matchId }) { match ->
                            DetailedMatchRowCard(match = match, onClick = { onMatchSelected(match.matchId) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailedMatchRowCard(match: DetailedMatchCard, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCard()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // Status Badge Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = when (match.status) {
                            MatchStatus.LIVE -> Color(0xFFFF1744)
                            MatchStatus.UPCOMING -> Color.White.copy(alpha = 0.1f)
                            MatchStatus.COMPLETED -> Color.White.copy(alpha = 0.05f)
                        },
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = match.status.name,
                    color = if (match.status == MatchStatus.LIVE) Color.White else Color.Gray,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }
            Text(match.matchTimeOrScore, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Teams Display Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MatchTeamIdentity(code = match.team1Code, name = match.team1Name)
            Text("VS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.2f))
            MatchTeamIdentity(code = match.team2Code, name = match.team2Name)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Fan Prediction Metric Bar Indicator
        val team1Percentage = (match.fanSupportPredictionRatio * 100).toInt()
        val team2Percentage = 100 - team1Percentage

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${match.team1Code} ($team1Percentage%)", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Fan Consensus", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("($team2Percentage%) ${match.team2Code}", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))

            // Asymmetric Dual Colored Bar layout mapping consensus parameters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
            ) {
                Box(modifier = Modifier.fillMaxHeight().weight(match.fanSupportPredictionRatio).background(IPLOrangeGradient))
                Box(modifier = Modifier.fillMaxHeight().weight(1f - match.fanSupportPredictionRatio).background(IPLPurpleGradient))
            }
        }
    }
}

@Composable
fun MatchTeamIdentity(code: String, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
                .border(1.dp, BorderGlass, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(code, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}