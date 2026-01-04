package com.mangala.wallet.ui.component

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class MangalaButtonSize(
    val height: Dp,
    val horizontalPadding: Dp = 0.dp,
    val verticalPadding: Dp = 0.dp,
    val fontSize: TextUnit
) {
    Small(32.dp, fontSize = 13.sp),
    Medium(40.dp, fontSize = 13.sp),
    XMedium(44.dp, fontSize = 13.sp),
    Big(48.dp, horizontalPadding = 24.dp, verticalPadding = 14.dp, fontSize = 17.sp),
}