package com.mangala.wallet.ui.theme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily

object MangalaTypography {
    @Composable
    fun Size8SemiBold(): TextStyle {
        return TextStyle(
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size10Regular(): TextStyle {
        return TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size10Medium(): TextStyle {
        return TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size10SemiBold(): TextStyle {
        return TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size12Regular(): TextStyle {
        return TextStyle(
            fontSize = 12.sp,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size12SemiBold(): TextStyle {
        return TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size12Medium(): TextStyle {
        return TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size13Medium(): TextStyle {
        return TextStyle(
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size13SemiBold(): TextStyle {
        return TextStyle(
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size13Regular(): TextStyle {
        return TextStyle(
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size13Italic(): TextStyle {
        return TextStyle(
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = getInterFontFamily(),
            fontStyle = FontStyle.Italic
        )
    }

    @Composable
    fun Size14Medium(): TextStyle {
        return TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size14SemiBold(): TextStyle {
        return TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size14Regular(): TextStyle {
        return TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size17Regular(): TextStyle {
        return TextStyle(
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size17Medium(): TextStyle {
        return TextStyle(
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = getInterFontFamily()
        )
    }

    @Composable
    fun Size17SemiBold(): TextStyle {
        return TextStyle(
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = getInterFontFamily()
        )
    }
}