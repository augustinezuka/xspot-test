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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.Expense
import com.example.data.model.Location
import com.example.data.model.LocationStats
import com.example.data.model.Router
import com.example.data.model.Voucher
import com.example.ui.components.CompactButton
import com.example.ui.components.CompactButtonStyle
import com.example.ui.components.GlassCard
import com.example.ui.components.SignalBarGlyph
import com.example.ui.components.StatusChip
import com.example.ui.components.ChipType
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.Umber400

@Composable
fun LocationDetailScreen(
  location: Location,
  stats: LocationStats,
  clusters: List<Cluster>,
  routers: List<Router>,
  vouchers: List<Voucher>,
  expenses: List<Expense>,
  onBackClick: () -> Unit,
  onAddRouterClick: () -> Unit,
  onGenerateVouchersClick: () -> Unit,
  onAddExpenseClick: () -> Unit
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
  var selectedTab by remember { mutableIntStateOf(0) }
 // 0: Overview, 1: Routers, 2: Vouchers, 3: Expenses

  val cluster = clusters.find { it.id == location.clusterId }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Back & Title Row
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
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          SignalBarGlyph(bars = if (location.isActive) 4 else 1, isDarkTheme = isDark)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = location.name,
            style = MaterialTheme.typography.headlineMedium,
            color = if (isDark) Color.White else Color.Black,
            fontWeight = FontWeight.Bold
          )
        }
        Text(
          text = location.address ?: "Harare Site",
          fontSize = 12.sp,
          color = Umber400
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Segmented Sub-Nav Pills
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(if (isDark) Color(0xFF231B13) else Color(0xFFEFE8E1))
        .padding(4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      val tabs = listOf("Overview", "Routers (${routers.size})", "Vouchers", "Expenses")
      tabs.forEachIndexed { idx, label ->
        val isSelected = selectedTab == idx
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) (if (isDark) Ember300 else Ember600) else Color.Transparent)
            .clickable { selectedTab = idx }
            .padding(vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Umber400,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    when (selectedTab) {
      0 -> {
        // OVERVIEW TAB
        GlassCard(
          modifier = Modifier.fillMaxWidth(),
          cornerRadius = 16.dp,
          contentPadding = 16.dp
        ) {
          Column {
            Text(
              text = "Site Stats & Financials",
              style = MaterialTheme.typography.titleMedium,
              color = if (isDark) Color.White else Color.Black,
              fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Routers Online", fontSize = 12.sp, color = Umber400)
                Text("${stats.routersOnline}/${stats.routerCount}", style = MaterialTheme.typography.titleLarge, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
              }
              Column {
                Text("Active Vouchers", fontSize = 12.sp, color = Umber400)
                Text("${stats.activeVouchers}", style = MaterialTheme.typography.titleLarge, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
              }
              Column {
                Text("Revenue Today", fontSize = 12.sp, color = Umber400)
                Text("$${stats.totalRevenue.toInt()}", style = MaterialTheme.typography.titleLarge, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cluster Membership Card
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Cluster Assignment", fontSize = 12.sp, color = Umber400)
                Text(cluster?.name ?: "No Cluster Assigned", style = MaterialTheme.typography.bodyLarge, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.SemiBold)
              }

              StatusChip(
                text = if (cluster?.sharingEnabled == true) "Cluster Sharing Active" else "Isolated Site",
                type = if (cluster?.sharingEnabled == true) ChipType.Success else ChipType.Neutral
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Monthly Fixed Overhead
            Text("Monthly Site Overheads", fontSize = 12.sp, color = Umber400)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
              Text("Rent: $${location.monthlyRent?.toInt() ?: 0}", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
              Text("Power: $${location.electricity?.toInt() ?: 0}", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
              Text("Fiber: $${location.internetCost?.toInt() ?: 0}", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
            }
          }
        }
      }

      1 -> {
        // ROUTERS TAB
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Routers at this Location", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
          CompactButton(text = "+ Add Router", onClick = onAddRouterClick, icon = Icons.Default.Add, style = CompactButtonStyle.Primary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          routers.forEach { router ->
            GlassCard(
              modifier = Modifier.fillMaxWidth(),
              cornerRadius = 14.dp,
              contentPadding = 12.dp
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Router, contentDescription = null, tint = if (isDark) Ember300 else Ember600, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(router.name, style = MaterialTheme.typography.bodyLarge, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                  Text("IP: ${router.ipAddress}:${router.apiPort} · ${router.lastSeenAt}", fontSize = 12.sp, color = Umber400)
                }
                StatusChip(
                  text = router.status.name,
                  type = if (router.status.name == "online") ChipType.Success else ChipType.Danger
                )
              }
            }
          }
        }
      }

      2 -> {
        // VOUCHERS TAB
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Vouchers for this Location", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
          CompactButton(text = "Bulk Generate", onClick = onGenerateVouchersClick, icon = Icons.Default.ConfirmationNumber, style = CompactButtonStyle.Primary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          vouchers.forEach { voucher ->
            GlassCard(
              modifier = Modifier.fillMaxWidth(),
              cornerRadius = 12.dp,
              contentPadding = 12.dp
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(voucher.code, style = MonospaceCodeStyle, color = if (isDark) Ember300 else Ember600)
                  if (voucher.pin != null) {
                    Text("PIN: ${voucher.pin}", fontSize = 12.sp, color = Umber400)
                  }
                }
                StatusChip(
                  text = voucher.status.name,
                  type = when (voucher.status.name) {
                    "active" -> ChipType.Success
                    "revoked" -> ChipType.Danger
                    else -> ChipType.Neutral
                  }
                )
              }
            }
          }
        }
      }

      3 -> {
        // EXPENSES TAB
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Site Expenses & Outgoings", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
          CompactButton(text = "+ Add Expense", onClick = onAddExpenseClick, icon = Icons.Default.ReceiptLong, style = CompactButtonStyle.Primary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          expenses.forEach { exp ->
            GlassCard(
              modifier = Modifier.fillMaxWidth(),
              cornerRadius = 12.dp,
              contentPadding = 12.dp
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(exp.category.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyLarge, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                  Text(exp.description ?: "Site expense", fontSize = 12.sp, color = Umber400)
                }
                Text("-$${exp.amount.toInt()}", style = MaterialTheme.typography.titleLarge, color = if (isDark) Color.Red else Color.Red, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
