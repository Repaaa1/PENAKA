package com.example.tesprojek.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PenakaColorScheme = lightColorScheme(
    primary = ButtonYellow,
    onPrimary = OutlineBrown,
    background = SkyBlue,
    onBackground = OutlineBrown,
    surface = SkyBlue,
    onSurface = OutlineBrown
)

@Composable
fun PenakaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PenakaColorScheme,
        typography = Typography(),
        content = content
    )
}