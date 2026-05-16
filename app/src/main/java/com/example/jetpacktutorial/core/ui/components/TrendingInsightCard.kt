package com.example.jetpacktutorial.core.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktutorial.core.data.model.PredictionInsightCard
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.core.ui.theme.IPLPurpleGradient
import com.example.jetpacktutorial.core.ui.theme.SurfaceGlass
import com.example.jetpacktutorial.feature.home.glassmorphicCard
import java.util.Locale

@Composable
fun TrendingInsightCard(
    insight: PredictionInsightCard,
    onOptionVote: (Int) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onOpenMatchPrediction: ((String) -> Unit)? = null,
) {
    val hasVoted = insight.userSelectedOptionIndex != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (compact) Modifier.width(280.dp) else Modifier)
            .glassmorphicCard()
            .padding(if (compact) 12.dp else 16.dp)
            .animateContentSize(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                insight.category.uppercase(Locale.ROOT),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.Gray,
                letterSpacing = 0.5.sp,
            )
            insight.trendingTag?.let { tag ->
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFF5722).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(tag, color = Color(0xFFFF5722), fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(if (compact) 6.dp else 10.dp))

        Text(
            text = insight.question,
            color = Color.White,
            fontSize = if (compact) 13.sp else 15.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = if (compact) 18.sp else 22.sp,
        )

        if (!compact) {
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (!hasVoted) {
            TrendingOptionRow(
                name = insight.option1Name,
                percentage = insight.option1Percentage,
                brush = IPLOrangeGradient,
                selectable = true,
                onClick = { onOptionVote(0) },
                compact = compact,
            )
            Spacer(modifier = Modifier.height(if (compact) 6.dp else 10.dp))
            TrendingOptionRow(
                name = insight.option2Name,
                percentage = insight.option2Percentage,
                brush = IPLPurpleGradient,
                selectable = true,
                onClick = { onOptionVote(1) },
                compact = compact,
            )
        } else {
            TrendingOptionRow(
                name = insight.option1Name,
                percentage = insight.option1Percentage,
                brush = IPLOrangeGradient,
                selectable = false,
                isUserChoice = insight.userSelectedOptionIndex == 0,
                compact = compact,
            )
            Spacer(modifier = Modifier.height(if (compact) 6.dp else 10.dp))
            TrendingOptionRow(
                name = insight.option2Name,
                percentage = insight.option2Percentage,
                brush = IPLPurpleGradient,
                selectable = false,
                isUserChoice = insight.userSelectedOptionIndex == 1,
                compact = compact,
            )
        }

        Spacer(modifier = Modifier.height(if (compact) 8.dp else 12.dp))

        Text(
            text = insight.totalVotersCount,
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )

        val matchId = insight.matchId
        if (matchId != null && onOpenMatchPrediction != null && !compact) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentNeonGlow.copy(alpha = 0.12f))
                    .border(1.dp, AccentNeonGlow.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .clickable { onOpenMatchPrediction(matchId) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Make full match prediction →",
                    color = AccentNeonGlow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun TrendingOptionRow(
    name: String,
    percentage: Int,
    brush: Brush,
    selectable: Boolean,
    onClick: () -> Unit = {},
    isUserChoice: Boolean = false,
    compact: Boolean,
) {
    val fraction = percentage / 100f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (selectable) {
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(1.dp, BorderGlass, RoundedCornerShape(10.dp))
                        .clickable(onClick = onClick)
                        .padding(10.dp)
                } else {
                    Modifier
                },
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    name,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = if (compact) 12.sp else 13.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (isUserChoice) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "• Your vote",
                        color = Color(0xFFFF5722),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Text(
                "$percentage%",
                color = Color.White,
                fontSize = if (compact) 12.sp else 13.sp,
                fontWeight = FontWeight.Black,
            )
        }
        if (!selectable || !compact) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 6.dp else 8.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .clip(CircleShape)
                        .background(brush),
                )
            }
        }
    }
}
