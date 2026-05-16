package com.example.jetpacktutorial.feature.dashboard

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.jetpacktutorial.feature.feed.FanFeedScreen
import com.example.jetpacktutorial.feature.home.HomeScreen
import com.example.jetpacktutorial.feature.leaderboard.LeaderboardScreen
import com.example.jetpacktutorial.feature.livematch.MatchHubScreen
import com.example.jetpacktutorial.feature.teams.TeamScreen
import com.example.jetpacktutorial.navigation.BottomNavItem
import com.example.jetpacktutorial.navigation.Routes


@Composable
fun DashboardScreen(
    tab: String?,
    onNavigateToPredict: (String) -> Unit,
    onNavigateToTodayMatch: () -> Unit,
    onNavigateToTrendingPrediction: () -> Unit,
    onNavigateToFanPoll: () -> Unit,
    onNavigateTopMasters:()-> Unit
) {


    val activity = LocalContext.current as? Activity

    var currentTab by rememberSaveable { mutableStateOf(tab ?: BottomNavItem.Home.route) }

    val switchTab: (String) -> Unit = { selectedTab ->
        currentTab = selectedTab
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomBar(
                currentTab,
                PaddingValues(20.dp),
            ) { selectedTab -> currentTab = selectedTab }
        }
    ) { padding ->
        padding
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentTab) {
                BottomNavItem.Home.route ->
                    Animation { offset, alpha ->
                        HomeScreen(
                            padding,
                            switchTab,
                            onPredictMatch = { matchId ->
                                onNavigateToPredict(matchId)
                            },
                            seeAllTodayMatch = {
                                onNavigateToTodayMatch()
                            },
                            seeAllTrendingPredictions = {
                                onNavigateToTrendingPrediction()
                            },
                            seeAllFanPoll = {
                                onNavigateToFanPoll()
                            },
                            seeAllTopMasters = {
                                onNavigateTopMasters()
                            },
                        )
                    }

                BottomNavItem.Match.route ->
                    Animation { offset, alpha ->
                        MatchHubScreen(
                            onBackClicked = {},
                            onNavigateToPredict = { matchId ->
                                onNavigateToPredict(matchId)
                            },
                        )
                    }

                BottomNavItem.Feed.route ->
                    Animation { offset, alpha ->
                        FanFeedScreen()
                    }

                BottomNavItem.Leaderboard.route ->
                    Animation { offset, alpha ->
                        LeaderboardScreen()
                    }

                BottomNavItem.Team.route ->
                    Animation { offset, alpha ->
                        TeamScreen(onTeamSelected = {})
                    }


            }


        }
    }

    BackHandler {
        when (currentTab) {
            BottomNavItem.Match.route,
            BottomNavItem.Feed.route,
            BottomNavItem.Leaderboard.route,
            BottomNavItem.Team.route -> currentTab = BottomNavItem.Home.route

            BottomNavItem.Home.route -> activity?.finishAndRemoveTask()
        }
    }

}

@Composable
fun Animation(
    content: @Composable (offset: Dp, alpha: Float) -> Unit
) {
    var start by remember { mutableStateOf(false) }
    val offset by animateDpAsState(
        targetValue = if (start) 0.dp else 140.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val alpha by animateFloatAsState(
        targetValue = if (start) 1f else 0f,
        animationSpec = tween(400)
    )
    LaunchedEffect(Unit) {
        start = true
    }
    content(offset, alpha)
}

@Composable
fun BottomBar(currentTab: String, padding: PaddingValues, onTabSelected: (String) -> Unit) {
    val bottomBarItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Match,
        BottomNavItem.Feed,
        BottomNavItem.Leaderboard,
        BottomNavItem.Team,

        )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars) // Safe area for gestures
            .padding(horizontal = 16.dp, vertical = 12.dp),   // Floating layout effect
        color = Color(0xFF121420).copy(alpha = 0.85f),       // Deep Navy Glass
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF00D1FF),
                            Color(0xFFAD00FF),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier

                .fillMaxWidth()
                .background(Color(0xFF121420).copy(alpha = 0.85f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomBarItems.forEach { item ->

                val interactionSource = remember { MutableInteractionSource() }
                val isSelected = currentTab == item.route


                // Optional: Animate the alpha/opacity for a softer look on unselected items
                val animatedAlpha by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0.7f,
                    label = "IconAlpha"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onTabSelected(item.route)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = if (isSelected) 1f else 0.9f
                                scaleY = if (isSelected) 1f else 0.9f
                                alpha = animatedAlpha
                            }
                            .graphicsLayer(alpha = animatedAlpha),
                        tint = if (isSelected) Color(0xFF00D1FF) else Color.White.copy(alpha = 0.4f),
                    )

                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        color = if (isSelected) Color(0xFF00D1FF) else Color.White.copy(alpha = 0.4f),
                    )
                }
            }
        }
    }


}
