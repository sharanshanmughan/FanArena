package com.example.jetpacktutorial.feature.prediction

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.core.ui.theme.SurfaceGlass
import com.example.jetpacktutorial.feature.home.LoadingSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen(

    onBackClicked: () -> Unit,
    onSubmitComplete: () -> Unit
) {
    val viewModel: PredictionViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    // Selection UI Local Memory States
    var selectedWinner by remember { mutableStateOf<String?>(null) }
    var selectedScorerId by remember { mutableStateOf<String?>(null) }
    var selectedBowlerId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.submissionResult.collect { success ->
            if (success) onSubmitComplete()
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Prediction Screen", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            // Fixed bottom submit bracket container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground)
                    .padding(16.dp)
            ) {
                val isFormValid = selectedWinner != null && selectedScorerId != null && selectedBowlerId != null

                Button(
                    onClick = { viewModel.submitPredictions(selectedWinner, selectedScorerId, selectedBowlerId) },
                    enabled = isFormValid,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (isFormValid) {
                                    Modifier.background(brush = IPLOrangeGradient)
                                } else {
                                    Modifier.background(color = Color.White.copy(alpha = 0.05f))
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "SUBMIT",
                            color = if (isFormValid) Color.White else Color.Gray,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Ambient design asset glow
            Box(modifier = Modifier.size(220.dp).align(Alignment.BottomStart).blur(100.dp).background(Color(0xFFFF5722).copy(alpha = 0.12f)))

            when (val currentState = state) {
                is PredictionUiState.Loading -> LoadingSkeleton()
                is PredictionUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(currentState.message, color = Color.Red) }
                is PredictionUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Category 1: Match Winner
                        PredictionGroupHeader("Match Winner")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            listOf(currentState.options.team1Name, currentState.options.team2Name).forEach { team ->
                                Box(modifier = Modifier.weight(1f)) {
                                    SelectablePredictionCard(
                                        title = team,
                                        subtitle = "Team",
                                        isSelected = selectedWinner == team,
                                        onClick = { selectedWinner = team }
                                    )
                                }
                            }
                        }

                        // Category 2: Top Scorer
                        PredictionGroupHeader("Top Scorer")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            currentState.options.topScorerOptions.forEach { player ->
                                Box(modifier = Modifier.weight(1f)) {
                                    SelectablePredictionCard(
                                        title = player.name,
                                        subtitle = player.teamToken,
                                        isSelected = selectedScorerId == player.id,
                                        onClick = { selectedScorerId = player.id }
                                    )
                                }
                            }
                        }

                        // Category 3: Top Bowler
                        PredictionGroupHeader("Top Bowler")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            currentState.options.topBowlerOptions.forEach { player ->
                                Box(modifier = Modifier.weight(1f)) {
                                    SelectablePredictionCard(
                                        title = player.name,
                                        subtitle = player.teamToken,
                                        isSelected = selectedBowlerId == player.id,
                                        onClick = { selectedBowlerId = player.id }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SelectablePredictionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Smooth transition animations for selection state feedback loops
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFF5722) else BorderGlass,
        label = "BorderColor"
    )
    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 1.dp,
        label = "BorderWidth"
    )
    val cardBackgroundGlow = if (isSelected) Color(0xFFFF5722).copy(alpha = 0.08f) else SurfaceGlass

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBackgroundGlow)
            .border(animatedBorderWidth, animatedBorderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Generic circle placeholder layout simulating team graphic asset space
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Text(title.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
        Text(subtitle, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun PredictionGroupHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}