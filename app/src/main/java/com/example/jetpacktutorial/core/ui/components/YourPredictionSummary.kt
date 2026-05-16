package com.example.jetpacktutorial.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktutorial.core.data.model.UserMatchPrediction
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.SurfaceGlass
import com.example.jetpacktutorial.feature.home.glassmorphicCard

@Composable
fun PredictedBadge(modifier: Modifier = Modifier) {
    Text(
        text = "PREDICTED",
        color = AccentNeonGlow,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(AccentNeonGlow.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
fun YourPredictionSummary(
    prediction: UserMatchPrediction,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .glassmorphicCard()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Your prediction",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = if (compact) 14.sp else 16.sp,
            )
            PredictedBadge()
        }

        if (compact) {
            Text(
                text = prediction.summaryLine(),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        } else {
            PredictionLine("Winner", prediction.winnerTeam)
            PredictionLine("Top scorer", prediction.topScorerName)
            PredictionLine("Top bowler", prediction.topBowlerName)
        }
    }
}

@Composable
private fun PredictionLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
