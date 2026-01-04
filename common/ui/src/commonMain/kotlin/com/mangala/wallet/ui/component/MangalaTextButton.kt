package com.mangala.wallet.ui.component

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun MangalaTextButton(
    onClick: () -> Unit,
    label: String,
    enabled: Boolean = true,
    contentColor: Color = MaterialTheme.mangalaColors.textPrimary,
    disableContentColor: Color = MaterialTheme.mangalaColors.textSecondary,
    modifier: Modifier = Modifier,
    size: MangalaButtonSize = MangalaButtonSize.Big,
    style: TextStyle = MangalaTypography.Size14Medium(),
) {
    MangalaTextButton(
        onClick = onClick,
        label = label,
        enabled = enabled,
        contentColor = contentColor,
        disableContentColor = disableContentColor,
        modifier = modifier,
        size = size,
        fontWeight = style.fontWeight ?: FontWeight.Medium,
        fontStyle = style.fontStyle ?: FontStyle.Normal,
        fontSize = style.fontSize
    )
}

@Composable
fun MangalaTextButton(
    onClick: () -> Unit,
    label: String,
    enabled: Boolean = true,
    contentColor: Color = MaterialTheme.mangalaColors.textPrimary,
    disableContentColor: Color = MaterialTheme.mangalaColors.textSecondary,
    modifier: Modifier,
    size: MangalaButtonSize = MangalaButtonSize.XMedium,
    fontWeight: FontWeight = FontWeight.Medium,
    fontStyle: FontStyle = FontStyle.Normal,
    fontSize: TextUnit = FontType.REGULAR
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = contentColor,
            disabledContentColor = disableContentColor
        ),
        shape = RoundedCornerShape(CornerRadius.Small),
        modifier = modifier.defaultMinSize(minHeight = size.height)
    ) {
        Text(
            text = label,
            fontWeight = fontWeight,
            fontFamily = getSfProFamilyFont(
                weight = fontWeight,
                fontStyle = fontStyle
            ),
            fontSize = fontSize
        )
    }
}