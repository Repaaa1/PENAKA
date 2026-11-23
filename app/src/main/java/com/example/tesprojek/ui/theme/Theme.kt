package com.example.tesprojek.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

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
        typography = Typography
    ) {
        // Font bawaan semua Text yang tidak menulis fontFamily sendiri
        ProvideTextStyle(TextStyle(fontFamily = Fredoka)) {
            content()
        }
    }
}