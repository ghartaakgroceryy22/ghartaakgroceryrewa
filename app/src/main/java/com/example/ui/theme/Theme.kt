package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = GroceryGreenPrimary,
  onPrimary = Color.White,
  primaryContainer = GroceryGreenContainer,
  onPrimaryContainer = GroceryGreenDark,
  secondary = GrocerySaffronSecondary,
  onSecondary = Color.White,
  secondaryContainer = GroceryAmberContainer,
  onSecondaryContainer = Color(0xFF78350F),
  tertiary = GroceryDiscountRed,
  onTertiary = Color.White,
  tertiaryContainer = GroceryDiscountPink,
  onTertiaryContainer = Color(0xFF991B1B),
  background = GroceryBackground,
  onBackground = GroceryTextPrimary,
  surface = GrocerySurface,
  onSurface = GroceryTextPrimary,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = GroceryTextSecondary,
  outline = GroceryCardBorder,
  error = GroceryDiscountRed,
  onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
  primary = GroceryDarkPrimary,
  onPrimary = Color(0xFF022C1A),
  primaryContainer = GroceryDarkPrimaryContainer,
  onPrimaryContainer = Color(0xFFD1FAE5),
  secondary = GroceryAmber,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFF78350F),
  onSecondaryContainer = GroceryAmberContainer,
  tertiary = Color(0xFFF87171),
  onTertiary = Color.Black,
  background = GroceryDarkBackground,
  onBackground = GroceryDarkText,
  surface = GroceryDarkSurface,
  onSurface = GroceryDarkText,
  surfaceVariant = Color(0xFF1E293B),
  onSurfaceVariant = GroceryDarkTextSecondary,
  outline = Color(0xFF334155),
  error = Color(0xFFEF4444),
  onError = Color.Black
)

@Composable
fun GharTakGroceryTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
