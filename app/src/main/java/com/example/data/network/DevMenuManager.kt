package com.example.data.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppThemeMode {
  SYSTEM,
  LIGHT,
  DARK
}

data class LogEntry(
  val timestamp: String = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date()),
  val level: String, // INFO, WARN, ERROR, HTTP
  val tag: String,
  val message: String
)

data class DevMenuConfig(
  val baseUrl: String = "http://localhost:8080",
  val accessHeaders: Map<String, String> = mapOf("Accept" to "application/json", "Content-Type" to "application/json"),
  val themeMode: AppThemeMode = AppThemeMode.DARK,
  val useDummyData: Boolean = false, // Default false as requested
  val swaggerStatus: String = "Not Tested"
)

object DevMenuManager {

  private val _config = MutableStateFlow(DevMenuConfig())
  val config: StateFlow<DevMenuConfig> = _config.asStateFlow()

  private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
  val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

  init {
    logInfo("DevMenuManager", "Initialized with default Base URL: http://localhost:8080")
  }

  fun updateBaseUrl(newUrl: String) {
    val trimmed = newUrl.trim().trimEnd('/')
    _config.value = _config.value.copy(baseUrl = trimmed)
    logInfo("DevMenu", "Updated API Base URL to: $trimmed")
  }

  fun addHeader(key: String, value: String) {
    if (key.isNotBlank()) {
      val updated = _config.value.accessHeaders.toMutableMap()
      updated[key.trim()] = value.trim()
      _config.value = _config.value.copy(accessHeaders = updated)
      logInfo("DevMenu", "Added header: ${key.trim()} -> ${value.trim()}")
    }
  }

  fun removeHeader(key: String) {
    val updated = _config.value.accessHeaders.toMutableMap()
    updated.remove(key)
    _config.value = _config.value.copy(accessHeaders = updated)
    logInfo("DevMenu", "Removed header: $key")
  }

  fun setThemeMode(mode: AppThemeMode) {
    _config.value = _config.value.copy(themeMode = mode)
    logInfo("DevMenu", "Theme changed to: ${mode.name}")
  }

  fun setUseDummyData(enabled: Boolean) {
    _config.value = _config.value.copy(useDummyData = enabled)
    logInfo("DevMenu", "Use Dummy Data toggled to: $enabled")
  }

  fun updateSwaggerStatus(status: String) {
    _config.value = _config.value.copy(swaggerStatus = status)
  }

  fun logInfo(tag: String, message: String) {
    appendLog("INFO", tag, message)
  }

  fun logWarn(tag: String, message: String) {
    appendLog("WARN", tag, message)
  }

  fun logError(tag: String, message: String) {
    appendLog("ERROR", tag, message)
  }

  fun logHttp(method: String, url: String, statusCode: Int, body: String? = null) {
    appendLog("HTTP", "$method $statusCode", "$url ${body?.let { "-> $it" } ?: ""}")
  }

  private fun appendLog(level: String, tag: String, message: String) {
    val newEntry = LogEntry(level = level, tag = tag, message = message)
    val current = _logs.value.toMutableList()
    current.add(0, newEntry) // Newest first
    if (current.size > 500) {
      current.removeAt(current.size - 1)
    }
    _logs.value = current
  }

  fun clearLogs() {
    _logs.value = emptyList()
    logInfo("DevMenu", "Logs cleared.")
  }

  // Network request executor
  fun executeHttpRequest(
    path: String,
    method: String = "GET",
    jsonBody: String? = null
  ): Pair<Int, String> {
    val fullUrl = if (path.startsWith("http")) path else "${_config.value.baseUrl}${if (path.startsWith("/")) path else "/$path"}"
    val startTime = System.currentTimeMillis()
    logInfo("HTTP_REQ", "$method -> $fullUrl")

    return try {
      val url = URL(fullUrl)
      val conn = url.openConnection() as HttpURLConnection
      conn.requestMethod = method
      conn.connectTimeout = 8000
      conn.readTimeout = 8000

      // Apply Dev Menu Custom Access Headers
      _config.value.accessHeaders.forEach { (k, v) ->
        conn.setRequestProperty(k, v)
      }

      if (jsonBody != null && (method == "POST" || method == "PUT" || method == "PATCH")) {
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")
        OutputStreamWriter(conn.outputStream).use { writer ->
          writer.write(jsonBody)
          writer.flush()
        }
      }

      val code = conn.responseCode
      val inputStream = if (code in 200..299) conn.inputStream else conn.errorStream
      val responseText = inputStream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
      val duration = System.currentTimeMillis() - startTime

      logHttp(method, fullUrl, code, "(${duration}ms) $responseText")
      Pair(code, responseText)
    } catch (e: Exception) {
      val duration = System.currentTimeMillis() - startTime
      logError("HTTP_ERR", "$method $fullUrl failed after ${duration}ms: ${e.localizedMessage}")
      Pair(-1, "Network Error: ${e.message}")
    }
  }
}
