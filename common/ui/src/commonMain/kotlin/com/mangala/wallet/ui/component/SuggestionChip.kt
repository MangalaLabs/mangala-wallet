package com.mangala.wallet.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SuggestionChip(
    value: String,
    isSelected: Boolean,
    isLoading: Boolean,
    color: Color = Colors.gray,
    contentColor: Color = Colors.mintGreen,
    backgroundColor: Color = Color.Transparent,
    onClick: () -> Unit,
) {
    Spacer(modifier = Modifier.width(Spacing.XTINY))
    Chip(
        onClick = onClick,
        enabled = !isLoading,
        colors = ChipDefaults.chipColors(
            backgroundColor = backgroundColor,
            contentColor = if (isSelected) {
                contentColor
            } else color,
        ),
        border = BorderStroke(1.dp, Colors.paleGray),
        shape = CircleShape,
        modifier = Modifier.mangalaWalletPlaceholder(isLoading)
    ) {
        Text(
            text = "$value%",
            color = if (isSelected) {
                contentColor
            } else color,
            fontSize = FontType.TINY,
            fontWeight = FontWeight.Medium
        )
    }
    Spacer(modifier = Modifier.width(Spacing.XTINY))
}