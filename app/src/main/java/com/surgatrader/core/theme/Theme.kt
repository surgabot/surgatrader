package com.surgatrader.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = ObsidianBg,
    primaryContainer = CyanAccentVariant,
    onPrimaryContainer = TextPrimary,
    secondary = GoldAccent,
    onSecondary = ObsidianBg,
    tertiary = SafeEmerald,
    onTertiary = ObsidianBg,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateCard,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder,
    error = DangerRuby,
    onError = TextPrimary
)

private val LightColorScheme = darkColorScheme( // Keep default dark for trading UI
    primary = CyanAccent,
    onPrimary = ObsidianBg,
    primaryContainer = CyanAccentVariant,
    onPrimaryContainer = TextPrimary,
    secondary = GoldAccent,
    onSecondary = ObsidianBg,
    tertiary = SafeEmerald,
    onTertiary = ObsidianBg,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateCard,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder,
    error = DangerRuby,
    onError = TextPrimary
)

@Composable
fun SurgaTraderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = ObsidianBg.toArgb()
                it.navigationBarColor = ObsidianBg.toArgb()
                val controller = WindowCompat.getInsetsController(it, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
