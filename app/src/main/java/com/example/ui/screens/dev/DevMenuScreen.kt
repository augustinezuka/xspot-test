package com.example.ui.screens.dev

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import com.example.data.network.AppThemeMode
import com.example.ui.components.ChipType
import com.example.ui.components.StatusChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.network.DevMenuManager
import com.example.ui.components.CompactButton
import com.example.ui.components.CompactButtonStyle
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.DarkGlassSurface
import com.example.ui.theme.Ember300
import com.example.ui.theme.Ember600
import com.example.ui.theme.Green300
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.LightGlassSurface
import com.example.ui.theme.Umber400
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevMenuScreen(
  onBackClick: () -> Unit
) {
  val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
  val context = LocalContext.current

  val scope = rememberCoroutineScope()

  val config by DevMenuManager.config.collectAsState()
  val logs by DevMenuManager.logs.collectAsState()

  var urlInput by remember { mutableStateOf(config.baseUrl) }
  var headerKey by remember { mutableStateOf("") }
  var headerVal by remember { mutableStateOf("") }
  var isTestingConnection by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Terminal,
              contentDescription = null,
              tint = if (isDark) Ember300 else Ember600,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Dev Menu",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
              )
              Text(
                text = "API Base URL Changer & System Logs",
                fontSize = 11.sp,
                color = Umber400
              )
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = if (isDark) Color.White else Color.Black
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = if (isDark) DarkGlassSurface else LightGlassSurface
        )
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Theme Appearance Controls
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14.dp,
        contentPadding = 14.dp
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Palette,
                contentDescription = null,
                tint = if (isDark) Ember300 else Ember600,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Theme Appearance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
              )
            }

            Text(
              text = config.themeMode.name,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Umber400
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CompactButton(
              text = "Dark Mode",
              onClick = { DevMenuManager.setThemeMode(AppThemeMode.DARK) },
              style = if (config.themeMode == AppThemeMode.DARK) CompactButtonStyle.Primary else CompactButtonStyle.Outlined,
              modifier = Modifier.weight(1f)
            )

            CompactButton(
              text = "Light Mode",
              onClick = { DevMenuManager.setThemeMode(AppThemeMode.LIGHT) },
              style = if (config.themeMode == AppThemeMode.LIGHT) CompactButtonStyle.Primary else CompactButtonStyle.Outlined,
              modifier = Modifier.weight(1f)
            )

            CompactButton(
              text = "System",
              onClick = { DevMenuManager.setThemeMode(AppThemeMode.SYSTEM) },
              style = if (config.themeMode == AppThemeMode.SYSTEM) CompactButtonStyle.Primary else CompactButtonStyle.Outlined,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // Section 1: API URL Changer & Access Headers
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14.dp,
        contentPadding = 16.dp
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.NetworkCheck,
                contentDescription = null,
                tint = if (isDark) Ember300 else Ember600,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "API Base URL Changer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
              )
            }

            Text(
              text = config.swaggerStatus,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (config.swaggerStatus.contains("Connected")) Green300 else Ember600
            )
          }

          DevMenuTextField(
            value = urlInput,
            onValueChange = {
              urlInput = it
              DevMenuManager.updateBaseUrl(it)
            },
            label = "Base URL (Default: http://localhost:8080)",
            modifier = Modifier.fillMaxWidth(),
            isDark = isDark
          )

          // Presets & Test Connection Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf("http://localhost:8080", "http://10.0.2.2:8080").forEach { preset ->
                CompactButton(
                  text = preset.replace("http://", ""),
                  onClick = {
                    urlInput = preset
                    DevMenuManager.updateBaseUrl(preset)
                  },
                  style = if (config.baseUrl == preset) CompactButtonStyle.Primary else CompactButtonStyle.Outlined
                )
              }
            }

            CompactButton(
              text = if (isTestingConnection) "Pinging..." else "Ping Health",
              onClick = {
                isTestingConnection = true
                scope.launch(Dispatchers.IO) {
                  val res = DevMenuManager.executeHttpRequest("/health", "GET")
                  val res2 = if (res.first != 200) DevMenuManager.executeHttpRequest("/", "GET") else res
                  withContext(Dispatchers.Main) {
                    isTestingConnection = false
                    val statusMsg = if (res2.first in 200..299) "Connected (HTTP ${res2.first})" else "Failed (HTTP ${res2.first})"
                    DevMenuManager.updateSwaggerStatus(statusMsg)
                    Toast.makeText(context, "API Ping Result: $statusMsg", Toast.LENGTH_SHORT).show()
                  }
                }
              },
              icon = Icons.Default.Refresh,
              style = CompactButtonStyle.Secondary
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Custom Access Headers (Key-Value Pairs)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Umber400
          )

          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
          ) {
            DevMenuTextField(
              value = headerKey,
              onValueChange = { headerKey = it },
              label = "Header Key",
              modifier = Modifier.weight(1f),
              isDark = isDark
            )
            DevMenuTextField(
              value = headerVal,
              onValueChange = { headerVal = it },
              label = "Value",
              modifier = Modifier.weight(1f),
              isDark = isDark
            )
            IconButton(
              onClick = {
                if (headerKey.isNotBlank()) {
                  DevMenuManager.addHeader(headerKey, headerVal)
                  headerKey = ""
                  headerVal = ""
                }
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = "Add Header", tint = if (isDark) Ember300 else Ember600)
            }
          }

          config.accessHeaders.forEach { (k, v) ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  if (isDark) Color(0xFF2A2016) else Color(0xFFF2ECE4),
                  shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "$k: $v",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = if (isDark) Color.White else Color.Black
              )
              IconButton(
                onClick = { DevMenuManager.removeHeader(k) },
                modifier = Modifier.size(20.dp)
              ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }

      // Section 1.5: Swagger & JWT Auth Specs
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14.dp,
        contentPadding = 16.dp
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Security,
                contentDescription = null,
                tint = if (isDark) Ember300 else Ember600,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Swagger & OpenAPI JWT Auth Specs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
              )
            }

            StatusChip(
              text = if (config.accessHeaders.containsKey("Authorization")) "JWT Active" else "No JWT",
              type = if (config.accessHeaders.containsKey("Authorization")) ChipType.Success else ChipType.Warning
            )
          }

          Text(
            text = "Swagger UI: ${config.baseUrl}/swagger-ui/index.html\nOpenAPI Docs: ${config.baseUrl}/v3/api-docs",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = Umber400
          )

          Text(
            text = "• Public Unauthenticated Routes (No JWT): POST /api/v1/auth/login, GET /dev_menu, GET /health, GET /v3/api-docs\n• Protected Routes (JWT Bearer Required): All /api/v1/* resources (locations, routers, vouchers, expenses, promotions)",
            fontSize = 11.sp,
            color = if (isDark) Color(0xFFD4C5B9) else Color(0xFF4A3E36)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CompactButton(
              text = "Test OpenAPI Docs",
              onClick = {
                scope.launch(Dispatchers.IO) {
                  val res = DevMenuManager.executeHttpRequest("/v3/api-docs", "GET")
                  withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Swagger Docs Status: HTTP ${res.first}", Toast.LENGTH_SHORT).show()
                  }
                }
              },
              style = CompactButtonStyle.Outlined,
              modifier = Modifier.weight(1f)
            )

            CompactButton(
              text = "Inject Admin JWT",
              onClick = {
                val dummyJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkB4c3BvdC5uZXQiLCJyb2xlIjoic3VwZXJfYWRtaW4iLCJpYXQiOjE3NTM3MDA0MDB9.xspot_admin_jwt_sig"
                DevMenuManager.addHeader("Authorization", "Bearer $dummyJwt")
                Toast.makeText(context, "JWT Authorization Header Injected", Toast.LENGTH_SHORT).show()
              },
              style = CompactButtonStyle.Primary,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // Section 2: App Logs Viewer
      GlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        cornerRadius = 14.dp,
        contentPadding = 16.dp
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.BugReport,
                contentDescription = null,
                tint = if (isDark) Ember300 else Ember600,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "App & HTTP Logs (${logs.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
              )
            }

            Row {
              IconButton(
                onClick = {
                  val allLogsStr = logs.joinToString("\n") { "[${it.timestamp}] [${it.level}] ${it.tag}: ${it.message}" }
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("XSpot Dev Logs", allLogsStr)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()
                }
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = if (isDark) Ember300 else Ember600, modifier = Modifier.size(20.dp))
              }

              IconButton(onClick = { DevMenuManager.clearLogs() }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Red, modifier = Modifier.size(20.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
              .background(Color(0xFF0D0A07), shape = RoundedCornerShape(8.dp))
              .border(1.dp, if (isDark) DarkGlassBorder else LightGlassBorder, shape = RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            if (logs.isEmpty()) {
              Text(
                text = "No log output recorded yet.",
                fontSize = 12.sp,
                color = Umber400,
                fontFamily = FontFamily.Monospace
              )
            } else {
              LazyColumn {
                items(logs) { log ->
                  val color = when (log.level) {
                    "ERROR" -> Color(0xFFFF5555)
                    "HTTP" -> Color(0xFF50FA7B)
                    "WARN" -> Color(0xFFFFB86C)
                    else -> Color(0xFF8BE9FD)
                  }
                  Text(
                    text = "[${log.timestamp}] [${log.level}] ${log.tag}: ${log.message}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = color,
                    modifier = Modifier.padding(vertical = 2.dp)
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

@Composable
private fun DevMenuTextField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  isDark: Boolean = false
) {
  Column(modifier = modifier) {
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = FontWeight.SemiBold,
      color = Umber400,
      modifier = Modifier.padding(bottom = 2.dp)
    )
    BasicTextField(
      value = value,
      onValueChange = onValueChange,
      singleLine = true,
      textStyle = TextStyle(
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        color = if (isDark) Color.White else Color.Black
      ),
      decorationBox = { innerTextField ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
              shape = RoundedCornerShape(6.dp)
            )
            .border(
              1.dp,
              if (isDark) DarkGlassBorder else LightGlassBorder,
              shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          innerTextField()
        }
      }
    )
  }
}
