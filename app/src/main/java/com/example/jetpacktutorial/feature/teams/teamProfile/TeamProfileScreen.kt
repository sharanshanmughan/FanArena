package com.example.jetpacktutorial.feature.teams.teamProfile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpacktutorial.core.data.model.ProfilePlayer
import com.example.jetpacktutorial.core.data.model.TeamPastResult
import com.example.jetpacktutorial.core.data.model.TeamProfileDetails
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.SurfaceGlass
import com.example.jetpacktutorial.feature.home.LoadingSkeleton
import com.example.jetpacktutorial.feature.teams.TeamProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamProfileScreen(
    onBackClicked: () -> Unit,

) {
   val viewModel: TeamProfileViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Roster", "Camp", "History")

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Franchise Profile", fontSize = 20.sp, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val currentState = state) {
                is TeamProfileUiState.Loading -> LoadingSkeleton()
                is TeamProfileUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(currentState.message, color = Color.Red) }
                is TeamProfileUiState.Success -> {
                    val details = currentState.data
                    val primaryColor = Color(details.teamInfo.primaryColorHex.toColorInt())
                    val secondaryColor = Color(details.teamInfo.secondaryColorHex.toColorInt())

                    // Ambient custom dynamic glow based on selected team color hex variables
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .align(Alignment.TopCenter)
                            .blur(120.dp)
                            .background(primaryColor.copy(alpha = 0.12f))
                    )

                    Column(modifier = Modifier.fillMaxSize()) {

                        // 1. Hero Team Identity Banner Layout Component
                        TeamHeroHeader(details = details, primaryColor = primaryColor, secondaryColor = secondaryColor)

                        // 2. Navigation Tab Control Strip
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = AccentNeonGlow,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = primaryColor
                                )
                            },
                            divider = {}
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if(selectedTab == index) Color.White else Color.Gray) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3. Conditional Content Router View Routing
                        Box(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                            when (selectedTab) {
                                0 -> SquadRosterView(squad = details.squadList, primaryColor = primaryColor)
                                1 -> FanCampView(details = details, onToggleCamp = { viewModel.toggleFanCamp() }, primaryColor = primaryColor)
                                2 -> MatchHistoryView(history = details.pastResults)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamHeroHeader(details: TeamProfileDetails, primaryColor: Color, secondaryColor: Color) {
    val gradient = Brush.verticalGradient(
        colors = listOf(primaryColor.copy(alpha = 0.25f), secondaryColor.copy(alpha = 0.05f))
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceGlass)
            .background(gradient)
            .border(1.dp, BorderGlass, RoundedCornerShape(24.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.3f))
                .border(2.dp, primaryColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(details.teamInfo.shortCode, color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(details.teamInfo.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(4.dp))
        Text(details.teamInfo.fanCountFormatted, color = AccentNeonGlow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SquadRosterView(squad: List<ProfilePlayer>, primaryColor: Color) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(squad, key = { it.playerId }) { player ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceGlass)
                    .border(1.dp, BorderGlass, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(player.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        if (player.isCaptain) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.background(primaryColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("CAPT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    Text(player.role, color = Color.Gray, fontSize = 12.sp)
                }
                Box(modifier = Modifier.size(8.dp).background(primaryColor.copy(alpha = 0.4f), CircleShape))
            }
        }
    }
}

@Composable
fun FanCampView(details: TeamProfileDetails, onToggleCamp: () -> Unit, primaryColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(SurfaceGlass).padding(16.dp)) {
            Column {
                Text("Camp Arena Directive", fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Joining a fan camp locks in your primary arena loyalty parameters, unlocking localized chat themes and custom multiplier streaks on match prediction submissions.", color = Color.Gray, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onToggleCamp,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (details.isUserInFanCamp) Color.White.copy(alpha = 0.08f) else Color.Transparent),
            border = if (details.isUserInFanCamp) BorderStroke(1.dp, BorderGlass) else null,
            contentPadding = PaddingValues()
        ) {
            val brushModifier = if (details.isUserInFanCamp) Modifier.background(Color.Transparent) else Modifier.background(Brush.linearGradient(listOf(primaryColor, primaryColor.copy(alpha = 0.6f))))
            Box(
                modifier = Modifier.fillMaxSize().then(brushModifier),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (details.isUserInFanCamp) "LEAVE FRANCHISE CAMP" else "JOIN OFFICIAL FAN CAMP",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun MatchHistoryView(history: List<TeamPastResult>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(history) { result ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceGlass)
                    .border(1.dp, BorderGlass, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(36.dp).background(Color.White.copy(alpha = 0.04f), CircleShape), contentAlignment = Alignment.Center) {
                        Text(result.opponentShortCode, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(result.resultMessage, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Text(
                    text = if (result.isWinner) "W" else "L",
                    color = if (result.isWinner) Color(0xFF00E676) else Color(0xFFFF1744),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}