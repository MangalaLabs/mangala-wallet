package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Check
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Paste
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Scan
import com.mangala.wallet.ui.RecipientValidationStatus
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.theme.mangalaColors


@Composable
fun ItemInput(
    value: String,
    hint: String = "",
    onValueChange: (String) -> Unit,
    validationStatus: RecipientValidationStatus? = null,
    imeAction: ImeAction = ImeAction.Done,
    invalidContent: @Composable () -> Unit = {},
    onClickScan: () -> Unit,
    titleContent: @Composable () -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val showFocusedActions = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> {
                    showFocusedActions.value = true
                }

                is FocusInteraction.Unfocus -> {
                    showFocusedActions.value = false
                }
            }
        }
    }

    Column {
        titleContent()
        Spacer(modifier = Modifier.height(Dimensions.Height.xSmall))
        CreateImportTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            placeholderText = hint,
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (validationStatus == RecipientValidationStatus.Validating) {
                        CircularProgressIndicator(
                            Modifier.size(20.dp),
                            color = MaterialTheme.mangalaColors.iconSecondary
                        )
                        HorizontalSpacer(Spacing.SMALL)
                    } else if (validationStatus == RecipientValidationStatus.Valid) {
                        Icon(
                            imageVector = MangalaWalletPack.Check,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Valid account name",
                            tint = Colors.second
                        )
                        HorizontalSpacer(Spacing.SMALL)
                    }

                    if (showFocusedActions.value) {
                        if (value.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    onValueChange("")
                                },
                            ) {
                                Icon(
                                    MangalaWalletPack.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.mangalaColors.iconPrimary,
                                    modifier = Modifier.size(Dimensions.IconButtonSize)
                                )
                            }
                        }

                        IconButton(onClick = {
                            val clipboardText =
                                clipboardManager.getText()?.text ?: ""
                            focusManager.clearFocus()
                            onValueChange(clipboardText)
                        }) {
                            Icon(
                                imageVector = MangalaWalletPack.Paste,
                                contentDescription = "Paste private key",
                                tint = MaterialTheme.mangalaColors.iconPrimary,
                                modifier = Modifier.size(Dimensions.IconButtonSize)
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            onClickScan()
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                imageVector = MangalaWalletPack.Scan,
                                contentDescription = "Scan account name",
                                tint = MaterialTheme.mangalaColors.iconPrimary
                            )
                        }

                        IconButton(onClick = {
                            val clipboardText =
                                clipboardManager.getText()?.text ?: ""
                            focusManager.clearFocus()
                            onValueChange(clipboardText)
                        }) {
                            Icon(
                                imageVector = MangalaWalletPack.Paste,
                                contentDescription = "Paste account name",
                                tint = MaterialTheme.mangalaColors.iconPrimary,
                                modifier = Modifier.size(Dimensions.IconButtonSize)
                            )
                        }
                    }

                    HorizontalSpacer(Spacing.XTINY)
                }
            },
            keyboardType = KeyboardType.Text,
            interactionSource = interactionSource,
            isError = validationStatus == RecipientValidationStatus.Invalid,
            imeAction = imeAction
        )
        if (validationStatus == RecipientValidationStatus.Invalid) {
            invalidContent()
        }
    }
}

@Composable
fun <T> CreateImportTextField(
    value: T,
    onValueChange: (T) -> Unit,
    placeholderText: String,
    isError: Boolean = false,
    trailingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    CompositionLocalProvider(LocalTextSelectionColors provides rememberCustomTextSelectionColors()) {
        OutlinedTextField(
            value = if (value is String) value else (value as TextFieldValue).text,
            onValueChange = { newValue ->
                if (value is String) {
                    onValueChange(newValue as T)
                } else if (value is TextFieldValue) {
                    onValueChange(TextFieldValue(newValue) as T)
                }
            },
            placeholder = {
                TextDescription2(
                    text = placeholderText,
                    color = MaterialTheme.mangalaColors.textSecondary,
                    fontSize = FontType.SMALL
                )
            },
            trailingIcon = {
                trailingIcon()
            },
            textStyle = TextStyle.Default.copy(
                fontSize = FontType.SMALL
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (isError) MaterialTheme.mangalaColors.buttonDestructiveContainer else MaterialTheme.mangalaColors.border,
                unfocusedBorderColor = if (isError) MaterialTheme.mangalaColors.buttonDestructiveContainer else MaterialTheme.mangalaColors.border,
                textColor = MaterialTheme.mangalaColors.textPrimary,
                cursorColor = MaterialTheme.mangalaColors.textPrimary,
            ),
            shape = RoundedCornerShape(CornerRadius.Small),
            visualTransformation = visualTransformation,
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.mangalaColors.bgInnerCard,
                    shape = RoundedCornerShape(CornerRadius.Small)
                ),
            interactionSource = interactionSource,
        )
    }
}

@Composable
private fun rememberCustomTextSelectionColors(): TextSelectionColors {
    val iconPrimary = MaterialTheme.mangalaColors.iconPrimary
    val iconSecondary = MaterialTheme.mangalaColors.iconSecondary
    return remember(iconPrimary, iconSecondary) {
        TextSelectionColors(
            handleColor = iconPrimary,
            backgroundColor = iconSecondary
        )
    }
}