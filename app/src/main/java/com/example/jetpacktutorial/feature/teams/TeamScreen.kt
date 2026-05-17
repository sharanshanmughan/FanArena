package com.example.jetpacktutorial.feature.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktutorial.core.data.model.IplTeamCard
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.SurfaceGlass
import com.example.jetpacktutorial.feature.home.LoadingSkeleton
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(onTeamSelected: (String) -> Unit, padding: PaddingValues) {
   val viewModel: TeamsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .background(DarkBackground)
            .padding(bottom = padding.calculateBottomPadding()*.6f),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("IPL Arena Franchises", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Contextual ambient glowing overlay element
            Box(modifier = Modifier.size(240.dp).align(Alignment.CenterEnd).blur(130.dp).background(Color(0xFFFF9800).copy(alpha = 0.05f)))

            when (val currentState = state) {
                is TeamsUiState.Loading -> LoadingSkeleton()
                is TeamsUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(currentState.message, color = Color.Red) }
                is TeamsUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(currentState.teamsList, key = { it.teamId }) { team ->
                            TeamFranchiseCard(team = team, onClick = { onTeamSelected(team.teamId) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamFranchiseCard(team: IplTeamCard, onClick: () -> Unit) {
    // Dynamic color parsing fallback structures mapping branding colors directly from API strings
    val primaryTeamColor = remember(team.primaryColorHex) { Color(team.primaryColorHex.toColorInt()) }
    val secondaryTeamColor = remember(team.secondaryColorHex) { Color(team.secondaryColorHex.toColorInt()) }

    val teamGradient = Brush.verticalGradient(
        colors = listOf(primaryTeamColor.copy(alpha = 0.15f), secondaryTeamColor.copy(alpha = 0.03f))
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceGlass)
            .background(teamGradient)
            .border(1.dp, BorderGlass, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Vector Frame wrapper layout boundary space simulation
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(primaryTeamColor.copy(alpha = 0.2f))
                .border(1.5.dp, primaryTeamColor.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = team.shortCode,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Text Branding Meta Layers Stack setup
        Text(
            text = team.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = team.fanCountFormatted,
            color = AccentNeonGlow,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}