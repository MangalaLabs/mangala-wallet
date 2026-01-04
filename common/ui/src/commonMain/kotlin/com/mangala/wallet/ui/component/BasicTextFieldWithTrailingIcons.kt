package com.mangala.wallet.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.getSfProFamilyFont

@Composable
fun BasicTextFieldWithHintAndTrailingIcons(
    value: TextFieldValue,
    placeholder: @Composable () -> Unit,
    textStyle: TextStyle,
    onValueChange: (TextFieldValue) -> Unit,
    boxModifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable () -> Unit,
    singleLine: Boolean = true
) {
    Box(modifier = boxModifier) {
        if (value.text.isEmpty()) {
            placeholder()
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = textFieldModifier.then(Modifier.fillMaxWidth().weight(1f)),
                singleLine = singleLine,
                textStyle = textStyle,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions
            )
            trailingIcon()
        }
    }
}

@Composable
fun BasicTextFieldWithHintAndTrailingIcons(
    value: String?,
    placeholder: @Composable () -> Unit,
    fontSize: TextUnit = 12.sp,
    textColor: Color = Colors.darkGray,
    fontWeight: FontWeight = FontWeight.Normal,
    onValueChange: (String) -> Unit,
    boxModifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable () -> Unit,
    singleLine: Boolean = true
) {
    Box(modifier = boxModifier) {
        if (value.isNullOrBlank()) {
            placeholder()
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value.orEmpty(),
                onValueChange = onValueChange,
                modifier = textFieldModifier.then(Modifier.fillMaxWidth().weight(1f)),
                singleLine = singleLine,
                textStyle = TextStyle(fontSize = fontSize, color = textColor, fontFamily = getSfProFamilyFont(fontWeight)),
                cursorBrush = SolidColor(textColor),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions
            )
            trailingIcon()
        }
    }
}

// TODO: Rename to BasicTextFieldWithHint when the OG BasicTextFieldWithHint is removed
@Composable
fun BasicTextFieldWithHintAndTrailingIcons(
    value: String?,
    hint: String,
    fontSize: TextUnit = 12.sp,
    hintColor: Color = Colors.gray,
    textColor: Color = Colors.darkGray,
    fontWeight: FontWeight = FontWeight.Normal,
    onValueChange: (String) -> Unit,
    boxModifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable () -> Unit,
    singleLine: Boolean = true
) {
    BasicTextFieldWithHintAndTrailingIcons(
        value = value,
        placeholder = {
            Text(
                text = hint,
                style = TextStyle(color = hintColor),
                maxLines = 1,
                fontSize = fontSize,
                color = hintColor,
                fontFamily = getSfProFamilyFont(fontWeight),
                modifier = Modifier.fillMaxWidth()
            )
        },
        fontSize = fontSize,
        textColor = textColor,
        fontWeight = fontWeight,
        onValueChange = onValueChange,
        boxModifier = boxModifier,
        textFieldModifier = textFieldModifier,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = trailingIcon,
        singleLine = singleLine
    )
}