package com.example.jetpacktutorial.feature.trendingPrediction

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.jetpacktutorial.core.data.model.PredictionInsightCard
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.core.ui.theme.IPLPurpleGradient
import com.example.jetpacktutorial.core.ui.theme.SurfaceGlass
import com.example.jetpacktutorial.feature.home.LoadingSkeleton
import com.example.jetpacktutorial.feature.home.glassmorphicCard
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingPredictionsScreen( onPredictionCardClicked: (String) -> Unit) {
    val viewModel: TrendingPredictionsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val currentFilter by viewModel.selectedFilter.collectAsState()

    val navigationFilterChips = listOf("All", "Match Multiplier", "Player Performance", "Boundaries")

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Trending Forecasts", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Ambient architectural glass blur shadow mapping
            Box(modifier = Modifier.size(280.dp).align(Alignment.BottomStart).blur(120.dp).background(Color(0xFF00E5FF).copy(alpha = 0.06f)))

            Column(modifier = Modifier.fillMaxSize()) {

                // Filter Categories Subheader Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(navigationFilterChips) { category ->
                        val isSelected = currentFilter == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White.copy(alpha = 0.15f) else SurfaceGlass)
                                .border(1.dp, if (isSelected) AccentNeonGlow else BorderGlass, RoundedCornerShape(12.dp))
                                .clickable { viewModel.changeFilter(category) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) AccentNeonGlow else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Primary Functional State Core Router
                when (val currentState = state) {
                    is TrendingPredictionsUiState.Loading -> LoadingSkeleton()
                    is TrendingPredictionsUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(currentState.message, color = Color.Red) }
                    is TrendingPredictionsUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(currentState.insights, key = { it.predictionId }) { insight ->
                                TrendingInsightCard(insight = insight, onClick = { onPredictionCardClicked(insight.predictionId) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrendingInsightCard(insight: PredictionInsightCard, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCard()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // Top Meta Category Metric Line Configuration
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(insight.category.uppercase(Locale.ROOT), fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Gray, letterSpacing = 0.5.sp)

            insight.trendingTag?.let { tag ->
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFF5722).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(tag, color = Color(0xFFFF5722), fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Central Query Statement Form Group
        Text(text = insight.question, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Interactive Comparative Option Channel 1 Block
        PredictionDataBar(name = insight.option1Name, percentage = insight.option1Percentage, fillerBrush = IPLOrangeGradient)

        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic Interactive Comparative Option Channel 2 Block
        PredictionDataBar(name = insight.option2Name, percentage = insight.option2Percentage, fillerBrush = IPLPurpleGradient)

        Spacer(modifier = Modifier.height(14.dp))

        // Global User Interactivity Aggregation Footer Note
        Text(text = insight.totalVotersCount, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PredictionDataBar(name: String, percentage: Int, fillerBrush: androidx.compose.ui.graphics.Brush) {
    val fractionRatio = percentage.toFloat() / 100f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("$percentage%", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Single row progress rail tracker architecture
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fractionRatio)
                    .clip(CircleShape)
                    .background(fillerBrush)
            )
        }
    }
}