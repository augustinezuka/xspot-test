package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber600
import com.example.ui.theme.Green300
import com.example.ui.theme.Green600
import com.example.ui.theme.Red300
import com.example.ui.theme.Red600
import com.example.ui.theme.Umber800

/**
 * Signature XSpot 4-Bar Signal Indicator (▂ ▄ ▆ █)
 * Represents health state across Routers, Locations, Map Pins, and Stat Cards.
 */
@Composable
fun SignalBarGlyph(
  bars: Int, // 0 to 4
  modifier: Modifier = Modifier,
  barWidth: Dp = 4.dp,
  maxBarHeight: Dp = 16.dp,
  barSpacing: Dp = 2.dp,
  isDarkTheme: Boolean = true
) {
  val boundedBars = bars.coerceIn(0, 4)

  // Color logic according to design spec:
  // 4 bars, success green -> fully healthy
  // 3 bars, success green -> healthy, minor staleness
  // 2 bars, warning amber -> degraded
  // 1 bar,  danger red    -> mostly down
  // 0 bars, danger red    -> offline
  val activeColor = when (boundedBars) {
    4, 3 -> if (isDarkTheme) Green300 else Green600
    2 -> if (isDarkTheme) Amber300 else Amber600
    else -> if (isDarkTheme) Red300 else Red600
  }

  val inactiveColor = if (isDarkTheme) Umber800.copy(alpha = 0.5f) else Color.LightGray.copy(alpha = 0.4f)

  val barHeights = listOf(
    maxBarHeight * 0.3f, // Bar 1
    maxBarHeight * 0.52f, // Bar 2
    maxBarHeight * 0.76f, // Bar 3
    maxBarHeight * 1.0f  // Bar 4
  )

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(barSpacing),
    verticalAlignment = Alignment.Bottom
  ) {
    for (i in 0 until 4) {
      val isFilled = i < boundedBars
      val color = if (isFilled) activeColor else inactiveColor

      Box(
        modifier = Modifier
          .width(barWidth)
          .height(barHeights[i])
          .clip(RoundedCornerShape(1.dp))
          .background(color)
      )
    }
  }
}
