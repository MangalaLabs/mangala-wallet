package com.mangala.wallet.features.conversationui.presentation.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun AddressInput(
    onAddressSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Address",
    initialValue: String = ""
) {
    var address by remember { mutableStateOf(initialValue) }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val borderBrush = if (isFocused) {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF227BFF),
                Color(0xFF1C8DF9),
                Color(0xFFB988EE),
                Color(0xFFEE4D5D)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF475569), // Slate-600
                Color(0xFF475569)  // Slate-600
            )
        )
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.Padding.default)
            .height(Dimensions.Height.xxxxLarge)
            .clip(RoundedCornerShape(Spacing.XSMALL))
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(Spacing.XSMALL)
            )
            .background(
                color = Color(0xFF1E293B), // Slate-800
                shape = RoundedCornerShape(Spacing.XSMALL)
            )
            .padding(horizontal = Spacing.SMALL, vertical = Spacing.XSMALL)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = address,
                onValueChange = { newValue ->
                    address = newValue
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    },
                textStyle = TextStyle(
                    color = MaterialTheme.mangalaColors.textPrimary, // Slate-100
                    fontSize = MangalaTypography.Size14Regular().fontSize
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (address.isNotBlank()) {
                            onAddressSelected(address.trim())
                        }
                    }
                ),
                cursorBrush = SolidColor(Color(0xFF3B82F6)), // Blue-500
                decorationBox = { innerTextField ->
                    if (address.isEmpty() && !isFocused) {
                        Text(
                            text = placeholder,
                            style = MangalaTypography.Size14Regular(),
                            color = Color(0xFF94A3B8) // Slate-400
                        )
                    }
                    innerTextField()
                },
                singleLine = true
            )
        }
    }
}