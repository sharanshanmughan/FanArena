package com.example.jetpacktutorial.feature.fanPoll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpacktutorial.core.ui.components.InteractivePollCardRow
import com.example.jetpacktutorial.core.ui.theme.DarkBackground
import com.example.jetpacktutorial.feature.home.LoadingSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FanPollsScreen(onBackClicked: () -> Unit) {
    val viewModel: FanPollsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Fan Polls Room",
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
                    .size(250.dp)
                    .align(Alignment.TopEnd)
                    .blur(110.dp)
                    .background(Color(0xFF4A148C).copy(alpha = 0.08f)),
            )

            when (val currentState = state) {
                is FanPollsUiState.Loading -> LoadingSkeleton()
                is FanPollsUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(currentState.message, color = Color.Red)
                    }
                }
                is FanPollsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        items(currentState.activePolls, key = { it.pollId }) { poll ->
                            InteractivePollCardRow(
                                poll = poll,
                                onVoteClicked = { index ->
                                    viewModel.submitPollVote(poll.pollId, index)
                                },
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
