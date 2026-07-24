package com.example.ui.screens.locations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cluster
import com.example.data.model.Location
import com.example.ui.components.CompactButton
import com.example.ui.components.CompactButtonStyle
import com.example.ui.components.GlassCard
import com.example.ui.components.InteractiveMapView
import com.example.ui.components.SignalBarGlyph
import com.example.ui.components.StatusChip
import com.example.ui.components.ChipType
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.Umber400

@Composable
fun LocationsScreen(
  locations: List<Location>,
  clusters: List<Cluster>,
  onLocationSelected: (Location) -> Unit,
  onAddLocationClick: () -> Unit
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  var isMapView by remember { mutableStateOf(false) }

  var searchQuery by remember { mutableStateOf("") }
  var selectedClusterId by remember { mutableStateOf<String?>(null) }

  val filteredLocations = locations.filter { loc ->
    val matchesSearch = loc.name.contains(searchQuery, ignoreCase = true) ||
        (loc.address?.contains(searchQuery, ignoreCase = true) == true)
    val matchesCluster = selectedClusterId == null || loc.clusterId == selectedClusterId
    matchesSearch && matchesCluster
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    // Header & Segmented Pill Switcher
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Locations",
        style = MaterialTheme.typography.headlineLarge,
        color = if (isDark) Color.White else Color.Black,
        fontWeight = FontWeight.Bold
      )

      // Map / List Segmented Switch
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(if (isDark) Color(0xFF231B13) else Color(0xFFEFE8E1))
          .padding(3.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (!isMapView) (if (isDark) Ember300 else Ember600) else Color.Transparent)
            .clickable { isMapView = false }
            .padding(horizontal = 10.dp, vertical = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.List,
              contentDescription = "List",
              tint = if (!isMapView) Color.White else Umber400,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("List", fontSize = 12.sp, color = if (!isMapView) Color.White else Umber400, fontWeight = FontWeight.SemiBold)
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isMapView) (if (isDark) Ember300 else Ember600) else Color.Transparent)
            .clickable { isMapView = true }
            .padding(horizontal = 10.dp, vertical = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Map,
              contentDescription = "Map",
              tint = if (isMapView) Color.White else Umber400,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Map", fontSize = 12.sp, color = if (isMapView) Color.White else Umber400, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Controls Row (Search & Cluster filter)
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search location name...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Umber400) },
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = if (isDark) Ember300 else Ember600,
          unfocusedBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
        )
      )

      Spacer(modifier = Modifier.width(8.dp))

      CompactButton(
        text = "+ Add",
        onClick = onAddLocationClick,
        icon = Icons.Default.Add,
        style = CompactButtonStyle.Primary
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (isMapView) {
      // First-Class Map Canvas View
      InteractiveMapView(
        locations = filteredLocations,
        onLocationSelected = onLocationSelected,
        modifier = Modifier.fillMaxSize()
      )
    } else {
      // List View
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredLocations) { location ->
          val isOnline = location.isActive
          val signalBars = if (isOnline) 4 else 1

          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
            onClick = { onLocationSelected(location) },
            contentPadding = 14.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Signal bar glyph
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
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDark) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  if (location.clusterId != null) {
                    val clusterName = clusters.find { it.id == location.clusterId }?.name ?: "Cluster"
                    StatusChip(text = clusterName, type = ChipType.Neutral)
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = location.address ?: "Harare Site",
                  fontSize = 12.sp,
                  color = Umber400
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = if (isOnline) "3/3 routers online" else "1/2 routers degraded",
                    fontSize = 12.sp,
                    color = if (isOnline) (if (isDark) Ember300 else Ember600) else Color.Red,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text("•", fontSize = 12.sp, color = Umber400)
                  Text(
                    text = "$210 today",
                    fontSize = 12.sp,
                    color = if (isDark) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
