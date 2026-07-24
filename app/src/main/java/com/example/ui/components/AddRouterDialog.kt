package com.example.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.TestConnectionResult
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.Green300
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.Umber400

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRouterDialog(
  locations: List<Location>,
  onDismiss: () -> Unit,
  onTestConnection: (ip: String, port: Int, user: String, pass: String) -> TestConnectionResult,
  onSave: (locationId: String, name: String, ip: String, port: Int, user: String) -> Unit
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  var step by remember { mutableIntStateOf(1) } // 1: Credentials & Test, 2: Name & Save
  var selectedLocationId by remember { mutableStateOf(locations.firstOrNull()?.id ?: "") }
  var name by remember { mutableStateOf("") }
  var ipAddress by remember { mutableStateOf("192.168.88.1") }
  var apiPort by remember { mutableStateOf("8728") }
  var apiUsername by remember { mutableStateOf("admin") }
  var apiPassword by remember { mutableStateOf("") }

  var testResult by remember { mutableStateOf<TestConnectionResult?>(null) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
  ) {

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text(
        text = if (step == 1) "Step 1: RouterOS Credentials & Live Test" else "Step 2: Assign Router Name & Site",
        style = MaterialTheme.typography.titleLarge,
        color = if (isDark) Color.White else Color.Black,
        fontWeight = FontWeight.Bold
      )

      if (step == 1) {
        OutlinedTextField(
          value = ipAddress,
          onValueChange = { ipAddress = it },
          label = { Text("IP Address / Hostname") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = apiPort,
            onValueChange = { apiPort = it },
            label = { Text("API Port") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )

          OutlinedTextField(
            value = apiUsername,
            onValueChange = { apiUsername = it },
            label = { Text("API Username") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
        }

        OutlinedTextField(
          value = apiPassword,
          onValueChange = { apiPassword = it },
          label = { Text("API Password") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        CompactButton(
          text = "Run RouterOS Test Connection",
          onClick = {
            val portInt = apiPort.toIntOrNull() ?: 8728
            testResult = onTestConnection(ipAddress, portInt, apiUsername, apiPassword)
          },
          icon = Icons.Default.NetworkCheck,
          style = CompactButtonStyle.Secondary,
          modifier = Modifier.fillMaxWidth()
        )

        testResult?.let { res ->
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 12.dp,
            contentPadding = 12.dp
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                if (res.success) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (res.success) Green300 else Color.Red,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(if (res.success) "Identity: ${res.identity}" else "Connection Failed", fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                Text(res.message ?: "", fontSize = 12.sp, color = Umber400)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          CompactButton(text = "Cancel", onClick = onDismiss, style = CompactButtonStyle.Outlined)
          Spacer(modifier = Modifier.width(10.dp))
          CompactButton(text = "Next: Name Router", onClick = { step = 2 }, style = CompactButtonStyle.Primary)
        }
      } else {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Router Friendly Name (e.g. MTK-Borrowdale-3)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        Text("Select Location Site", fontSize = 12.sp, color = Umber400)
        locations.forEach { loc ->
          val isSel = selectedLocationId == loc.id
          GlassCard(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 2.dp),
            cornerRadius = 10.dp,
            onClick = { selectedLocationId = loc.id },
            contentPadding = 10.dp
          ) {
            Text(loc.name, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = if (isDark) Color.White else Color.Black)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          CompactButton(text = "Back", onClick = { step = 1 }, style = CompactButtonStyle.Outlined)
          Spacer(modifier = Modifier.width(10.dp))
          CompactButton(
            text = "Save Router to Fleet",
            onClick = {
              if (name.isNotBlank()) {
                onSave(selectedLocationId, name, ipAddress, apiPort.toIntOrNull() ?: 8728, apiUsername)
                onDismiss()
              }
            },
            style = CompactButtonStyle.Primary
          )
        }
      }
    }
  }
}
