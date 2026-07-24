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
import com.example.data.model.Location
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.Umber400

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
  locations: List<Location>,
  onDismiss: () -> Unit,
  onSave: (locationId: String, category: String, amount: Double, desc: String?) -> Unit
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  var selectedLocationId by remember { mutableStateOf(locations.firstOrNull()?.id ?: "") }
  var category by remember { mutableStateOf("rent") } // rent, electricity, internet, other
  var amount by remember { mutableStateOf("100") }
  var description by remember { mutableStateOf("") }

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
        text = "Record Site Expense",
        style = MaterialTheme.typography.titleLarge,
        color = if (isDark) Color.White else Color.Black,
        fontWeight = FontWeight.Bold
      )

      Text("Category", fontSize = 12.sp, color = Umber400)
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val categories = listOf("rent", "electricity", "internet", "other")
        categories.forEach { cat ->
          CompactButton(
            text = cat.replaceFirstChar { it.uppercase() },
            onClick = { category = cat },
            style = if (category == cat) CompactButtonStyle.Primary else CompactButtonStyle.Outlined
          )
        }
      }

      OutlinedTextField(
        value = amount,
        onValueChange = { amount = it },
        label = { Text("Expense Amount ($)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )

      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description / Receipt Memo") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        CompactButton(text = "Cancel", onClick = onDismiss, style = CompactButtonStyle.Outlined)
        Spacer(modifier = Modifier.width(10.dp))
        CompactButton(
          text = "Save Expense",
          onClick = {
            val amtNum = amount.toDoubleOrNull() ?: 0.0
            if (amtNum > 0) {
              onSave(selectedLocationId, category, amtNum, description.ifBlank { null })
              onDismiss()
            }
          },
          style = CompactButtonStyle.Primary
        )
      }
    }
  }
}
