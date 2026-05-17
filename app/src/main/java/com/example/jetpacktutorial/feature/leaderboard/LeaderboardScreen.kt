package com.example.jetpacktutorial.feature.leaderboard

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktutorial.core.data.model.ArenaRankedUser
import com.example.jetpacktutorial.core.data.model.LeaderboardTab
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.core.ui.theme.SurfaceGlass
import com.example.jetpacktutorial.feature.home.LoadingSkeleton
import com.example.jetpacktutorial.feature.home.glassmorphicCard
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(padding: PaddingValues) {
    val viewModel: LeaderboardViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val activeTab by viewModel.currentTab.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .background(DarkBackground)
            .padding(bottom = padding.calculateBottomPadding()*.6f),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Arena Masterboard", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Ambient design asset glow
            Box(modifier = Modifier.size(260.dp).align(Alignment.TopStart).blur(120.dp).background(Color(0xFF00E5FF).copy(alpha = 0.08f)))

            Column(modifier = Modifier.fillMaxSize()) {
                // Segmented Tab Swapper Layout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(SurfaceGlass, RoundedCornerShape(14.dp))
                        .border(1.dp, BorderGlass, RoundedCornerShape(14.dp))
                        .padding(4.dp)
                ) {
                    LeaderboardTab.entries.forEach { tab ->
                        val isSelected = activeTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .then(
                                    if (isSelected) Modifier.background(brush = IPLOrangeGradient)
                                    else Modifier.clickable { viewModel.switchTab(tab) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.name.lowercase(Locale.ROOT)
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                                color = if (isSelected) Color.White else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // State Machine Switch Route Parsing
                when (val currentState = state) {
                    is LeaderboardUiState.Loading -> LoadingSkeleton()
                    is LeaderboardUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(currentState.message, color = Color.Red) }
                    is LeaderboardUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(currentState.payload.rankingsList, key = { it.username }) { user ->
                                RankedUserRow(user)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankedUserRow(user: ArenaRankedUser) {
    // Dynamic styling variables for highlighting top 3 users
    val isTopRank = user.rank <= 3
    val parsedColor = remember(user.badgeGlowColor) { Color(user.badgeGlowColor.toColorInt()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCard()
            .then(
                if (user.rank == 1) Modifier.border(1.dp, Color(0xFFFF9800).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank Indicator Allocation Frame Space
        Box(
            modifier = Modifier.width(36.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = when(user.rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> "${user.rank}th"
                },
                fontSize = if (isTopRank) 20.sp else 14.sp,
                fontWeight = FontWeight.Black,
                color = if (isTopRank) Color.Unspecified else Color.Gray
            )
        }

        // Avatar Core Graphic Group Asset representation
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .then(if (isTopRank) Modifier.background(IPLOrangeGradient)
                else Modifier.background(BorderGlass) )
                .padding(2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFF1E222B)), contentAlignment = Alignment.Center) {
                Text(user.username.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Username and localized badge text indicators stack setup
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.username,
                color = if (user.rank == 1) Color(0xFFFF9800) else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .background(parsedColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = user.badgeName.uppercase(Locale.ROOT),
                    color = parsedColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Total Accumulated Point Metrics Container
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${user.points}",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
            Text(
                text = "pts",
                color = AccentNeonGlow,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}