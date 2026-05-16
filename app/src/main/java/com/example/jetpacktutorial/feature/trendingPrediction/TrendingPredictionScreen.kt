package com.example.jetpacktutorial.feature.trendingPrediction

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.jetpacktutorial.core.data.repository.TrendingPredictionsRepository
import com.example.jetpacktutorial.core.ui.components.TrendingInsightCard
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.SurfaceGlass
import com.example.jetpacktutorial.feature.home.LoadingSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingPredictionsScreen(
    onBackClicked: () -> Unit,
    onOpenMatchPrediction: (String) -> Unit,
) {
    val viewModel: TrendingPredictionsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val currentFilter by viewModel.selectedFilter.collectAsState()

    val filterChips = listOf(
        TrendingPredictionsRepository.FILTER_ALL,
        "Match Multiplier",
        "Player Performance",
        "Boundaries",
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Trending Forecasts",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.BottomStart)
                    .blur(120.dp)
                    .background(Color(0xFF00E5FF).copy(alpha = 0.06f)),
            )

            Column(modifier = Modifier.fillMaxSize()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(filterChips) { category ->
                        val isSelected = currentFilter == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White.copy(alpha = 0.15f) else SurfaceGlass)
                                .border(
                                    1.dp,
                                    if (isSelected) AccentNeonGlow else BorderGlass,
                                    RoundedCornerShape(12.dp),
                                )
                                .clickable { viewModel.changeFilter(category) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) AccentNeonGlow else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (val currentState = state) {
                    is TrendingPredictionsUiState.Loading -> LoadingSkeleton()
                    is TrendingPredictionsUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(currentState.message, color = Color.Red)
                        }
                    }
                    is TrendingPredictionsUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
                        ) {
                            items(currentState.insights, key = { it.predictionId }) { insight ->
                                TrendingInsightCard(
                                    insight = insight,
                                    onOptionVote = { index ->
                                        viewModel.submitTrendingVote(insight.predictionId, index)
                                    },
                                    onOpenMatchPrediction = onOpenMatchPrediction,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
