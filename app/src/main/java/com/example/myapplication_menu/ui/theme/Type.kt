package com.example.myapplication_menu.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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

// Estilos de texto personalizados para la tienda de donas
val MenuTitleStyle = TextStyle(
    fontFamily = FontFamily.Cursive,
    fontWeight = FontWeight.Bold,
    fontSize = 50.sp
)

val CategoryTitleStyle = TextStyle(
    fontFamily = FontFamily.Cursive,
    fontWeight = FontWeight.Bold,
    fontSize = 40.sp
)

val ProductNameStyle = TextStyle(
    fontFamily = FontFamily.Cursive,
    fontWeight = FontWeight.Bold,
    fontSize = 26.sp
)

val ProductPriceStyle = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp
)