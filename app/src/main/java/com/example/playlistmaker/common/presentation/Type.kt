package com.example.playlistmaker.common.presentation

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

val YsDisplayFontFamily = FontFamily(
    Font(R.font.ys_display_regular, FontWeight.Normal),
    Font(R.font.ys_display_medium, FontWeight.Medium)
)

val Typography = Typography(
    h1 = TextStyle(
        fontFamily = YsDisplayFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        letterSpacing = 0.sp
    ),
    body1 = TextStyle(
        fontFamily = YsDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.02.sp
    )
)