package com.example.jetpacktutorial.feature.spash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // State for animations
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }

    // Start animations and timer for navigation
    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = EaseOutBack // This provides the overshoot effect
                ) // Custom spring-like easing
            )
        }
        launch {
            alpha.animateTo(1f, tween(1000))
        }
        delay(3000) // Stay on screen for 3 seconds
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08080C)), // Surface Foundation
        contentAlignment = Alignment.Center
    ) {
        // Layer 1: Background Pulse/Glow
        RadialGlowBackground()

        // Layer 2: Main Branding
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value).alpha(alpha.value)
        ) {
            LogoIcon() // The shield icon
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "FanArena",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Layer 3: Animated Loading Bar (at the bottom)
        LinearProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .width(200.dp),
            color = Color(0xFF00D1FF), // Electric Blue accent
            trackColor = Color.DarkGray
        )
    }
}

@Composable
fun LogoIcon(modifier: Modifier = Modifier) {
    // Use a fixed aspect ratio for the shield to keep it consistent
    Box(
        modifier = modifier
            .size(120.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center // Strictly centers all children
    ) {
        // Layer 1: The Outer Glow (Pulse Effect)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00D1FF).copy(alpha = 0.3f), Color.Transparent),
                    center = center,
                    radius = size.maxDimension / 1.5f
                )
            )
        }

        // Layer 2: The Shield Vector
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(size.width * 0.15f, size.height * 0.05f)
                lineTo(size.width * 0.85f, size.height * 0.05f)
                lineTo(size.width * 0.85f, size.height * 0.65f)
                quadraticBezierTo(
                    size.width * 0.5f, size.height * 0.98f,
                    size.width * 0.15f, size.height * 0.65f
                )
                close()
            }

            // High-contrast Gradient Fill
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1D2E), Color(0xFF08080C))
                )
            )

            // Neon Stroke
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF00D1FF), Color(0xFFAD00FF))
                ),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Layer 3: Centered Initials
        // We use a Box wrapper to ensure Text isn't affected by its own internal height/leading
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "FA",
                color = Color.White,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-2).sp, // Tighter kerning for "Sporty" look
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false // REMOVES default top/bottom font padding
                    )
                )
            )
        }
    }
}

@Composable
fun RadialGlowBackground(modifier: Modifier = Modifier) {
    // 1. Define an infinite transition for the "Pulse" effect
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundPulse")

    // 2. Animate the radius of the glow to make it "breathe"
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowRadius"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 3. Draw a large radial gradient centered behind the logo
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF00D1FF).copy(alpha = 0.15f), // Electric Blue center
                    Color(0xFFAD00FF).copy(alpha = 0.05f), // Fading to Neon Purple
                    Color.Transparent                     // Fading into Black
                ),
                center = Offset(canvasWidth / 2, canvasHeight / 2),
                radius = (canvasWidth * 0.8f) * glowScale
            ),
            size = size
        )

        // 4. Optional: Subtle scanlines or grid for that "Esports Dashboard" look
        val lineSpacing = 40f
        for (x in 0..canvasWidth.toInt() step lineSpacing.toInt()) {
            drawLine(
                color = Color.White.copy(alpha = 0.02f),
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), canvasHeight),
                strokeWidth = 1f
            )
        }
    }
}
