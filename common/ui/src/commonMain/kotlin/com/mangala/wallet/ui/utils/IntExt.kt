package com.mangala.wallet.ui.utils

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Int.toDp(density: Density): Dp = (this.div(density.density)).dp