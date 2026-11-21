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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

/**
 * Lightweight indeterminate loading treatment. The two offset glass-like arcs keep moving
 * without the Material 3 morphing polygon indicator, so loading states share the app's
 * restrained liquid-glass visual language.
 */
@Composable
fun CenterLoadingBox(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "glassLoading")
    val rotation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1250, easing = LinearEasing),
        ),
        label = "glassLoadingRotation",
    )
    val secondaryRotation = transition.animateFloat(
        initialValue = 180f,
        targetValue = 540f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = LinearEasing),
        ),
        label = "glassLoadingSecondaryRotation",
    )

    Box(modifier = modifier) {
        Canvas(
            Modifier
                .size(52.dp)
                .wrapContentSize()
                .align(Alignment.Center),
        ) {
            val stroke = 3.dp.toPx()
            drawCircle(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.09f),
                style = Stroke(width = stroke),
            )
            drawArc(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                startAngle = rotation.value,
                sweepAngle = 238f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f),
                startAngle = secondaryRotation.value,
                sweepAngle = 62f,
                useCenter = false,
                style = Stroke(width = (stroke * 0.7f), cap = StrokeCap.Round),
            )
        }
    }
}
