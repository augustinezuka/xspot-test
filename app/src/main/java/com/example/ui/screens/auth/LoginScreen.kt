package com.example.ui.screens.auth

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.CompactButton
import com.example.ui.components.CompactButtonStyle
import com.example.ui.components.GlassCard
import com.example.ui.components.SignalBarGlyph
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.Umber400

@Composable
fun LoginScreen(
  onLogin: (email: String, pass: String) -> Unit,
  onOpenDevMenu: () -> Unit
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

  var email by remember { mutableStateOf("admin@xspot.net") }
  var password by remember { mutableStateOf("") }
  var secretTapCount by remember { mutableIntStateOf(0) }

  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Branding Header
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          Icons.Default.Router,
          contentDescription = "XSpot",
          tint = if (isDark) Ember300 else Ember600,
          modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        SignalBarGlyph(
          bars = 4,
          barWidth = 5.dp,
          maxBarHeight = 22.dp,
          isDarkTheme = isDark
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "XSpot Super Admin",
        style = MaterialTheme.typography.displayLarge,
        color = if (isDark) Color.White else Color.Black,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = "Hotspot Fleet Management Console",
        fontSize = 14.sp,
        color = Umber400
      )

      Spacer(modifier = Modifier.height(32.dp))

      // Glass Login Card
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        contentPadding = 20.dp
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Super Admin Authentication",
            style = MaterialTheme.typography.titleMedium,
            color = if (isDark) Color.White else Color.Black,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Email Field
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Admin Username / Email") },
            leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = Umber400) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = if (isDark) Ember300 else Ember600,
              unfocusedBorderColor = if (isDark) Color(0xFF5C4A3A) else Color(0xFFA68B72)
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Password Field
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Admin Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Umber400) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = if (isDark) Ember300 else Ember600,
              unfocusedBorderColor = if (isDark) Color(0xFF5C4A3A) else Color(0xFFA68B72)
            )
          )

          Spacer(modifier = Modifier.height(24.dp))

          // Sign In Action
          CompactButton(
            text = "Authenticate & Launch",
            onClick = { onLogin(email, password) },
            modifier = Modifier.fillMaxWidth(),
            height = 42.dp,
            style = CompactButtonStyle.Primary
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Version Secret Tap Trigger
          Text(
            text = "v1.0.0 (Build 2026) - Super Admin Console",
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
  }
}
