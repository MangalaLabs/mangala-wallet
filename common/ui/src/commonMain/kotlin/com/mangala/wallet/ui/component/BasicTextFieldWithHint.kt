package com.mangala.wallet.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.ui.getSfProFamilyFont

@Deprecated("Use BasicTextFieldWithHintAndTrailingIcons instead")
@Composable
fun BasicTextFieldWithHint(
    value: String?,
    hint: String,
    fontSize: TextUnit = 12.sp,
    hintColor: Color = Colors.gray,
    textColor: Color = Colors.darkGray,
    fontWeight: FontWeight = FontWeight.Normal,
    onValueChange: (String) -> Unit,
    boxModifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    Box(modifier = boxModifier) {
        if (value.isNullOrBlank()) {
            Text(
                text = hint,
                style = TextStyle(color = hintColor),
                maxLines = 1,
                fontSize = fontSize,
                color = hintColor,
                fontFamily = getSfProFamilyFont(fontWeight),
                modifier = Modifier.fillMaxWidth()
            )
        }

        BasicTextField(
            value = value.orEmpty(),
            onValueChange = onValueChange,
            modifier = textFieldModifier.then(Modifier.fillMaxWidth()),
            textStyle = TextStyle(
                fontSize = fontSize,
                color = textColor,
                fontFamily = getSfProFamilyFont(fontWeight)
            ),
            singleLine = singleLine, // Remove singleLine to allow wrapping
        )
    }
}
