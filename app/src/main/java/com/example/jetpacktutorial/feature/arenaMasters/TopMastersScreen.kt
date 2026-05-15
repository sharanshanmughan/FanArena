package com.example.jetpacktutorial.feature.arenaMasters

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpacktutorial.core.data.model.ArenaMasterProfile
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.feature.home.LoadingSkeleton
import com.example.jetpacktutorial.feature.home.glassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopMastersScreen( onProfileClicked: (String) -> Unit) {
    val viewModel: TopMastersViewModel= hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Top Arena Masters", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // High-tier ambient gold background accent aura glow
            Box(modifier = Modifier.size(300.dp).align(Alignment.TopCenter).blur(130.dp).background(Color(0xFFFFD700).copy(alpha = 0.05f)))

            when (val currentState = state) {
                is TopMastersUiState.Loading -> LoadingSkeleton()
                is TopMastersUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(currentState.message, color = Color.Red) }
                is TopMastersUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(currentState.masters, key = { it.masterId }) { master ->
                            MasterEliteCard(master = master, onClick = { onProfileClicked(master.masterId) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MasterEliteCard(master: ArenaMasterProfile, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCard()
            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.25f), RoundedCornerShape(24.dp)) // Signature master gold trim
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank Crown Holder Placement Space
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(IPLOrangeGradient),
            contentAlignment = Alignment.Center
        ) {
            Text("#${master.overallRank}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Center Identity Stack block
        Column(modifier = Modifier.weight(1f)) {
            Text(text = master.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = master.specialTitle,
                color = AccentNeonGlow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Favorite Club Tag representation
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = "Camp: ${master.favoriteTeamToken}", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Metrics Tracking Layer Block
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${master.totalPoints} pts",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${master.winAccuracyPercentage}% Accuracy",
                color = Color(0xFF4CAF50), // Positive accuracy split tint green
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}