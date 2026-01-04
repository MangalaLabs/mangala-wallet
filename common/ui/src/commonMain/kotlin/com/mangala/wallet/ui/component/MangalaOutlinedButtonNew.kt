package com.mangala.wallet.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.mangala.wallet.ui.theme.MangalaTypography

@Composable
fun MangalaOutlinedButtonNew(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    size: MangalaButtonSize = MangalaButtonSize.Big,
    style: TextStyle = MangalaTypography.Size14Medium(),
) {
    MangalaGradientButton(
        label = label,
        onClick = onClick,
        enabled = enabled,
        size = size,
        modifier = modifier,
        buttonStyle = MangalaButtonStyle.OUTLINED,
        style = style,
    )
}