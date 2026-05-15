package com.example.jetpacktutorial.feature.auth

import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jetpacktutorial.feature.spash.LogoIcon
import kotlin.math.cos
import kotlin.math.sin


@Preview
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    gotoHome:()-> Unit
) {
    val context = LocalContext.current




    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var loginStatus by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.isAuthenticated) {

        if (uiState.isAuthenticated) {
            gotoHome()

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        WarpSpeedBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .navigationBarsPadding(), // Ensures padding for Android gesture bar
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            LogoIcon(modifier = Modifier.size(140.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FANARENA",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                ),
                color = Color.White
            )

            Text(
                text = "THE SECOND SCREEN FOR CHAMPIONS",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF00D1FF),
                modifier = Modifier.padding(top = 4.dp, bottom = 64.dp)
            )

            Text("is Login $loginStatus")


            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Google Sign In
                    LoginButton(
                        text = "Sign in with Google",
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        onClick = {
                            viewModel.onEvent(
                                LoginEvent.GoogleSignIn(context as Activity)
                            )
                        },
                       isLoading =  uiState.isLoading
                    )

                    Spacer(Modifier.height(12.dp))

                    // Guest Access
                    LoginButton(
                        text = "Enter as Guest",
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        onClick = {   viewModel.onEvent(LoginEvent.GuestSignIn)},
                        isOutlined = true,
                        isLoading = uiState.isGuestLoading
                    )
                }
            }






            uiState.error?.let {
                Log.e("firebaselog", it)
                Text(
                    text = it
                )
            }
        }
    }
}


@Composable
fun WarpSpeedBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "WarpPulse")

    // Animate a phase shift to create movement
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PhaseShift"
    )

    Canvas(modifier = modifier
        .fillMaxSize()
        .background(Color(0xFF08080C))) {
        val center = Offset(size.width / 2, size.height * 0.4f) // Center slightly higher for logo

        // 1. Draw Deep Space Glow
        drawRect(
            brush = Brush.radialGradient(
                0.0f to Color(0xFF121420),
                0.6f to Color(0xFF08080C),
                center = center,
                radius = size.width
            )
        )

        // 2. Draw Radiating Light Streaks
        val lineCount = 12
        for (i in 0 until lineCount) {
            val angle = (i.toFloat() / lineCount) * 2 * Math.PI.toFloat() + (phase * 0.1f)
            val lineLength = size.width * 1.5f
            val endX = center.x + cos(angle) * lineLength
            val endY = center.y + sin(angle) * lineLength

            drawLine(
                brush = Brush.linearGradient(
                    0f to Color.Transparent,
                    0.4f to Color(0xFF00D1FF).copy(alpha = 0.2f),
                    1f to Color(0xFFAD00FF).copy(alpha = 0.5f),
                    start = center,
                    end = Offset(endX, endY)
                ),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }

        // 3. Floating "Data Particles"
        val random = java.util.Random(42)
        repeat(30) {
            val pAngle = random.nextFloat() * 2 * Math.PI.toFloat()
            val pDist = (random.nextFloat() * size.width * 0.8f + (phase * 50) % size.width)
            val px = center.x + cos(pAngle) * pDist
            val py = center.y + sin(pAngle) * pDist

            drawCircle(
                color = Color(0xFF00D1FF).copy(alpha = 0.3f),
                radius = 3f,
                center = Offset(px, py)
            )
        }
    }
}

@Composable
fun LoginButton(
    text: String,
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black,
    isOutlined: Boolean = false,
    onClick: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isOutlined) Color.Transparent else containerColor,
        border = if (isOutlined) BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)) else null
    ) {
        AnimatedContent(targetState = isLoading) { loading ->
            if (loading) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = contentColor,
                        strokeWidth = 2.dp
                    )
                }

            } else {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {


                    Text(
                        text = text,
                        style = TextStyle(
                            color = contentColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }



    }
}