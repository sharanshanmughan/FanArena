package com.example.jetpacktutorial.feature.fanPoll

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpacktutorial.core.data.model.InteractivePollCard
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.feature.home.LoadingSkeleton
import com.example.jetpacktutorial.feature.home.glassmorphicCard
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FanPollsScreen() {
    val viewModel: FanPollsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Fan Polls Room", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Deep background aesthetic visual design layer
            Box(modifier = Modifier.size(250.dp).align(Alignment.TopEnd).blur(110.dp).background(Color(0xFF4A148C).copy(alpha = 0.08f)))

            when (val currentState = state) {
                is FanPollsUiState.Loading -> LoadingSkeleton()
                is FanPollsUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(currentState.message, color = Color.Red) }
                is FanPollsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(currentState.activePolls, key = { it.pollId }) { poll ->
                            InteractivePollCardRow(
                                poll = poll,
                                onVoteClicked = { index -> viewModel.submitPollVote(poll.pollId, index) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractivePollCardRow(poll: InteractivePollCard, onVoteClicked: (Int) -> Unit) {
    val holdsVotedState = poll.userSelectedOptionIndex != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphicCard()
            .padding(16.dp)
            .animateContentSize() // Smoothly animates size shifts when moving to results view
    ) {
        // Category Subtitle Tag Meta Row
        Text(
            text = poll.category.uppercase(Locale.ROOT),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = AccentNeonGlow,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Core Poll Question Text Layer
        Text(
            text = poll.question,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contextual Selection Block Logic
        if (!holdsVotedState) {
            // Interactive voting state options
            poll.optionsList.forEachIndexed { index, option ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(1.dp, BorderGlass, RoundedCornerShape(12.dp))
                        .clickable { onVoteClicked(index) }
                        .padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = option, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            // Post-Vote Results Distribution Layout View
            poll.optionsList.forEachIndexed { index, option ->
                val resultPercentage = poll.voteDistribution.getOrNull(index) ?: 0
                val fractionFill = resultPercentage.toFloat() / 100f
                val isUserChoice = poll.userSelectedOptionIndex == index

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = option, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            if (isUserChoice) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("• Your Vote", color = Color(0xFFFF5722), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(text = "$resultPercentage%", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Single Data Fill Component Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.04f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fractionFill)
                                .clip(CircleShape)
                                .background(if (isUserChoice) IPLOrangeGradient else Brush.horizontalGradient(listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.1f))))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Total Cumulative Polling Metadata Footnote Text Display
        Text(
            text = "${poll.totalVotesFormatted} accumulated",
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}