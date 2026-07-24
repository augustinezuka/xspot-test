package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

enum class CompactButtonStyle {
  Primary,
  Secondary,
  Outlined,
  Ghost
}

/**
 * Sleek, compact button without excessive default Material padding.
 * Specifically tuned for field technician quick actions.
 */
@Composable
fun CompactButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  style: CompactButtonStyle = CompactButtonStyle.Primary,
  enabled: Boolean = true,
  height: Dp = 34.dp,
  contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  val primaryBg = if (isDark) Ember300 else Ember600
  val secondaryBg = if (isDark) Color(0xFF334155) else Color(0xFFFFEDD5)

  val (bgColor, rawTextColor, borderColor) = when (style) {
    CompactButtonStyle.Primary -> Triple(
      primaryBg,
      if (primaryBg.luminance() < 0.5f) Color.White else Slate950,
      Color.Transparent
    )
    CompactButtonStyle.Secondary -> Triple(
      secondaryBg,
      if (isDark) Ember300 else Ember600,
      Color.Transparent
    )
    CompactButtonStyle.Outlined -> Triple(
      Color.Transparent,
      if (isDark) Color(0xFFF1F5F9) else Slate900,
      if (isDark) DarkGlassBorder else LightGlassBorder
    )
    CompactButtonStyle.Ghost -> Triple(
      Color.Transparent,
      if (isDark) Ember300 else Ember600,
      Color.Transparent
    )
  }

  val textColor = if (bgColor != Color.Transparent && bgColor.luminance() < 0.5f && style != CompactButtonStyle.Secondary) {
    Color.White
  } else {
    rawTextColor
  }

  val shape = RoundedCornerShape(8.dp)

  val alphaModifier = if (enabled) 1.0f else 0.4f
  val finalBgColor = if (bgColor == Color.Transparent) Color.Transparent else bgColor.copy(alpha = alphaModifier)

  Box(
    modifier = modifier
      .height(height)
      .clip(shape)
      .background(finalBgColor)
      .let {
        if (borderColor != Color.Transparent) {
          it.border(1.dp, borderColor.copy(alpha = alphaModifier), shape)
        } else it
      }
      .let {
        if (enabled) it.clickable { onClick() } else it
      }
      .padding(contentPadding),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = textColor.copy(alpha = alphaModifier),
          modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
      }
      Text(
        text = text,
        color = textColor.copy(alpha = alphaModifier),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

