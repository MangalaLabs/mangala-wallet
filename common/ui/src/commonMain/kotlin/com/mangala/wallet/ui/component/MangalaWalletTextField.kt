package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear

@Composable
fun MangalaWalletTextField(
    value: String?,
    hint: String,
    onValueChange: (String) -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    MangalaWalletTextField(
        value = value,
        hint = hint,
        onValueChange = onValueChange,
        trailingIcon = {
            if (value.isNullOrEmpty().not()) {
                MangalaWalletIconButton(
                    MangalaWalletPack.Clear,
                    modifier = Modifier.size(16.dp),
                    tint = Colors.caption,
                    onClick = { onValueChange("") }
                )
            }
        },
        keyboardActions = keyboardActions
    )
}

@Composable
fun MangalaWalletTextField(
    value: String?,
    hint: String,
    fontSize: TextUnit = FontType.REGULAR,
    fontWeight: FontWeight = FontWeight.Normal,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable () -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    BasicTextFieldWithHintAndTrailingIcons(
        value = value,
        hint = hint,
        onValueChange = onValueChange,
        fontSize = fontSize,
        hintColor = Colors.stroke,
        textColor = Colors.main1Text,
        fontWeight = fontWeight,
        boxModifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(CornerRadius.Small))
            .padding(horizontal = Spacing.SMALL, vertical = Spacing.XSMALL),
        trailingIcon = trailingIcon,
        keyboardActions = keyboardActions
    )
}

@Composable
fun MangalaWalletTextFieldAndTrailingIcons(
    value: String?,
    hint: String,
    fontSize: TextUnit = FontType.REGULAR,
    fontWeight: FontWeight = FontWeight.Normal,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable () -> Unit,
    modifier: Modifier
) {
    BasicTextFieldWithHintAndTrailingIcons(
        value = value,
        hint = hint,
        onValueChange = onValueChange,
        fontSize = fontSize,
        hintColor = Colors.stroke,
        textColor = Colors.main1Text,
        fontWeight = fontWeight,
        boxModifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(CornerRadius.Small))
            .padding(horizontal = Spacing.SMALL, vertical = Spacing.XSMALL),
        trailingIcon = trailingIcon
    )
}