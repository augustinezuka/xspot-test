package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Liquid Glassmorphic Surface for Dark & Light Mode.
 * Features translucent surface brush, liquid reflection edge borders, and ambient drop shadows.
 */
@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 16.dp,
  onClick: (() -> Unit)? = null,
  contentPadding: Dp = 16.dp,
  content: @Composable BoxScope.() -> Unit
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  val shape = RoundedCornerShape(cornerRadius)

  val surfaceBrush = if (isDark) {
    Brush.verticalGradient(
      colors = listOf(
        Color(0xFF1E293B).copy(alpha = 0.70f),
        Color(0xFF0F172A).copy(alpha = 0.85f)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.78f),
        Color.White.copy(alpha = 0.48f)
      )
    )
  }

  val borderBrush = if (isDark) {
    Brush.verticalGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.35f),
        Color.White.copy(alpha = 0.08f)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.95f),
        Color(0xFF94A3B8).copy(alpha = 0.40f)
      )
    )
  }

  val baseModifier = modifier
    .shadow(
      elevation = if (isDark) 8.dp else 6.dp,
      shape = shape,
      spotColor = if (isDark) Color(0x50000000) else Color(0x1E0F172A)
    )
    .clip(shape)
    .background(surfaceBrush)
    .border(1.dp, borderBrush, shape)
    .let {
      if (onClick != null) it.clickable { onClick() } else it
    }
    .padding(contentPadding)

  Box(
    modifier = baseModifier,
    content = content
  )
}

