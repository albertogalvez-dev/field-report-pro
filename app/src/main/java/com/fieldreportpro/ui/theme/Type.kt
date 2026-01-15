package com.fieldreportpro.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val FieldReportTypography = Typography(
    titleLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
    ),
    labelMedium = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
    )
)

object AppTextStyles {
    val SectionTitle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    )
    val CardTitle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    )
    val Body = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    )
    val ChipText = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
    )
}
