package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber600
import com.example.ui.theme.Green300
import com.example.ui.theme.Green600
import com.example.ui.theme.Red300
import com.example.ui.theme.Red600
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600

enum class ChipType {
  Success,
  Neutral,
  Danger,
  Warning
}

@Composable
fun StatusChip(
  text: String,
  modifier: Modifier = Modifier,
  type: ChipType = ChipType.Neutral
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  val (bgColor, textColor) = when (type) {
    ChipType.Success -> if (isDark) Pair(Color(0xFF064E3B), Green300) else Pair(Color(0xFFDCFCE7), Green600)
    ChipType.Neutral -> if (isDark) Pair(Color(0xFF334155), Slate400) else Pair(Color(0xFFE2E8F0), Slate600)
    ChipType.Danger -> if (isDark) Pair(Color(0xFF7F1D1D), Red300) else Pair(Color(0xFFFEE2E2), Red600)
    ChipType.Warning -> if (isDark) Pair(Color(0xFF78350F), Amber300) else Pair(Color(0xFFFEF3C7), Amber600)
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    Text(
      text = text.lowercase(),
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}

