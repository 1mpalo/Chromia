package com.impalo.core.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    // Основные действия и активные элементы.
    primary = Neutral200,
    onPrimary = Neutral950,
    primaryContainer = Neutral850,
    onPrimaryContainer = Neutral100,
    inversePrimary = Neutral700,

    // Дополнительные действия и элементы выбора.
    secondary = Neutral300,
    onSecondary = Neutral950,
    secondaryContainer = Neutral900,
    onSecondaryContainer = Neutral200,

    // Дополнительная группа акцентов.
    // Пока тоже нейтральная — цветной акцент не нужен.
    tertiary = Neutral300,
    onTertiary = Neutral950,
    tertiaryContainer = Neutral850,
    onTertiaryContainer = Neutral200,

    // Общий фон.
    background = Neutral950,
    onBackground = Neutral200,

    // Основные поверхности и содержимое.
    surface = Neutral950,
    onSurface = Neutral200,
    surfaceVariant = Neutral850,
    onSurfaceVariant = Neutral400,

    // Уровни поверхностей:
    // чем выше уровень, тем немного светлее фон.
    surfaceDim = Neutral950,
    surfaceBright = Neutral800,
    surfaceContainerLowest = Neutral950,
    surfaceContainerLow = Neutral925,
    surfaceContainer = Neutral900,
    surfaceContainerHigh = Neutral850,
    surfaceContainerHighest = Neutral800,

    // Рамки и разделители.
    outline = Neutral500,
    outlineVariant = Neutral850,

    // Контрастные поверхности, например Snackbar.
    inverseSurface = Neutral200,
    inverseOnSurface = Neutral950,

    // Ошибки.
    error = Red300,
    onError = Red950,
    errorContainer = Red900,
    onErrorContainer = Red200,

    // Затемнение за модальными окнами.
    scrim = Black,

    // Поверхности различаем заданными цветами,
    // без дополнительного тонального оттенка.
    surfaceTint = Color.Transparent
)

private val LightColorScheme = lightColorScheme(
    // Основные действия и активные элементы.
    primary = Neutral200,
    onPrimary = Neutral950,
    primaryContainer = Neutral850,
    onPrimaryContainer = Neutral100,
    inversePrimary = Neutral700,

    // Дополнительные действия и элементы выбора.
    secondary = Neutral300,
    onSecondary = Neutral950,
    secondaryContainer = Neutral900,
    onSecondaryContainer = Neutral200,

    // Дополнительная группа акцентов.
    // Пока тоже нейтральная — цветной акцент не нужен.
    tertiary = Neutral300,
    onTertiary = Neutral950,
    tertiaryContainer = Neutral850,
    onTertiaryContainer = Neutral200,

    // Общий фон.
    background = Neutral950,
    onBackground = Neutral200,

    // Основные поверхности и содержимое.
    surface = Neutral950,
    onSurface = Neutral200,
    surfaceVariant = Neutral850,
    onSurfaceVariant = Neutral400,

    // Уровни поверхностей:
    // чем выше уровень, тем немного светлее фон.
    surfaceDim = Neutral950,
    surfaceBright = Neutral800,
    surfaceContainerLowest = Neutral950,
    surfaceContainerLow = Neutral925,
    surfaceContainer = Neutral900,
    surfaceContainerHigh = Neutral850,
    surfaceContainerHighest = Neutral800,

    // Рамки и разделители.
    outline = Neutral500,
    outlineVariant = Neutral850,

    // Контрастные поверхности, например Snackbar.
    inverseSurface = Neutral200,
    inverseOnSurface = Neutral950,

    // Ошибки.
    error = Red300,
    onError = Red950,
    errorContainer = Red900,
    onErrorContainer = Red200,

    // Затемнение за модальными окнами.
    scrim = Black,

    // Поверхности различаем заданными цветами,
    // без дополнительного тонального оттенка.
    surfaceTint = Color.Transparent
)

@Composable
fun ChromiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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