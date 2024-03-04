package com.example.dominobackgammonclient.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme1 = lightColorScheme(
    primary = md_theme_light_primary1,
    onPrimary = md_theme_light_onPrimary1,
    primaryContainer = md_theme_light_primaryContainer1,
    onPrimaryContainer = md_theme_light_onPrimaryContainer1,
    secondary = md_theme_light_secondary1,
    onSecondary = md_theme_light_onSecondary1,
    secondaryContainer = md_theme_light_secondaryContainer1,
    onSecondaryContainer = md_theme_light_onSecondaryContainer1,
    tertiary = md_theme_light_tertiary1,
    onTertiary = md_theme_light_onTertiary1,
    tertiaryContainer = md_theme_light_tertiaryContainer1,
    onTertiaryContainer = md_theme_light_onTertiaryContainer1,
    error = md_theme_light_error1,
    errorContainer = md_theme_light_errorContainer1,
    onError = md_theme_light_onError1,
    onErrorContainer = md_theme_light_onErrorContainer1,
    background = md_theme_light_background1,
    onBackground = md_theme_light_onBackground1,
    surface = md_theme_light_surface1,
    onSurface = md_theme_light_onSurface1,
    surfaceVariant = md_theme_light_surfaceVariant1,
    onSurfaceVariant = md_theme_light_onSurfaceVariant1,
    outline = md_theme_light_outline1,
    inverseOnSurface = md_theme_light_inverseOnSurface1,
    inverseSurface = md_theme_light_inverseSurface1,
    inversePrimary = md_theme_light_inversePrimary1,
    surfaceTint = md_theme_light_surfaceTint1,
    outlineVariant = md_theme_light_outlineVariant1,
    scrim = md_theme_light_scrim1,
)

private val DarkColorScheme1 = darkColorScheme(
    primary = md_theme_dark_primary1,
    onPrimary = md_theme_dark_onPrimary1,
    primaryContainer = md_theme_dark_primaryContainer1,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer1,
    secondary = md_theme_dark_secondary1,
    onSecondary = md_theme_dark_onSecondary1,
    secondaryContainer = md_theme_dark_secondaryContainer1,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer1,
    tertiary = md_theme_dark_tertiary1,
    onTertiary = md_theme_dark_onTertiary1,
    tertiaryContainer = md_theme_dark_tertiaryContainer1,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer1,
    error = md_theme_dark_error1,
    errorContainer = md_theme_dark_errorContainer1,
    onError = md_theme_dark_onError1,
    onErrorContainer = md_theme_dark_onErrorContainer1,
    background = md_theme_dark_background1,
    onBackground = md_theme_dark_onBackground1,
    surface = md_theme_dark_surface1,
    onSurface = md_theme_dark_onSurface1,
    surfaceVariant = md_theme_dark_surfaceVariant1,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant1,
    outline = md_theme_dark_outline1,
    inverseOnSurface = md_theme_dark_inverseOnSurface1,
    inverseSurface = md_theme_dark_inverseSurface1,
    inversePrimary = md_theme_dark_inversePrimary1,
    surfaceTint = md_theme_dark_surfaceTint1,
    outlineVariant = md_theme_dark_outlineVariant1,
    scrim = md_theme_dark_scrim1,
)


private val LightColorScheme2 = lightColorScheme(
    primary = md_theme_light_primary2,
    onPrimary = md_theme_light_onPrimary2,
    primaryContainer = md_theme_light_primaryContainer2,
    onPrimaryContainer = md_theme_light_onPrimaryContainer2,
    secondary = md_theme_light_secondary2,
    onSecondary = md_theme_light_onSecondary2,
    secondaryContainer = md_theme_light_secondaryContainer2,
    onSecondaryContainer = md_theme_light_onSecondaryContainer2,
    tertiary = md_theme_light_tertiary2,
    onTertiary = md_theme_light_onTertiary2,
    tertiaryContainer = md_theme_light_tertiaryContainer2,
    onTertiaryContainer = md_theme_light_onTertiaryContainer2,
    error = md_theme_light_error2,
    errorContainer = md_theme_light_errorContainer2,
    onError = md_theme_light_onError2,
    onErrorContainer = md_theme_light_onErrorContainer2,
    background = md_theme_light_background2,
    onBackground = md_theme_light_onBackground2,
    surface = md_theme_light_surface2,
    onSurface = md_theme_light_onSurface2,
    surfaceVariant = md_theme_light_surfaceVariant2,
    onSurfaceVariant = md_theme_light_onSurfaceVariant2,
    outline = md_theme_light_outline2,
    inverseOnSurface = md_theme_light_inverseOnSurface2,
    inverseSurface = md_theme_light_inverseSurface2,
    inversePrimary = md_theme_light_inversePrimary2,
    surfaceTint = md_theme_light_surfaceTint2,
    outlineVariant = md_theme_light_outlineVariant2,
    scrim = md_theme_light_scrim2,
)

private val DarkColorScheme2 = darkColorScheme(
    primary = md_theme_dark_primary2,
    onPrimary = md_theme_dark_onPrimary2,
    primaryContainer = md_theme_dark_primaryContainer2,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer2,
    secondary = md_theme_dark_secondary2,
    onSecondary = md_theme_dark_onSecondary2,
    secondaryContainer = md_theme_dark_secondaryContainer2,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer2,
    tertiary = md_theme_dark_tertiary2,
    onTertiary = md_theme_dark_onTertiary2,
    tertiaryContainer = md_theme_dark_tertiaryContainer2,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer2,
    error = md_theme_dark_error2,
    errorContainer = md_theme_dark_errorContainer2,
    onError = md_theme_dark_onError2,
    onErrorContainer = md_theme_dark_onErrorContainer2,
    background = md_theme_dark_background2,
    onBackground = md_theme_dark_onBackground2,
    surface = md_theme_dark_surface2,
    onSurface = md_theme_dark_onSurface2,
    surfaceVariant = md_theme_dark_surfaceVariant2,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant2,
    outline = md_theme_dark_outline2,
    inverseOnSurface = md_theme_dark_inverseOnSurface2,
    inverseSurface = md_theme_dark_inverseSurface2,
    inversePrimary = md_theme_dark_inversePrimary2,
    surfaceTint = md_theme_dark_surfaceTint2,
    outlineVariant = md_theme_dark_outlineVariant2,
    scrim = md_theme_dark_scrim2,
)


@Composable
fun DominoBackgammonClientTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    colourScheme: Int = 1,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> when(colourScheme) {
            1 -> DarkColorScheme1
            2 -> DarkColorScheme2
            else -> DarkColorScheme1
        }
        else -> when(colourScheme) {
            1 -> LightColorScheme1
            2 -> LightColorScheme2
            else -> LightColorScheme1
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}