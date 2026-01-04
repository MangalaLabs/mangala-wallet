package com.mangala.wallet.ui.component

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.theme.MangalaTypography

@Composable
fun MangalaButton(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ColorsNew.primary_950,
    contentColor: Color = ColorsNew.white,
    size: MangalaButtonSize = MangalaButtonSize.Big,
    style: TextStyle = MangalaTypography.Size14Medium(),
    disabledBackgroundColor: Color = ColorsNew.primary_200,
    disabledContentColor: Color = ColorsNew.primary_400,
) {
    Button(
        modifier = modifier.defaultMinSize(minHeight = size.height),
        onClick = onClick,
        enabled = enabled,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        shape = RoundedCornerShape(CornerRadius.Small),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            disabledBackgroundColor = disabledBackgroundColor,
            disabledContentColor = disabledContentColor
        )
    ) {
        Text(
            text = label,
            style = style,
        )
    }
}