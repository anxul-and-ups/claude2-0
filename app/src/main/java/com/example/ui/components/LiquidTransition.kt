package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.sin

/**
 * Soft liquid "wave reveal" that sweeps across the content area whenever [trigger] changes
 * (for example the selected folder). It is purely decorative, never intercepts touches and has
 * no connection to the editor — typing / Enter can never start it.
 */
@Composable
fun LiquidWaveOverlay(trigger: Any, color: Color, modifier: Modifier = Modifier) {
    val progress = remember { Animatable(1f) }
    var first by remember { mutableStateOf(true) }
    LaunchedEffect(trigger) {
        if (first) { first = false; return@LaunchedEffect }
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 620, easing = FastOutSlowInEasing))
    }
    val p = progress.value
    if (p < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val amp = w * 0.07f
            val front = (w + amp * 2f) * p - amp
            val path = Path().apply {
                moveTo(0f, 0f)
                var y = 0f
                val step = h / 36f
                while (y <= h + 0.5f) {
                    val x = front + amp * sin((y / h) * 2f * PI.toFloat() * 1.4f + p * 7f)
                    lineTo(x, y)
                    y += step
                }
                lineTo(0f, h)
                close()
            }
            val alpha = (1f - p) * 0.32f
            drawPath(
                path,
                brush = Brush.horizontalGradient(
                    listOf(color.copy(alpha = alpha * 0.2f), color.copy(alpha = alpha)),
                    startX = 0f,
                    endX = (front + amp).coerceAtLeast(1f)
                )
            )
        }
    }
}
