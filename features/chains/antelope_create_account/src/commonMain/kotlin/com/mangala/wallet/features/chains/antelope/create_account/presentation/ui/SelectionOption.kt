package com.mangala.wallet.features.chains.antelope.create_account.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MaxWidthBox

@Composable
fun SelectionOption(
    text: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    SelectionOption(onClick, selected, enabled) {
        SelectionOptionText(text)
    }
}

@Composable
fun SelectionOption(
    onClick: () -> Unit,
    selected: Boolean,
    enabled: Boolean = true,
    paddingStart: Dp = Dimensions.Padding.default,
    paddingEnd: Dp = Dimensions.Padding.default,
    paddingTop: Dp = Dimensions.Padding.small,
    paddingBottom: Dp = Dimensions.Padding.small,
    content: @Composable () -> Unit
) {
    MaxWidthBox(
        modifier = selectedBorder(selected)
            .clip(RoundedCornerShape(CornerRadius.Small))
            .background(Colors.white)
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier.alpha(0.5f))
            .padding(
                start = paddingStart,
                end = paddingEnd,
                top = paddingTop,
                bottom = paddingBottom
            )
    ) {
        content()
    }
}

@Composable
fun SelectionOptionText(
    text: String,
    color: Color = Colors.darkDarkGray,
    modifier: Modifier = Modifier
) {
    TextDescription2(text = text, color = color, modifier = modifier)
}

private fun selectedBorder(selected: Boolean) = if (selected) {
    Modifier.border(1.dp, Colors.darkDarkGray, RoundedCornerShape(CornerRadius.Small))
} else {
    Modifier
}