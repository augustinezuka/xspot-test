package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.data.network.AppThemeMode

private val DarkColorScheme = darkColorScheme(
  primary = Ember300,
  onPrimary = Slate950,
  primaryContainer = Ember900,
  onPrimaryContainer = Ember100,
  secondary = Amber300,
  onSecondary = Slate950,
  tertiary = Green300,
  onTertiary = Slate950,
  background = DarkGlassBgStart,
  onBackground = Slate50,
  surface = DarkGlassSurface,
  onSurface = Slate50,
  surfaceVariant = DarkGlassBorder,
  onSurfaceVariant = Slate400,
  outline = DarkGlassBorderHighlight,
  error = Red300,
  onError = Slate950
)

private val LightColorScheme = lightColorScheme(
  primary = Ember600,
  onPrimary = Color.White,
  primaryContainer = Ember100,
  onPrimaryContainer = Ember900,
  secondary = Amber600,
  onSecondary = Color.White,
  tertiary = Green600,
  onTertiary = Color.White,
  background = LightGlassBgStart,
  onBackground = Slate900,
  surface = LightGlassSurface,
  onSurface = Slate900,
  surfaceVariant = LightGlassBorder,
  onSurfaceVariant = Slate600,
  outline = Slate400,
  error = Red600,
  onError = Color.White
)

@Composable
fun XSpotFieldTheme(
  themeMode: AppThemeMode = AppThemeMode.DARK,
  darkTheme: Boolean = when (themeMode) {
    AppThemeMode.LIGHT -> false
    AppThemeMode.DARK -> true
    AppThemeMode.SYSTEM -> isSystemInDarkTheme()
  },
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current

  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      // Set status bar transparent and align icon appearance with dark/light theme
      window.statusBarColor = Color.Transparent.toArgb()
      window.navigationBarColor = Color.Transparent.toArgb()
      val insetsController = WindowCompat.getInsetsController(window, view)
      insetsController.isAppearanceLightStatusBars = !darkTheme
      insetsController.isAppearanceLightNavigationBars = !darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = {
      // Fluid liquid ambient backdrop
      val bgGradient = if (darkTheme) {
        Brush.radialGradient(
          colors = listOf(
            Color(0xFF2E1A11), // Subtle Ember atmospheric liquid glow
            DarkGlassBgStart,
            DarkGlassBgEnd
          ),
          radius = 1800f
        )
      } else {
        Brush.radialGradient(
          colors = listOf(
            Color(0xFFFFEDD5), // Soft liquid warm amber reflection top-center
            Color(0xFFE2E8F0), // Crystal liquid slate background
            Color(0xFFCBD5E1)  // Smooth gradient edge
          ),
          radius = 1600f
        )
      }

      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(bgGradient)
      ) {
        content()
      }
    }
  )
}

