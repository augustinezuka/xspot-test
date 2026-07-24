package com.example.ui.screens.more

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Expense
import com.example.data.model.OpenAccessSettings
import com.example.data.model.Organization
import com.example.data.model.Promotion
import com.example.data.model.PromotionWinner
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.Voucher
import com.example.ui.components.CompactButton
import com.example.ui.components.CompactButtonStyle
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusChip
import com.example.ui.components.ChipType
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.Green300
import com.example.ui.theme.Green600
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.Umber400
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.runtime.collectAsState
import com.example.data.network.AppThemeMode
import com.example.data.network.DevMenuManager

@Composable
fun MoreScreen(
  userRole: UserRole,
  onRoleChanged: (UserRole) -> Unit,
  organizations: List<Organization>,
  users: List<User>,
  promotions: List<Promotion>,
  winners: List<PromotionWinner>,
  openAccess: OpenAccessSettings,
  expenses: List<Expense> = emptyList(),
  vouchers: List<Voucher> = emptyList(),
  onDrawPromotion: (String, Int) -> List<PromotionWinner>,
  onUpdateOpenAccess: (OpenAccessSettings) -> Unit,
  onAddExpenseClick: () -> Unit,
  onOpenDevMenu: () -> Unit = {},
  onLogout: () -> Unit = {}
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
  val devConfig by DevMenuManager.config.collectAsState()

  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  // Open Access State
  var isOpenAccessEnabled by remember { mutableStateOf(openAccess.isEnabled) }
  var welcomeMsg by remember { mutableStateOf(openAccess.welcomeMessage ?: "") }

  // Promotion Draw Animation State
  var isDrawingWinner by remember { mutableStateOf(false) }
  var currentDrawingName by remember { mutableStateOf("Ready to draw...") }
  var latestWinner by remember { mutableStateOf<PromotionWinner?>(null) }
  var secretTapCount by remember { mutableIntStateOf(0) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    Text(
      text = "Org & System Setup",
      style = MaterialTheme.typography.headlineLarge,
      color = if (isDark) Color.White else Color.Black,
      fontWeight = FontWeight.Bold
    )

    // Console Control Card
    GlassCard(
      modifier = Modifier.fillMaxWidth(),
      cornerRadius = 16.dp,
      contentPadding = 16.dp
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Super Admin Console", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
            Text("Hotspot Fleet Management System", fontSize = 12.sp, color = Umber400)
          }

          StatusChip(
            text = "Super Admin",
            type = ChipType.Success
          )
        }

        CompactButton(
          text = "Sign Out & Revoke JWT",
          onClick = onLogout,
          icon = Icons.AutoMirrored.Filled.ExitToApp,
          modifier = Modifier.fillMaxWidth(),
          style = CompactButtonStyle.Outlined
        )
      }
    }

    // App Theme Appearance Controls
    GlassCard(
      modifier = Modifier.fillMaxWidth(),
      cornerRadius = 16.dp,
      contentPadding = 16.dp
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Palette, contentDescription = null, tint = if (isDark) Ember300 else Ember600, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Theme Appearance",
              style = MaterialTheme.typography.titleMedium,
              color = if (isDark) Color.White else Color.Black,
              fontWeight = FontWeight.Bold
            )
          }

          StatusChip(
            text = devConfig.themeMode.name,
            type = ChipType.Neutral
          )
        }

        Text(
          text = "Choose light or dark visual theme for the field console:",
          fontSize = 12.sp,
          color = Umber400
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          CompactButton(
            text = "Dark Mode",
            onClick = { DevMenuManager.setThemeMode(AppThemeMode.DARK) },
            style = if (devConfig.themeMode == AppThemeMode.DARK) CompactButtonStyle.Primary else CompactButtonStyle.Outlined,
            modifier = Modifier.weight(1f)
          )

          CompactButton(
            text = "Light Mode",
            onClick = { DevMenuManager.setThemeMode(AppThemeMode.LIGHT) },
            style = if (devConfig.themeMode == AppThemeMode.LIGHT) CompactButtonStyle.Primary else CompactButtonStyle.Outlined,
            modifier = Modifier.weight(1f)
          )

          CompactButton(
            text = "System",
            onClick = { DevMenuManager.setThemeMode(AppThemeMode.SYSTEM) },
            style = if (devConfig.themeMode == AppThemeMode.SYSTEM) CompactButtonStyle.Primary else CompactButtonStyle.Outlined,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Open Access Settings (Hotspot Welcome & Schedules)
    GlassCard(
      modifier = Modifier.fillMaxWidth(),
      cornerRadius = 16.dp,
      contentPadding = 16.dp
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Public, contentDescription = null, tint = if (isDark) Ember300 else Ember600, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("Open Access Wi-Fi Mode", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
              Text("Free public access schedule", fontSize = 12.sp, color = Umber400)
            }
          }

          Switch(
            checked = isOpenAccessEnabled,
            onCheckedChange = {
              isOpenAccessEnabled = it
              onUpdateOpenAccess(openAccess.copy(isEnabled = it))
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = if (isDark) Ember300 else Ember600
            )
          )
        }

        if (isOpenAccessEnabled) {
          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = welcomeMsg,
            onValueChange = { welcomeMsg = it },
            label = { Text("Welcome Portal Message") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = if (isDark) Ember300 else Ember600,
              unfocusedBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Schedule: 08:00 - 18:00", fontSize = 12.sp, color = Umber400)
            Text("Speed Cap: 2.0 Mbps", fontSize = 12.sp, color = Umber400)
          }
        }
      }
    }

    // Promotions & Live Raffle Drawing
    GlassCard(
      modifier = Modifier.fillMaxWidth(),
      cornerRadius = 16.dp,
      contentPadding = 16.dp
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Casino, contentDescription = null, tint = if (isDark) Ember300 else Ember600, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Promotions & Live Raffle", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        promotions.firstOrNull()?.let { prom ->
          Text(prom.name, style = MaterialTheme.typography.bodyLarge, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
          Text("Prize: ${prom.prize}", fontSize = 12.sp, color = if (isDark) Ember300 else Ember600)
          Text(prom.description ?: "", fontSize = 12.sp, color = Umber400)

          Spacer(modifier = Modifier.height(14.dp))

          // Draw Winner Action Button
          CompactButton(
            text = if (isDrawingWinner) "Selecting Winner..." else "Draw Raffle Winner Now",
            onClick = {
              scope.launch {
                isDrawingWinner = true
                val candidates = listOf("Tinashe Shumba", "Blessing Mutasa", "Rudo Mapfumo", "Simbarashe Moyo", "Natsai Gumbo")
                for (i in 1..12) {
                  currentDrawingName = candidates.random()
                  delay(100)
                }
                val drawnWinners = onDrawPromotion(prom.id, 1)
                latestWinner = drawnWinners.firstOrNull()
                isDrawingWinner = false
              }
            },
            icon = Icons.Default.Casino,
            style = CompactButtonStyle.Primary,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isDrawingWinner
          )

          if (isDrawingWinner) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Shuffling entries: $currentDrawingName", fontSize = 13.sp, color = if (isDark) Ember300 else Ember600, fontWeight = FontWeight.Bold)
          }

          latestWinner?.let { winner ->
            Spacer(modifier = Modifier.height(12.dp))
            GlassCard(
              modifier = Modifier.fillMaxWidth(),
              cornerRadius = 12.dp,
              contentPadding = 12.dp
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green300, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text("Winner Drawn: ${winner.name}", style = MaterialTheme.typography.bodyLarge, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                  Text("Contact: ${winner.phone ?: winner.email ?: "Qualified Customer"}", fontSize = 12.sp, color = Umber400)
                }
              }
            }
          }
        }
      }
    }

    // Financial Reports & Expense Modal Trigger
    GlassCard(
      modifier = Modifier.fillMaxWidth(),
      cornerRadius = 16.dp,
      contentPadding = 16.dp
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = if (isDark) Ember300 else Ember600, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Financial Reports & Expenses", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
          }

          CompactButton(
            text = "+ Expense",
            onClick = onAddExpenseClick,
            icon = Icons.Default.Add,
            style = CompactButtonStyle.Secondary
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val grossRev = vouchers.size * 2.0
        val siteExp = expenses.sumOf { it.amount }
        val netProf = grossRev - siteExp

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Gross Revenue", fontSize = 12.sp, color = Umber400)
            Text(if (grossRev == 0.0) "$0" else "$${grossRev.toInt()}", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
          }
          Column {
            Text("Site Expenses", fontSize = 12.sp, color = Umber400)
            Text(if (siteExp == 0.0) "$0" else "-$${siteExp.toInt()}", style = MaterialTheme.typography.titleMedium, color = if (siteExp > 0) Color.Red else (if (isDark) Color.White else Color.Black), fontWeight = FontWeight.Bold)
          }
          Column {
            Text("Net Profit", fontSize = 12.sp, color = Umber400)
            Text(if (netProf == 0.0) "$0" else "$${netProf.toInt()}", style = MaterialTheme.typography.titleMedium, color = if (netProf > 0) (if (isDark) Green300 else Green600) else if (netProf < 0) Color.Red else (if (isDark) Color.White else Color.Black), fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Offline Field Sync Status
    GlassCard(
      modifier = Modifier.fillMaxWidth(),
      cornerRadius = 16.dp,
      contentPadding = 16.dp
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Sync, contentDescription = null, tint = if (isDark) Ember300 else Ember600, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Offline Field Sync Queue", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
            Text("All actions synced to cloud", fontSize = 12.sp, color = Umber400)
          }
        }

        CompactButton(
          text = "Sync Now",
          onClick = { Toast.makeText(context, "Field database up to date!", Toast.LENGTH_SHORT).show() },
          style = CompactButtonStyle.Outlined
        )
      }
    }

    // Version Secret Tap Footer
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "XSpot Super Admin v1.0.0 (Build 2026)",
        fontSize = 11.sp,
        color = Umber400,
        modifier = Modifier.clickable {
          secretTapCount++
          if (secretTapCount >= 5) {
            secretTapCount = 0
            onOpenDevMenu()
          }
        }
      )
    }
  }
}
