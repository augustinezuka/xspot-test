package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Location
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.DarkGlassSurface
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.Green300
import com.example.ui.theme.Green600
import com.example.ui.theme.Umber400
import com.example.ui.theme.Umber800

/**
 * Custom Map Canvas rendering site pins with mini 4-bar signal indicators.
 * Strictly no emojis!
 */
@Composable
fun InteractiveMapView(
  locations: List<Location>,
  onLocationSelected: (Location) -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
  var selectedLocation by remember { mutableStateOf<Location?>(null) }
  var zoomLevel by remember { mutableFloatStateOf(1.0f) }

  // Map theme colors
  val mapBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9)
  val roadColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
  val gridColor = if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFCBD5E1).copy(alpha = 0.6f)
  val pinBg = if (isDark) Color(0xFF1E293B) else Color.White

  Box(
    modifier = modifier
      .fillMaxSize()
      .clip(RoundedCornerShape(16.dp))
      .background(mapBg)
      .border(1.dp, if (isDark) DarkGlassBorder else Color(0xFFE3D5C8), RoundedCornerShape(16.dp))
  ) {
    // Vector Canvas Map Drawing
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(locations) {
          detectTapGestures { tapOffset ->
            // Match click position to closest pin
            val canvasW = size.width
            val canvasH = size.height

            locations.forEachIndexed { index, loc ->
              // Map lat/lng coordinates onto canvas
              val x = (canvasW * 0.25f) + (index * canvasW * 0.2f)
              val y = (canvasH * 0.3f) + ((index % 3) * canvasH * 0.2f)

              val distance = (tapOffset.x - x) * (tapOffset.x - x) + (tapOffset.y - y) * (tapOffset.y - y)
              if (distance < 3600f) { // Within 60px tap radius
                selectedLocation = loc
              }
            }
          }
        }
    ) {
      val width = size.width
      val height = size.height

      // 1. Draw Grid Lines & Roads
      val stepX = width / 6f
      val stepY = height / 6f

      for (i in 0..6) {
        drawLine(
          color = gridColor,
          start = Offset(i * stepX, 0f),
          end = Offset(i * stepX, height),
          strokeWidth = 1f
        )
        drawLine(
          color = gridColor,
          start = Offset(0f, i * stepY),
          end = Offset(width, i * stepY),
          strokeWidth = 1f
        )
      }

      // Draw stylized road networks
      val mainRoad = Path().apply {
        moveTo(0f, height * 0.45f)
        cubicTo(width * 0.3f, height * 0.4f, width * 0.6f, height * 0.6f, width, height * 0.5f)
      }
      drawPath(mainRoad, color = roadColor, style = Stroke(width = 8f * zoomLevel))

      val crossRoad = Path().apply {
        moveTo(width * 0.5f, 0f)
        cubicTo(width * 0.48f, height * 0.4f, width * 0.52f, height * 0.7f, width * 0.45f, height)
      }
      drawPath(crossRoad, color = roadColor, style = Stroke(width = 6f * zoomLevel))

      // 2. Draw Pins for each Location
      locations.forEachIndexed { index, loc ->
        val x = (width * 0.25f) + (index * width * 0.2f)
        val y = (height * 0.3f) + ((index % 3) * height * 0.2f)

        val isSelected = selectedLocation?.id == loc.id
        val signalBars = if (loc.isActive) 4 else 1

        // Outer glow/ring if selected
        if (isSelected) {
          drawCircle(
            color = if (isDark) Ember300.copy(alpha = 0.3f) else Ember600.copy(alpha = 0.3f),
            radius = 28f * zoomLevel,
            center = Offset(x, y)
          )
        }

        // Pin Card Container
        val cardWidth = 84f * zoomLevel
        val cardHeight = 36f * zoomLevel

        drawRoundRect(
          color = if (isSelected) (if (isDark) Ember300 else Ember600) else pinBg,
          topLeft = Offset(x - cardWidth / 2, y - cardHeight / 2),
          size = Size(cardWidth, cardHeight),
          cornerRadius = CornerRadius(12f, 12f)
        )

        drawRoundRect(
          color = if (isDark) DarkGlassBorder else Color(0xFFE3D5C8),
          topLeft = Offset(x - cardWidth / 2, y - cardHeight / 2),
          size = Size(cardWidth, cardHeight),
          cornerRadius = CornerRadius(12f, 12f),
          style = Stroke(width = 2f)
        )

        // Draw Mini 4-Bar Signal Glyph inside pin
        val barColor = if (isSelected) Color.White else (if (signalBars >= 3) (if (isDark) Green300 else Green600) else Color.Red)
        val barStartX = x - 24f
        val barStartY = y + 6f

        for (b in 0..3) {
          val h = (b + 1) * 3.5f
          val bx = barStartX + (b * 6f)
          val isFilled = b < signalBars
          drawRect(
            color = if (isFilled) barColor else Color.Gray.copy(alpha = 0.4f),
            topLeft = Offset(bx, barStartY - h),
            size = Size(4f, h)
          )
        }
      }
    }

    // Map Floating Action Controls (Zoom In/Out, Center)
    Column(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(if (isDark) DarkGlassSurface else Color.White)
          .border(1.dp, if (isDark) DarkGlassBorder else Color(0xFFE3D5C8), CircleShape)
          .clickable { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(2.0f) },
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = if (isDark) Color.White else Color.Black, modifier = Modifier.size(18.dp))
      }

      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(if (isDark) DarkGlassSurface else Color.White)
          .border(1.dp, if (isDark) DarkGlassBorder else Color(0xFFE3D5C8), CircleShape)
          .clickable { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.6f) },
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = if (isDark) Color.White else Color.Black, modifier = Modifier.size(18.dp))
      }

      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(if (isDark) DarkGlassSurface else Color.White)
          .border(1.dp, if (isDark) DarkGlassBorder else Color(0xFFE3D5C8), CircleShape)
          .clickable { zoomLevel = 1.0f; selectedLocation = locations.firstOrNull() },
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.MyLocation, contentDescription = "Reset Location", tint = if (isDark) Ember300 else Ember600, modifier = Modifier.size(18.dp))
      }
    }

    // Selected Pin Pop-Up Card
    AnimatedVisibility(
      visible = selectedLocation != null,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(12.dp)
        .fillMaxWidth()
    ) {
      selectedLocation?.let { loc ->
        GlassCard(
          modifier = Modifier.fillMaxWidth(),
          cornerRadius = 14.dp,
          contentPadding = 12.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                SignalBarGlyph(
                  bars = if (loc.isActive) 4 else 1,
                  barWidth = 3.dp,
                  maxBarHeight = 12.dp,
                  isDarkTheme = isDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = loc.name,
                  style = MaterialTheme.typography.titleMedium,
                  color = if (isDark) Color.White else Color.Black,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = loc.address ?: "Harare Site",
                fontSize = 12.sp,
                color = Umber400
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              CompactButton(
                text = "View",
                onClick = { onLocationSelected(loc) },
                icon = Icons.Default.ArrowForward,
                style = CompactButtonStyle.Primary
              )

              Spacer(modifier = Modifier.width(6.dp))

              IconButton(
                onClick = { selectedLocation = null },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  Icons.Default.Close,
                  contentDescription = "Close",
                  tint = Umber400,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}
