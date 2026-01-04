package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun InputBasic(
    value: String,
    placeholder: String,
    onNameChange: (String) -> Unit,
    enable: Boolean = true,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.mangalaColors.bgInnerCard,
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize() // Ensures 48dp minimum
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            BasicTextField(
                value = value,
                enabled = enable,
                onValueChange = onNameChange,
                textStyle = MangalaTypography.Size14Regular().merge(
                    color = MaterialTheme.mangalaColors.textPrimary
                ),
                cursorBrush = SolidColor(MaterialTheme.mangalaColors.textPrimary),
                singleLine = singleLine,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier
                    .fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.mangalaColors.textSecondary
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}