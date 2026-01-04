package com.mangala.wallet.ui.modifier

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.CornerRadius

fun Modifier.roundedCornersItemShape(list: List<*>, currentIndex: Int): Modifier {
    return Modifier.then(Modifier.clip(roundedCornerItemShape(list, currentIndex)))
}

fun roundedCornerItemShape(list: List<*>, currentIndex: Int) = when {
    list.size == 1 -> RoundedCornerShape(CornerRadius.Small)
    currentIndex == 0 -> RoundedCornerShape(topStart = CornerRadius.Small, topEnd = CornerRadius.Small)
    currentIndex == list.lastIndex -> RoundedCornerShape(bottomStart = CornerRadius.Small, bottomEnd = CornerRadius.Small)
    else -> RoundedCornerShape(0.dp)
}