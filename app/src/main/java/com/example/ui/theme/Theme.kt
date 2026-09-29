package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryDark,
  onPrimary = OnPrimaryDark,
  primaryContainer = PrimaryBlueDark,
  secondary = SecondaryDark,
  background = BackgroundDark,
  surface = SurfaceDark,
  surfaceVariant = SurfaceVariantDark,
  onBackground = OnBackgroundDark,
  onSurface = OnSurfaceDark,
  onSurfaceVariant = OnSurfaceVariantDark,
  outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryBlue,
  onPrimary = OnPrimaryLight,
  primaryContainer = SurfaceVariantLight,
  secondary = SecondaryTeal,
  onSecondary = OnSecondaryLight,
  background = BackgroundLight,
  surface = SurfaceLight,
  surfaceVariant = SurfaceVariantLight,
  onBackground = OnBackgroundLight,
  onSurface = OnSurfaceLight,
  onSurfaceVariant = OnSurfaceVariantLight,
  outline = OutlineLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography(),
    content = content
  )
}
