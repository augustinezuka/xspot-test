package com.example.ui.screens.routers

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Location
import com.example.data.model.Router
import com.example.data.model.RouterStatus
import com.example.ui.components.CompactButton
import com.example.ui.components.CompactButtonStyle
import com.example.ui.components.GlassCard
import com.example.ui.components.SignalBarGlyph
import com.example.ui.components.StatusChip
import com.example.ui.components.ChipType
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.Umber400

@Composable
fun RoutersScreen(
  routers: List<Router>,
  locations: List<Location>,
  onRouterSelected: (Router) -> Unit,
  onAddRouterClick: () -> Unit,
  onTestConnectionClick: () -> Unit
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  var searchQuery by remember { mutableStateOf("") }


  val filteredRouters = routers.filter { r ->
    r.name.contains(searchQuery, ignoreCase = true) || r.ipAddress.contains(searchQuery)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Router Fleet",
        style = MaterialTheme.typography.headlineLarge,
        color = if (isDark) Color.White else Color.Black,
        fontWeight = FontWeight.Bold
      )

      Row {
        CompactButton(
          text = "Test OS",
          onClick = onTestConnectionClick,
          icon = Icons.Default.NetworkCheck,
          style = CompactButtonStyle.Outlined
        )
        Spacer(modifier = Modifier.width(6.dp))
        CompactButton(
          text = "+ Add",
          onClick = onAddRouterClick,
          icon = Icons.Default.Add,
          style = CompactButtonStyle.Primary
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Search router by identity or IP...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Umber400) },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = if (isDark) Ember300 else Ember600,
        unfocusedBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
      )
    )

    Spacer(modifier = Modifier.height(16.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(filteredRouters) { router ->
        val locName = locations.find { it.id == router.locationId }?.name ?: "Field Site"
        val isOnline = router.status == RouterStatus.online
        val signalBars = if (isOnline) 4 else 0

        GlassCard(
          modifier = Modifier.fillMaxWidth(),
          cornerRadius = 16.dp,
          onClick = { onRouterSelected(router) },
          contentPadding = 14.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            SignalBarGlyph(
              bars = signalBars,
              barWidth = 4.dp,
              maxBarHeight = 18.dp,
              isDarkTheme = isDark
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = router.name,
                  style = MaterialTheme.typography.titleMedium,
                  color = if (isDark) Color.White else Color.Black,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusChip(
                  text = if (isOnline) "Online" else "Offline",
                  type = if (isOnline) ChipType.Success else ChipType.Danger
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = "$locName · IP: ${router.ipAddress}:${router.apiPort}",
                fontSize = 12.sp,
                color = Umber400
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = "Last seen: ${router.lastSeenAt ?: "Unknown"}",
                fontSize = 11.sp,
                color = if (isOnline) (if (isDark) Ember300 else Ember600) else Color.Red
              )
            }
          }
        }
      }
    }
  }
}
