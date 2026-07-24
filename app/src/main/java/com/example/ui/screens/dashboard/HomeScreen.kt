package com.example.ui.screens.dashboard

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.SignalCellularOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityItem
import com.example.data.model.DashboardOverview
import com.example.data.model.UserRole
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
import com.example.ui.theme.Red300
import com.example.ui.theme.Umber400

@Composable
fun HomeScreen(
  overview: DashboardOverview,
  activities: List<ActivityItem>,
  userRole: UserRole,
  isAppOnline: Boolean,
  onNavigateToVoucherGenerate: () -> Unit,
  onNavigateToAddLocation: () -> Unit,
  onNavigateToTestRouter: () -> Unit,
  onNavigateToValidateVoucher: () -> Unit,
  onActivityClick: (ActivityItem) -> Unit
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  Column(

    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Header Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Welcome, Super Admin",
          style = MaterialTheme.typography.headlineMedium,
          color = if (isDark) Color.White else Color.Black,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          StatusChip(
            text = "Super Admin",
            type = ChipType.Neutral
          )
          Spacer(modifier = Modifier.width(8.dp))
          // Field Connectivity Indicator
          StatusChip(
            text = if (isAppOnline) "Field Online" else "Field Offline (Queue Active)",
            type = if (isAppOnline) ChipType.Success else ChipType.Warning
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Horizontally Scrolling Stat Cards
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // 1. Routers Stat Card with Signal Bar Glyph
      GlassCard(
        modifier = Modifier.width(150.dp),
        cornerRadius = 16.dp,
        contentPadding = 14.dp
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Router,
              contentDescription = null,
              tint = if (isDark) Ember300 else Ember600,
              modifier = Modifier.size(20.dp)
            )
            SignalBarGlyph(
              bars = 4,
              barWidth = 3.dp,
              maxBarHeight = 14.dp,
              isDarkTheme = isDark
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text("Routers", fontSize = 13.sp, color = Umber400)
          Text(
            text = "${overview.routersOnline}/${overview.totalRouters}",
            style = MaterialTheme.typography.displayLarge,
            color = if (isDark) Color.White else Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
          )
        }
      }

      // 2. Locations Stat Card
      GlassCard(
        modifier = Modifier.width(140.dp),
        cornerRadius = 16.dp,
        contentPadding = 14.dp
      ) {
        Column {
          Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            tint = if (isDark) Ember300 else Ember600,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text("Locations", fontSize = 13.sp, color = Umber400)
          Text(
            text = "${overview.totalLocations}",
            style = MaterialTheme.typography.displayLarge,
            color = if (isDark) Color.White else Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
          )
        }
      }

      // 3. Revenue Today Stat Card
      GlassCard(
        modifier = Modifier.width(160.dp),
        cornerRadius = 16.dp,
        contentPadding = 14.dp
      ) {
        Column {
          Icon(
            Icons.Default.ElectricalServices,
            contentDescription = null,
            tint = if (isDark) Ember300 else Ember600,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text("Revenue today", fontSize = 13.sp, color = Umber400)
          Text(
            text = "$${overview.revenueToday.toInt()}",
            style = MaterialTheme.typography.displayLarge,
            color = if (isDark) Color.White else Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
          )
        }
      }

      // 4. Active HotSpot Sessions Stat Card
      GlassCard(
        modifier = Modifier.width(150.dp),
        cornerRadius = 16.dp,
        contentPadding = 14.dp
      ) {
        Column {
          Icon(
            Icons.Default.Wifi,
            contentDescription = null,
            tint = if (isDark) Green300 else Green600,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text("Active users", fontSize = 13.sp, color = Umber400)
          Text(
            text = "${overview.activeSessions}",
            style = MaterialTheme.typography.displayLarge,
            color = if (isDark) Color.White else Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Quick Actions
    Text(
      text = "Quick Field Actions",
      style = MaterialTheme.typography.titleMedium,
      color = if (isDark) Color.White else Color.Black,
      fontWeight = FontWeight.SemiBold
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      CompactButton(
        text = "+ Bulk Vouchers",
        onClick = onNavigateToVoucherGenerate,
        icon = Icons.Default.ConfirmationNumber,
        style = CompactButtonStyle.Primary
      )

      CompactButton(
        text = "+ Add Location",
        onClick = onNavigateToAddLocation,
        icon = Icons.Default.Add,
        style = CompactButtonStyle.Secondary
      )

      CompactButton(
        text = "Test Router OS",
        onClick = onNavigateToTestRouter,
        icon = Icons.Default.NetworkCheck,
        style = CompactButtonStyle.Outlined
      )

      CompactButton(
        text = "Redeem / Validate",
        onClick = onNavigateToValidateVoucher,
        icon = Icons.Default.QrCodeScanner,
        style = CompactButtonStyle.Outlined
      )
    }

    Spacer(modifier = Modifier.height(28.dp))

    // Live Activity Feed
    Text(
      text = "Live Network Activity",
      style = MaterialTheme.typography.titleMedium,
      color = if (isDark) Color.White else Color.Black,
      fontWeight = FontWeight.SemiBold
    )

    Spacer(modifier = Modifier.height(12.dp))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      activities.forEach { item ->
        GlassCard(
          modifier = Modifier.fillMaxWidth(),
          cornerRadius = 14.dp,
          onClick = { onActivityClick(item) },
          contentPadding = 12.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Activity Kind Icon or Signal Glyph
            if (item.kind == "voucher_activated") {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isDark) Green300 else Green600,
                modifier = Modifier.size(22.dp)
              )
            } else {
              SignalBarGlyph(
                bars = item.signalBars,
                barWidth = 3.dp,
                maxBarHeight = 16.dp,
                isDarkTheme = isDark
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = item.message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) Color.White else Color.Black,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${item.locationName} · ${item.occurredAt}",
                fontSize = 12.sp,
                color = Umber400
              )
            }
          }
        }
      }
    }
  }
}
