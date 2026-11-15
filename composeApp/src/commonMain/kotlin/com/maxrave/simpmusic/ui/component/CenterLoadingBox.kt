package com.maxrave.simpmusic.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Quiet iOS-style activity indicator: eight fading graphite/foreground beads rotating
 * continuously, without a Material loading container or color-filled polygon.
 */
@Composable
fun CenterLoadingBox(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "ios-loading")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
        ),
        label = "ios-loading-rotation",
    )

    Box(modifier = modifier) {
        Canvas(
            Modifier
                .size(28.dp)
                .wrapContentSize()
                .align(Alignment.Center)
                .rotate(rotation),
        ) {
            val beadRadius = size.minDimension * 0.065f
            val orbitRadius = size.minDimension * 0.36f
            repeat(8) { index ->
                val angle = index * (2.0 * PI / 8.0)
                drawCircle(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = (index + 1) / 8f),
                    radius = beadRadius,
                    center = androidx.compose.ui.geometry.Offset(
                        x = center.x + cos(angle).toFloat() * orbitRadius,
                        y = center.y + sin(angle).toFloat() * orbitRadius,
                    ),
                )
            }
        }
    }
}
