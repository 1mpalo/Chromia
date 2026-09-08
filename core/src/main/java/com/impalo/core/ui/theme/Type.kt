package com.impalo.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.impalo.core.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val InterFontFamily = FontFamily(
    // Обычные
    Font(R.font.inter_thin, FontWeight.Thin),
    Font(R.font.inter_extralight, FontWeight.ExtraLight),
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold),
    Font(R.font.inter_black, FontWeight.Black),

    // Курсивные
    Font(R.font.inter_thinitalic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.inter_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.inter_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.inter_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.inter_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.inter_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.inter_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.inter_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.inter_blackitalic, FontWeight.Black, FontStyle.Italic)
)

val InterDisplayFontFamily = FontFamily(
    // Обычные
    Font(R.font.interdisplay_thin, FontWeight.Thin),
    Font(R.font.interdisplay_extralight, FontWeight.ExtraLight),
    Font(R.font.interdisplay_light, FontWeight.Light),
    Font(R.font.interdisplay_regular, FontWeight.Normal),
    Font(R.font.interdisplay_medium, FontWeight.Medium),
    Font(R.font.interdisplay_semibold, FontWeight.SemiBold),
    Font(R.font.interdisplay_bold, FontWeight.Bold),
    Font(R.font.interdisplay_extrabold, FontWeight.ExtraBold),
    Font(R.font.interdisplay_black, FontWeight.Black),

    // Курсивные
    Font(R.font.interdisplay_thinitalic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.interdisplay_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.interdisplay_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.interdisplay_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.interdisplay_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.interdisplay_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.interdisplay_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.interdisplay_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.interdisplay_blackitalic, FontWeight.Black, FontStyle.Italic)
)