package com.example.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cluster
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.Umber400

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationDialog(
  clusters: List<Cluster>,
  onDismiss: () -> Unit,
  onSave: (name: String, address: String?, rent: Double, electricity: Double, internet: Double, clusterId: String?) -> Unit
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  var name by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var rent by remember { mutableStateOf("300") }
  var electricity by remember { mutableStateOf("100") }
  var internet by remember { mutableStateOf("250") }
  var selectedClusterId by remember { mutableStateOf<String?>(clusters.firstOrNull()?.id) }

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
        text = "Add New Hotspot Location",
        style = MaterialTheme.typography.titleLarge,
        color = if (isDark) Color.White else Color.Black,
        fontWeight = FontWeight.Bold
      )

      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Location Name (e.g. Sam Nujoma Plaza)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = if (isDark) Ember300 else Ember600,
          unfocusedBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
        )
      )

      OutlinedTextField(
        value = address,
        onValueChange = { address = it },
        label = { Text("Address / Physical Site Description") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = if (isDark) Ember300 else Ember600,
          unfocusedBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
        )
      )

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = rent,
          onValueChange = { rent = it },
          label = { Text("Rent ($)") },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )
        OutlinedTextField(
          value = electricity,
          onValueChange = { electricity = it },
          label = { Text("Power ($)") },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )
        OutlinedTextField(
          value = internet,
          onValueChange = { internet = it },
          label = { Text("Fiber ($)") },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )
      }

      Text("Cluster Assignment", fontSize = 12.sp, color = Umber400)
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        clusters.forEach { cl ->
          val isSel = selectedClusterId == cl.id
          CompactButton(
            text = cl.name.take(12) + "...",
            onClick = { selectedClusterId = cl.id },
            style = if (isSel) CompactButtonStyle.Primary else CompactButtonStyle.Outlined
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        CompactButton(text = "Cancel", onClick = onDismiss, style = CompactButtonStyle.Outlined)
        Spacer(modifier = Modifier.width(10.dp))
        CompactButton(
          text = "Save Location",
          onClick = {
            if (name.isNotBlank()) {
              onSave(
                name,
                address.ifBlank { null },
                rent.toDoubleOrNull() ?: 0.0,
                electricity.toDoubleOrNull() ?: 0.0,
                internet.toDoubleOrNull() ?: 0.0,
                selectedClusterId
              )
              onDismiss()
            }
          },
          style = CompactButtonStyle.Primary
        )
      }
    }
  }
}
