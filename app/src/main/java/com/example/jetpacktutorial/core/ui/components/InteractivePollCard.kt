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
import com.example.jetpacktutorial.core.data.model.InteractivePollCard
import com.example.jetpacktutorial.core.ui.theme.AccentNeonGlow
import com.example.jetpacktutorial.core.ui.theme.BorderGlass
import com.example.jetpacktutorial.core.ui.theme.IPLOrangeGradient
import com.example.jetpacktutorial.feature.home.glassmorphicCard
import java.util.Locale

@Composable
fun InteractivePollCardRow(
    poll: InteractivePollCard,
    onVoteClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasVoted = poll.userSelectedOptionIndex != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .glassmorphicCard()
            .padding(16.dp)
            .animateContentSize(),
    ) {
        Text(
            text = poll.category.uppercase(Locale.ROOT),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = AccentNeonGlow,
            letterSpacing = 0.5.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = poll.question,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasVoted) {
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
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = option,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        } else {
            poll.optionsList.forEachIndexed { index, option ->
                val resultPercentage = poll.voteDistribution.getOrElse(index) { 0 }
                val fractionFill = resultPercentage / 100f
                val isUserChoice = poll.userSelectedOptionIndex == index

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = option,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            if (isUserChoice) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• Your vote",
                                    color = Color(0xFFFF5722),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Text(
                            text = "$resultPercentage%",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.04f)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fractionFill)
                                .clip(CircleShape)
                                .background(
                                    if (isUserChoice) {
                                        IPLOrangeGradient
                                    } else {
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color.Gray.copy(alpha = 0.3f),
                                                Color.Gray.copy(alpha = 0.1f),
                                            ),
                                        )
                                    },
                                ),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${poll.totalVotesFormatted} accumulated",
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
