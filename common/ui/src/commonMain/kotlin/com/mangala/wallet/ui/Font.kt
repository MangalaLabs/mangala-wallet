package com.mangala.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Black
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraLight
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.font.FontWeight.Companion.Thin
import androidx.compose.ui.text.font.FontWeight.Companion.W100
import androidx.compose.ui.text.font.FontWeight.Companion.W200
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.font.FontWeight.Companion.W800
import androidx.compose.ui.text.font.FontWeight.Companion.W900
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.compose.fontFamilyResource

@Composable
fun getSfProFamilyFont(weight: FontWeight, fontStyle: FontStyle = FontStyle.Normal) = when(weight) {
//    Thin, W100 -> fontFamilyResource(MR.fonts.sfpro.sfpro)
//    ExtraLight, W200 -> fontFamilyResource(MR.fonts.sfpro.sfpro)
//    Light, W300 -> fontFamilyResource(MR.fonts.sfpro.sfpro)
    Normal, W400 -> when(fontStyle) {
        FontStyle.Italic -> fontFamilyResource(MR.fonts.sfpro_italic)
        else -> fontFamilyResource(MR.fonts.sfpro)
    }
    Medium, W500 -> fontFamilyResource(MR.fonts.sfpro_medium)
    SemiBold, W600 -> fontFamilyResource(MR.fonts.sfpro_semibold)
    Bold, W700 -> fontFamilyResource(MR.fonts.sfpro_bold)
//    ExtraBold, W800 -> fontFamilyResource(MR.fonts.sfpro.sfpro)
//    Black, W900 -> fontFamilyResource(MR.fonts.sfpro.sfpro)
    else -> fontFamilyResource(MR.fonts.sfpro)
}