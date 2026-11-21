package com.maxrave.simpmusic.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

/**
 * Shared animated loader used by page, dialog and player loading states.
 * A quiet track with two independently rotating arcs replaces the default Material spinner.
 */
@Composable
fun LiquidGlassLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.09f)
    val secondaryColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f)
    val transition = rememberInfiniteTransition(label = "glassLoading")
    val rotation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(1250, easing = LinearEasing)),
        label = "glassLoadingRotation",
    )
    val secondaryRotation = transition.animateFloat(
        initialValue = 180f,
        targetValue = 540f,
        animationSpec = infiniteRepeatable(animation = tween(1900, easing = LinearEasing)),
        label = "glassLoadingSecondaryRotation",
    )

    Canvas(modifier = modifier.size(size)) {
        val stroke = this.size.minDimension * 0.075f
        drawCircle(color = trackColor, style = Stroke(width = stroke))
        drawArc(
            color = tint.copy(alpha = 0.95f),
            startAngle = rotation.value,
            sweepAngle = 238f,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        drawArc(
            color = secondaryColor,
            startAngle = secondaryRotation.value,
            sweepAngle = 62f,
            useCenter = false,
            style = Stroke(width = stroke * 0.7f, cap = StrokeCap.Round),
        )
    }
}

@Composable
fun CenterLoadingBox(modifier: Modifier) {
    Box(modifier = modifier) {
        LiquidGlassLoadingIndicator(
            modifier = Modifier.align(Alignment.Center),
            size = 52.dp,
        )
    }
}
