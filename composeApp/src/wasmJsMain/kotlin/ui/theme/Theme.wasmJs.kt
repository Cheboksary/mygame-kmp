package ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import kotlinx.browser.window

@Composable
actual fun AppTheme(
    darkTheme: Boolean,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        window.matchMedia("(prefers-color-scheme: dark)").matches -> darkScheme
        else -> lightScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}