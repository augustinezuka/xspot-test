package com.example.ui.screens.routers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.HotspotSession
import com.example.data.model.Router
import com.example.data.model.RouterHealth
import com.example.data.model.RouterStatus
import com.example.ui.components.CompactButton
import com.example.ui.components.CompactButtonStyle
import com.example.ui.components.GlassCard
import com.example.ui.components.SignalBarGlyph
import com.example.ui.components.StatusChip
import com.example.ui.components.ChipType
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.Green300
import com.example.ui.theme.Green600
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.Umber400

@Composable
fun RouterDetailScreen(
  router: Router,
  health: RouterHealth,
  sessions: List<HotspotSession>,
  onBackClick: () -> Unit,
  onRefreshHealth: () -> Unit,
  onDisconnectSession: (String) -> Unit
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  var sessionToDisconnect by remember { mutableStateOf<HotspotSession?>(null) }


  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBackClick) {
        Icon(
          Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = if (isDark) Color.White else Color.Black
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          SignalBarGlyph(
            bars = if (health.online) 4 else 0,
            isDarkTheme = isDark
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = router.name,
            style = MaterialTheme.typography.headlineMedium,
            color = if (isDark) Color.White else Color.Black,
            fontWeight = FontWeight.Bold
          )
        }
        Text(
          text = "IP: ${router.ipAddress}:${router.apiPort}",
          fontSize = 12.sp,
          color = Umber400
        )
      }

      IconButton(onClick = onRefreshHealth) {
        Icon(
          Icons.Default.Refresh,
          contentDescription = "Refresh Health",
          tint = if (isDark) Ember300 else Ember600
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 502 Unreachable Banner if offline
    if (!health.online) {
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14.dp,
        contentPadding = 12.dp
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text("Router Service Unreachable", fontSize = 13.sp, color = Color.Red, fontWeight = FontWeight.Bold)
            Text(health.message ?: "Can't reach this router right now", fontSize = 12.sp, color = Umber400)
          }
          CompactButton(text = "Retry", onClick = onRefreshHealth, style = CompactButtonStyle.Outlined)
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Health Card
    GlassCard(
      modifier = Modifier.fillMaxWidth(),
      cornerRadius = 16.dp,
      contentPadding = 16.dp
    ) {
      Column {
        Text("RouterOS Health Telemetry", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Identity", fontSize = 12.sp, color = Umber400)
            Text(health.identity ?: "MTK AP", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
          }
          Column {
            Text("Uptime", fontSize = 12.sp, color = Umber400)
            Text(health.uptime ?: "Offline", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
          }
          Column {
            Text("Active Users", fontSize = 12.sp, color = Umber400)
            Text("${health.activeHotspotUsers ?: 0}", style = MaterialTheme.typography.titleMedium, color = if (isDark) Green300 else Green600, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CPU Load Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("CPU Load", fontSize = 12.sp, color = Umber400)
          Text("${health.cpuLoad ?: 0}%", fontSize = 12.sp, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
          progress = { (health.cpuLoad ?: 0) / 100f },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = if ((health.cpuLoad ?: 0) > 70) Color.Red else (if (isDark) Ember300 else Ember600),
          trackColor = if (isDark) Color(0xFF2A2017) else Color(0xFFEAE2D8)
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Live Hotspot Sessions
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Live Active Sessions (${sessions.size})",
        style = MaterialTheme.typography.titleMedium,
        color = if (isDark) Color.White else Color.Black,
        fontWeight = FontWeight.Bold
      )
      CompactButton(text = "Refresh List", onClick = onRefreshHealth, icon = Icons.Default.Refresh, style = CompactButtonStyle.Outlined)
    }

    Spacer(modifier = Modifier.height(12.dp))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      sessions.forEach { sess ->
        GlassCard(
          modifier = Modifier.fillMaxWidth(),
          cornerRadius = 14.dp,
          contentPadding = 12.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = sess.user,
                style = MonospaceCodeStyle,
                color = if (isDark) Color.White else Color.Black
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "IP: ${sess.address} · up ${sess.uptime} · 84MB / 12MB",
                fontSize = 11.sp,
                color = Umber400
              )
            }

            // Disconnect Session Button
            CompactButton(
              text = "Kick",
              onClick = { sessionToDisconnect = sess },
              icon = Icons.Default.PersonRemove,
              style = CompactButtonStyle.Ghost
            )
          }
        }
      }
    }

    // Session Disconnect Confirm Dialog
    sessionToDisconnect?.let { targetSess ->
      Spacer(modifier = Modifier.height(16.dp))
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14.dp,
        contentPadding = 14.dp
      ) {
        Column {
          Text("Kick Active Session?", style = MaterialTheme.typography.titleMedium, color = Color.Red, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Disconnect user ${targetSess.user} on IP ${targetSess.address}? They will be logged off immediately.", fontSize = 12.sp, color = Umber400)
          Spacer(modifier = Modifier.height(12.dp))
          Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            CompactButton(text = "Cancel", onClick = { sessionToDisconnect = null }, style = CompactButtonStyle.Outlined)
            Spacer(modifier = Modifier.width(8.dp))
            CompactButton(
              text = "Confirm Disconnect",
              onClick = {
                onDisconnectSession(targetSess.user)
                sessionToDisconnect = null
              },
              style = CompactButtonStyle.Primary
            )
          }
        }
      }
    }
  }
}
