package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState

/** Frosted-glass surface: translucent gradient + soft edge highlight. No blur, no extra layers. */
@Composable
fun HazeGlassCard(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    isDarkMode: Boolean = true,
    strong: Boolean = false,
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val fill = if (isDarkMode) {
        Brush.verticalGradient(
            listOf(
                if (strong) Color(0xCC202632) else Color(0x33FFFFFF),
                if (strong) Color(0xCC161B25) else Color(0x14FFFFFF)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                if (strong) Color(0xE6FFFFFF) else Color(0x99FFFFFF),
                if (strong) Color(0xCCF3F6FA) else Color(0x66FFFFFF)
            )
        )
    }
    val border = if (isDarkMode) {
        Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x0AFFFFFF), Color(0x1AFFFFFF)))
    } else {
        Brush.linearGradient(listOf(Color(0x88FFFFFF), Color(0x22FFFFFF), Color(0x44FFFFFF)))
    }
    val click = if (onClick != null) {
        val source = remember { MutableInteractionSource() }
        Modifier.clickable(interactionSource = source, indication = null, onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .clip(shape)
            .background(fill)
            .border(borderWidth, border, shape)
            .then(click)
    ) { content() }
}
