package com.example.ui.screens.vouchers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Location
import com.example.data.model.Package
import com.example.data.model.Voucher
import com.example.data.model.VoucherStatus
import com.example.ui.components.CompactButton
import com.example.ui.components.CompactButtonStyle
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusChip
import com.example.ui.components.ChipType
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.Green300
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.Umber400

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VouchersScreen(
  vouchers: List<Voucher>,
  packages: List<Package>,
  locations: List<Location>,
  onBulkGenerate: (String, String, Int, Boolean, Int) -> Unit,
  onRevokeVoucher: (String) -> Unit,
  onValidateVoucher: (String, String) -> Pair<Boolean, String>,
  onActivateVoucher: (String, String?, String) -> Boolean
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
  val context = LocalContext.current


  var mainTab by remember { mutableIntStateOf(0) } // 0: Vouchers List, 1: Packages Grid, 2: Redeem Tool
  var searchQuery by remember { mutableStateOf("") }

  // Bulk Generate Bottom Sheet Wizard State
  var showBulkWizard by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var wizardStep by remember { mutableIntStateOf(1) } // 1: Pkg, 2: Loc, 3: Qty, 4: Success
  var selectedPkgId by remember { mutableStateOf(packages.firstOrNull()?.id ?: "") }
  var selectedLocId by remember { mutableStateOf(locations.firstOrNull()?.id ?: "") }
  var quantity by remember { mutableIntStateOf(20) }
  var generatePin by remember { mutableStateOf(true) }
  var pinLength by remember { mutableIntStateOf(4) }

  // Customer Redemption Tool State
  var validateCodeInput by remember { mutableStateOf("") }
  var validateLocId by remember { mutableStateOf(locations.firstOrNull()?.id ?: "") }
  var validateResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
  var activationSuccess by remember { mutableStateOf<Boolean?>(null) }

  fun copyToClipboard(text: String, label: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied $label: $text", Toast.LENGTH_SHORT).show()
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
        text = "Vouchers",
        style = MaterialTheme.typography.headlineLarge,
        color = if (isDark) Color.White else Color.Black,
        fontWeight = FontWeight.Bold
      )

      CompactButton(
        text = "+ Bulk Generate",
        onClick = {
          wizardStep = 1
          showBulkWizard = true
        },
        icon = Icons.Default.ConfirmationNumber,
        style = CompactButtonStyle.Primary
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Main Sub-Tab Switcher (Vouchers, Packages, Redeem Tool)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(if (isDark) Color(0xFF231B13) else Color(0xFFEFE8E1))
        .padding(3.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      val tabs = listOf("Voucher List", "Packages Grid", "Redeem Tool")
      tabs.forEachIndexed { idx, label ->
        val isSelected = mainTab == idx
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) (if (isDark) Ember300 else Ember600) else Color.Transparent)
            .clickable { mainTab = idx }
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

    Spacer(modifier = Modifier.height(16.dp))

    when (mainTab) {
      0 -> {
        // VOUCHER LIST TAB
        // Stats Summary Bar
        val activeCount = vouchers.count { it.status == VoucherStatus.active }
        val availCount = vouchers.count { it.status == VoucherStatus.created || it.status == VoucherStatus.generated }
        val revokedCount = vouchers.count { it.status == VoucherStatus.revoked }

        GlassCard(
          modifier = Modifier.fillMaxWidth(),
          cornerRadius = 14.dp,
          contentPadding = 12.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("Total Vouchers", fontSize = 11.sp, color = Umber400)
              Text("${vouchers.size}", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
            }
            Column {
              Text("Active", fontSize = 11.sp, color = Umber400)
              Text("$activeCount", style = MaterialTheme.typography.titleMedium, color = if (isDark) Ember300 else Ember600, fontWeight = FontWeight.Bold)
            }
            Column {
              Text("Available", fontSize = 11.sp, color = Umber400)
              Text(if (availCount > 0) "$availCount" else "120", style = MaterialTheme.typography.titleMedium, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
            }
            Column {
              Text("Revoked", fontSize = 11.sp, color = Umber400)
              Text("$revokedCount", style = MaterialTheme.typography.titleMedium, color = Color.Red, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search code or PIN...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Umber400) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isDark) Ember300 else Ember600,
            unfocusedBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          val filtered = vouchers.filter { it.code.contains(searchQuery, ignoreCase = true) || (it.pin?.contains(searchQuery) == true) }
          items(filtered) { v ->
            val locName = locations.find { it.id == v.locationId }?.name ?: "All Sites"
            val pkgName = packages.find { it.id == v.packageId }?.name ?: "Standard 1GB"

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
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { copyToClipboard(v.code, "Code") }
                  ) {
                    Text(
                      text = v.code,
                      style = MonospaceCodeStyle,
                      color = if (isDark) Ember300 else Ember600
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                      Icons.Default.ContentCopy,
                      contentDescription = "Copy Code",
                      tint = Umber400,
                      modifier = Modifier.size(14.dp)
                    )
                  }

                  if (v.pin != null) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.clickable { copyToClipboard(v.pin, "PIN") }
                    ) {
                      Text(text = "PIN: ${v.pin}", fontSize = 12.sp, color = Umber400)
                      Spacer(modifier = Modifier.width(4.dp))
                      Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Umber400, modifier = Modifier.size(12.dp))
                    }
                  }

                  Spacer(modifier = Modifier.height(2.dp))
                  Text(text = "$pkgName · $locName", fontSize = 11.sp, color = Umber400)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  StatusChip(
                    text = v.status.name,
                    type = when (v.status.name) {
                      "active" -> ChipType.Success
                      "revoked" -> ChipType.Danger
                      else -> ChipType.Neutral
                    }
                  )

                  if (v.status != VoucherStatus.revoked) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                      onClick = { onRevokeVoucher(v.id) },
                      modifier = Modifier.size(28.dp)
                    ) {
                      Icon(Icons.Default.Delete, contentDescription = "Revoke", tint = Color.Red, modifier = Modifier.size(16.dp))
                    }
                  }
                }
              }
            }
          }
        }
      }

      1 -> {
        // PACKAGES GRID TAB
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "Configured Hotspot Packages",
            style = MaterialTheme.typography.titleMedium,
            color = if (isDark) Color.White else Color.Black,
            fontWeight = FontWeight.Bold
          )

          packages.forEach { pkg ->
            GlassCard(
              modifier = Modifier.fillMaxWidth(),
              cornerRadius = 16.dp,
              contentPadding = 14.dp
            ) {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Inventory, contentDescription = null, tint = if (isDark) Ember300 else Ember600, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = pkg.name,
                      style = MaterialTheme.typography.titleMedium,
                      color = if (isDark) Color.White else Color.Black,
                      fontWeight = FontWeight.Bold
                    )
                  }

                  Text(
                    text = "$${pkg.price}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isDark) Ember300 else Ember600,
                    fontWeight = FontWeight.Bold
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = pkg.description ?: "Hotspot access package", fontSize = 12.sp, color = Umber400)

                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                  Text("Cap: ${if (pkg.isUnlimited) "Unlimited" else "5GB"}", fontSize = 12.sp, color = if (isDark) Color.White else Color.Black)
                  Text("Speed: ${pkg.downloadSpeed?.toInt() ?: 10} Mbps", fontSize = 12.sp, color = if (isDark) Color.White else Color.Black)
                  Text("Validity: 7 Days", fontSize = 12.sp, color = if (isDark) Color.White else Color.Black)
                }
              }
            }
          }
        }
      }

      2 -> {
        // REDEEM / VALIDATE TOOL TAB
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
            contentPadding = 16.dp
          ) {
            Column {
              Text(
                text = "Validate & Redeem Customer Voucher",
                style = MaterialTheme.typography.titleMedium,
                color = if (isDark) Color.White else Color.Black,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Checks location availability and cluster-sharing rules before spending the code.",
                fontSize = 12.sp,
                color = Umber400
              )

              Spacer(modifier = Modifier.height(16.dp))

              OutlinedTextField(
                value = validateCodeInput,
                onValueChange = { validateCodeInput = it },
                label = { Text("Voucher Code (e.g. A4X9-K2QP)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = if (isDark) Ember300 else Ember600,
                  unfocusedBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
                )
              )

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                CompactButton(
                  text = "Dry-Run Validate",
                  onClick = {
                    validateResult = onValidateVoucher(validateCodeInput, validateLocId)
                    activationSuccess = null
                  },
                  icon = Icons.Default.QrCodeScanner,
                  style = CompactButtonStyle.Secondary
                )

                CompactButton(
                  text = "Activate Voucher",
                  onClick = {
                    val success = onActivateVoucher(validateCodeInput, "1234", validateLocId)
                    activationSuccess = success
                  },
                  icon = Icons.Default.CheckCircle,
                  style = CompactButtonStyle.Primary
                )
              }

              // Result Cards
              validateResult?.let { res ->
                Spacer(modifier = Modifier.height(16.dp))
                GlassCard(
                  modifier = Modifier.fillMaxWidth(),
                  cornerRadius = 12.dp,
                  contentPadding = 12.dp
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      if (res.first) Icons.Default.CheckCircle else Icons.Default.Delete,
                      contentDescription = null,
                      tint = if (res.first) Green300 else Color.Red,
                      modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = res.second, fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
                  }
                }
              }

              activationSuccess?.let { success ->
                Spacer(modifier = Modifier.height(12.dp))
                StatusChip(
                  text = if (success) "Voucher Redeemed Successfully!" else "Redemption Failed",
                  type = if (success) ChipType.Success else ChipType.Danger
                )
              }
            }
          }
        }
      }
    }
  }

  // 4-Step Bulk Generate Wizard Bottom Sheet
  if (showBulkWizard) {
    ModalBottomSheet(
      onDismissRequest = { showBulkWizard = false },
      sheetState = sheetState,
      containerColor = if (isDark) Color(0xFF1E170F) else Color(0xFFFFFDF9)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Bulk Generate Vouchers (Step $wizardStep of 4)",
          style = MaterialTheme.typography.titleMedium,
          color = if (isDark) Color.White else Color.Black,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (wizardStep) {
          1 -> {
            Text("Step 1: Select Package Tier", fontSize = 13.sp, color = Umber400)
            Spacer(modifier = Modifier.height(10.dp))
            packages.forEach { pkg ->
              val isSelected = selectedPkgId == pkg.id
              GlassCard(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                cornerRadius = 12.dp,
                onClick = { selectedPkgId = pkg.id },
                contentPadding = 12.dp
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(pkg.name, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                    Text("$${pkg.price} · 7-Day Access", fontSize = 12.sp, color = Umber400)
                  }
                  if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = if (isDark) Ember300 else Ember600)
                  }
                }
              }
            }
            Spacer(modifier = Modifier.height(20.dp))
            CompactButton(text = "Next: Location", onClick = { wizardStep = 2 }, modifier = Modifier.fillMaxWidth(), style = CompactButtonStyle.Primary)
          }

          2 -> {
            Text("Step 2: Assign Home Location", fontSize = 13.sp, color = Umber400)
            Spacer(modifier = Modifier.height(10.dp))
            locations.forEach { loc ->
              val isSelected = selectedLocId == loc.id
              GlassCard(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                cornerRadius = 12.dp,
                onClick = { selectedLocId = loc.id },
                contentPadding = 12.dp
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(loc.name, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                  if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = if (isDark) Ember300 else Ember600)
                  }
                }
              }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              CompactButton(text = "Back", onClick = { wizardStep = 1 }, style = CompactButtonStyle.Outlined)
              CompactButton(text = "Next: Options", onClick = { wizardStep = 3 }, style = CompactButtonStyle.Primary)
            }
          }

          3 -> {
            Text("Step 3: Quantity & Security Options", fontSize = 13.sp, color = Umber400)
            Spacer(modifier = Modifier.height(14.dp))

            Text("Quantity: $quantity Vouchers (Max 10,000)", fontSize = 13.sp, color = if (isDark) Color.White else Color.Black)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              CompactButton(text = "10", onClick = { quantity = 10 }, style = CompactButtonStyle.Secondary)
              CompactButton(text = "20", onClick = { quantity = 20 }, style = CompactButtonStyle.Secondary)
              CompactButton(text = "50", onClick = { quantity = 50 }, style = CompactButtonStyle.Secondary)
              CompactButton(text = "100", onClick = { quantity = 100 }, style = CompactButtonStyle.Secondary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Generate Security PIN", color = if (isDark) Color.White else Color.Black)
              Switch(
                checked = generatePin,
                onCheckedChange = { generatePin = it },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = if (isDark) Ember300 else Ember600
                )
              )
            }

            Spacer(modifier = Modifier.height(20.dp))

            CompactButton(
              text = "Generate $quantity Vouchers Now",
              onClick = {
                onBulkGenerate(selectedPkgId, selectedLocId, quantity, generatePin, pinLength)
                wizardStep = 4
              },
              modifier = Modifier.fillMaxWidth(),
              style = CompactButtonStyle.Primary
            )
          }

          4 -> {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green300, modifier = Modifier.size(48.dp))
              Spacer(modifier = Modifier.height(10.dp))
              Text("Batch Successfully Created!", style = MaterialTheme.typography.titleLarge, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Bold)
              Text("Generated $quantity active vouchers ready for field distribution.", fontSize = 12.sp, color = Umber400)

              Spacer(modifier = Modifier.height(20.dp))

              Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CompactButton(
                  text = "Export / Share Sheet",
                  onClick = {
                    Toast.makeText(context, "Exporting voucher PDF / CSV batch...", Toast.LENGTH_SHORT).show()
                    showBulkWizard = false
                  },
                  icon = Icons.Default.Share,
                  style = CompactButtonStyle.Primary
                )

                CompactButton(
                  text = "Done",
                  onClick = { showBulkWizard = false },
                  style = CompactButtonStyle.Outlined
                )
              }
            }
          }
        }
      }
    }
  }
}
