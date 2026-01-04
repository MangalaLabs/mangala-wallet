package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.QuestionMark
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.resolve
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ApproverAuthorizationInput(
    firstInputLabel: String,
    firstInputValue: String,
    firstInputPlaceholder: String,
    onFirstInputValueChange: (String) -> Unit,
    firstInputLoading: Boolean,
    firstInputError: String? = null,
    secondInputLabel: String,
    secondInputValue: String,
    secondInputPlaceholder: String,
    onSecondInputValueChange: (String) -> Unit,
    secondInputLoading: Boolean,
    secondInputError: String? = null,
    subtitle: String,
    suggestionsActor: List<String>,
    suggestionsPermission: List<String>,
    shouldShowRemoveButton: Boolean,
    onUpdateBothInputValues: (String, String) -> Unit,
    onDelete: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isDropdownExpandedActor by remember { mutableStateOf(false) }
    var isDropdownExpandedPermission by remember { mutableStateOf(false) }

    MaxWidthColumn(
        Modifier
            .background(MaterialTheme.mangalaColors.bgInnerCard, RoundedCornerShape(CornerRadius.Small))
            .padding(
                vertical = Dimensions.Padding.half,
                horizontal = Dimensions.Padding.default
            )
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isDropdownExpandedActor,
            onExpandedChange = { expanded ->
                isDropdownExpandedActor = expanded
            }
        ) {
            ProposalTextInputField(
                label = firstInputLabel,
                query = firstInputValue,
                onQueryChange = { newValue ->
                    onFirstInputValueChange(newValue)
                    isDropdownExpandedActor = newValue.isNotEmpty()
                },
                modifier = Modifier.fillMaxWidth().onFocusChanged {
                    if (!it.isFocused) {
                        isDropdownExpandedActor = false
                    }
                },
                placeholder = secondInputPlaceholder,
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (firstInputLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.mangalaColors.iconPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        if (firstInputValue.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    onUpdateBothInputValues("", "")
                                }
                            ) {
                                Icon(
                                    imageVector = MangalaWalletPack.Clear,
                                    contentDescription = "Delete",
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.mangalaColors.iconPrimary
                                )
                            }
                        }
                    }
                },
                isError = firstInputError != null,
                errorText = firstInputError
            )

            if (suggestionsActor.isNotEmpty()) {
                DropdownMenu(
                    modifier = Modifier
                        .exposedDropdownSize()
                        .background(MaterialTheme.mangalaColors.bgInnerCard),
                    expanded = isDropdownExpandedActor,
                    onDismissRequest = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        isDropdownExpandedActor = false
                    },
                    properties = PopupProperties(focusable = false)
                ) {
                    suggestionsActor.forEach { suggestion ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = suggestion,
                                    color = MaterialTheme.mangalaColors.textPrimary,
                                    style = MangalaTypography.Size13Medium()
                                ) 
                            },
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onFirstInputValueChange(suggestion)
                                isDropdownExpandedActor = false
                            },
                            modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                        )
                    }
                }
            }
        }
        VerticalSpacer(Spacing.SMALL)
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isDropdownExpandedPermission,
            onExpandedChange = { expanded ->
                isDropdownExpandedPermission = expanded
            }
        ) {
        ProposalTextInputField(
            label = secondInputLabel,
            query = secondInputValue,
            onQueryChange = { newValue ->
                onSecondInputValueChange(newValue)
                isDropdownExpandedPermission = newValue.isNotEmpty()
            },
            placeholder = firstInputPlaceholder,
            modifier = Modifier.fillMaxWidth().onFocusChanged {
                if (!it.isFocused) {
                    isDropdownExpandedPermission = false
                }
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (secondInputLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.mangalaColors.iconPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    if (secondInputValue.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onSecondInputValueChange("")
                            }
                        ) {
                            Icon(
                                imageVector = MangalaWalletPack.Clear,
                                contentDescription = "Delete",
                                modifier = Modifier.size(24.dp),
                                tint = ColorsNew.primary_950
                            )
                        }
                    }
                }
            },
            isError = secondInputError != null,
            errorText = secondInputError
        )
            if (suggestionsPermission.isNotEmpty()) {
                DropdownMenu(
                    modifier = Modifier
                        .exposedDropdownSize()
                        .background(MaterialTheme.mangalaColors.bgInnerCard),
                    expanded = isDropdownExpandedPermission,
                    onDismissRequest = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        isDropdownExpandedPermission = false
                    },
                    properties = PopupProperties(focusable = false)
                ) {
                    suggestionsPermission.forEach { suggestion ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = suggestion,
                                    color = MaterialTheme.mangalaColors.textPrimary,
                                    style = MangalaTypography.Size13Medium()
                                ) 
                            },
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onSecondInputValueChange(suggestion)
                                isDropdownExpandedPermission = false
                            },
                            modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                        )
                    }
                }
            }
        }
        VerticalSpacer(Spacing.XSMALL)
        MaxWidthRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(Modifier.weight(1f)) {
                Text(
                    subtitle,
                    style = MangalaTypography.Size13Medium(),
                    color = MaterialTheme.mangalaColors.textSecondary
                )
                HorizontalSpacer(Spacing.XTINY)
                Icon(
                    imageVector = MangalaWalletPack.QuestionMark,
                    contentDescription = "Help",
                    tint = MaterialTheme.mangalaColors.iconSecondary
                )
            }
            if (shouldShowRemoveButton) {
                ApproverTextButton(
                    onClick = {
                        onDelete()
                    },
                    text = MR.strings.all_delete.desc().localized(),
                    contentPadding = PaddingValues(),
                    color = ColorsNew.error_600,
                    textStyle = MangalaTypography.Size13Regular()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ApproverTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.mangalaColors.textPrimary,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 0.dp,
        vertical = Dimensions.Padding.default
    ),
    textStyle: TextStyle = MangalaTypography.Size14Medium()
) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false,
        LocalMinimumInteractiveComponentSize provides 0.dp,
    ) {
        TextButton(
            modifier = modifier.then(Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)),
            onClick = onClick,
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                text,
                style = textStyle,
                color = color,
            )
        }
    }
}