package com.poker.tournamentdirector.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PokerGreenLight,
    onPrimary = PokerBlack,
    primaryContainer = PokerGreen,
    onPrimaryContainer = PokerIvory,
    secondary = PokerGold,
    onSecondary = PokerBlack,
    secondaryContainer = PokerGoldDark,
    onSecondaryContainer = PokerIvory,
    tertiary = PokerBlue,
    onTertiary = PokerIvory,
    error = PokerRed,
    background = PokerBlack,
    onBackground = PokerIvory,
    surface = PokerFelt,
    onSurface = PokerIvory,
    surfaceVariant = ColorTokens.DarkSurfaceVariant,
    onSurfaceVariant = ColorTokens.DarkOnSurfaceVariant,
    outline = ColorTokens.DarkOutline,
)

private val LightColorScheme = lightColorScheme(
    primary = PokerGreen,
    onPrimary = PokerIvory,
    primaryContainer = ColorTokens.LightPrimaryContainer,
    onPrimaryContainer = PokerBlack,
    secondary = PokerGoldDark,
    onSecondary = PokerIvory,
    secondaryContainer = ColorTokens.LightSecondaryContainer,
    onSecondaryContainer = PokerBlack,
    tertiary = PokerBlue,
    onTertiary = PokerIvory,
    error = PokerRed,
    background = ColorTokens.LightBackground,
    onBackground = PokerBlack,
    surface = ColorTokens.LightSurface,
    onSurface = PokerBlack,
    surfaceVariant = ColorTokens.LightSurfaceVariant,
    onSurfaceVariant = ColorTokens.LightOnSurfaceVariant,
    outline = ColorTokens.LightOutline,
)

@Composable
fun PokerTournamentDirectorTheme(
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
        typography = AppTypography,
        content = content,
    )
}

private object ColorTokens {
    val DarkSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF22392D)
    val DarkOnSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFD1D8CF)
    val DarkOutline = androidx.compose.ui.graphics.Color(0xFF7D8A80)
    val LightPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFBFE8CB)
    val LightSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFFFE5A6)
    val LightBackground = androidx.compose.ui.graphics.Color(0xFFFBFCF7)
    val LightSurface = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
    val LightSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFE1E9DE)
    val LightOnSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF404940)
    val LightOutline = androidx.compose.ui.graphics.Color(0xFF707970)
}
