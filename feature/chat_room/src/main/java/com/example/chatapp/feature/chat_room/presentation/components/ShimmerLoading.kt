package com.example.chatapp.feature.chat_room.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shimmer loading animation for skeleton screens.
 * Creates a pulsing shimmer effect to indicate loading state.
 */
@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier,
    width: Float = 200f,
    height: Float = 20f
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by transition.animateFloat(
        initialValue = -width,
        targetValue = width * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1300,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )

    Box(
        modifier = modifier
            .width(width.dp)
            .height(height.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = shimmerColors,
                    start = androidx.compose.ui.geometry.Offset(shimmerX, 0f),
                    end = androidx.compose.ui.geometry.Offset(shimmerX + width, height)
                ),
                shape = RoundedCornerShape(4.dp)
            )
    )
}

/**
 * Message skeleton loader - shows placeholder while loading messages.
 */
@Composable
fun MessageSkeletonLoader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Avatar skeleton
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(20.dp)
                    )
            )

            // Message bubble skeleton
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp)
            ) {
                ShimmerLoading(width = 100f, height = 12f)
                Spacer(modifier = Modifier.height(8.dp))
                ShimmerLoading(width = 200f, height = 16f)
                Spacer(modifier = Modifier.height(4.dp))
                ShimmerLoading(width = 150f, height = 12f)
            }
        }
    }
}
