package com.mangala.wallet.common.mokoresources.font

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.compose.asFont

@Composable
fun getInterFontFamily() = FontFamily(
    MR.fonts.inter_18pt_black.asFont(weight = FontWeight.Black, style = FontStyle.Normal)!!,
    MR.fonts.inter_18pt_blackitalic.asFont(weight = FontWeight.Black, style = FontStyle.Italic)!!,
    MR.fonts.inter_18pt_bold.asFont(weight = FontWeight.Bold, style = FontStyle.Normal)!!,
    MR.fonts.inter_18pt_bolditalic.asFont(weight = FontWeight.Bold, style = FontStyle.Italic)!!,
    MR.fonts.inter_18pt_extrabold.asFont(weight = FontWeight.ExtraBold, style = FontStyle.Normal)!!,
    MR.fonts.inter_18pt_extrabolditalic.asFont(weight = FontWeight.ExtraBold, style = FontStyle.Italic)!!,
    MR.fonts.inter_18pt_extralight.asFont(weight = FontWeight.ExtraLight, style = FontStyle.Normal)!!,
    MR.fonts.inter_18pt_extralightitalic.asFont(weight = FontWeight.ExtraLight, style = FontStyle.Italic)!!,
    MR.fonts.inter_18pt_light.asFont(weight = FontWeight.Light, style = FontStyle.Normal)!!,
    MR.fonts.inter_18pt_lightitalic.asFont(weight = FontWeight.Light, style = FontStyle.Italic)!!,
    MR.fonts.inter_18pt_medium.asFont(weight = FontWeight.Medium, style = FontStyle.Normal)!!,
    MR.fonts.inter_18pt_mediumitalic.asFont(weight = FontWeight.Medium, style = FontStyle.Italic)!!,
    MR.fonts.inter_18pt_regular.asFont(weight = FontWeight.Normal, style = FontStyle.Normal)!!,
    MR.fonts.inter_18pt_italic.asFont(weight = FontWeight.Normal, style = FontStyle.Italic)!!,
    MR.fonts.inter_18pt_semibold.asFont(weight = FontWeight.SemiBold, style = FontStyle.Normal)!!,
    MR.fonts.inter_18pt_semibolditalic.asFont(weight = FontWeight.SemiBold, style = FontStyle.Italic)!!,
    MR.fonts.inter_18pt_thin.asFont(weight = FontWeight.Thin, style = FontStyle.Normal)!!,
    MR.fonts.inter_18pt_thinitalic.asFont(weight = FontWeight.Thin, style = FontStyle.Italic)!!
)