package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
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

@Composable
fun ProposalAuthorizationInput(
    firstInputLabel: String,
    firstInputValue: String,
    firstInputPlaceholder: String,
    onFirstInputValueChange: (value: String, isChoosingFromSuggestion: Boolean) -> Unit,
    firstInputLoading: Boolean,
    firstInputError: WrappedStringResource? = null,
    firstInputSuggestions: List<String>,
    secondInputLabel: String,
    secondInputValue: String,
    secondInputPlaceholder: String,
    onSecondInputValueChange: (value: String, isChoosingFromSuggestion: Boolean) -> Unit,
    secondInputSuggestions: List<String>,
    secondInputLoading: Boolean,
    secondInputError: WrappedStringResource? = null,
    subtitle: String?,
    shouldShowRemoveButton: Boolean,
    onDelete: () -> Unit,
    isLastItem: Boolean = false,
    secondInputVisible: Boolean = true
) {
    MaxWidthColumn(
        Modifier
            .background(MaterialTheme.mangalaColors.bgInnerCard, RoundedCornerShape(CornerRadius.Small))
            .padding(
                vertical = Dimensions.Padding.half,
                horizontal = Dimensions.Padding.default
            )
    ) {
        ProposalAutoCompleteTextInputField(
            label = firstInputLabel,
            query = firstInputValue,
            onQueryChange = { newValue, isChoosingFromSuggestion ->
                onFirstInputValueChange(newValue, isChoosingFromSuggestion)
            },
            suggestions = firstInputSuggestions,
            modifier = Modifier.fillMaxWidth(),
            placeholder = secondInputPlaceholder,
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (firstInputLoading) {
                        CircularProgressIndicator(color = MaterialTheme.mangalaColors.iconPrimary, modifier = Modifier.size(24.dp))
                    }
                    if (firstInputValue.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onFirstInputValueChange("", false)
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
            errorText = firstInputError?.resolve()
        )
        AnimatedVisibility(secondInputVisible) {
            MaxWidthColumn {
                VerticalSpacer(Spacing.XSMALL)
                ProposalAutoCompleteTextInputField(
                    label = secondInputLabel,
                    query = secondInputValue,
                    onQueryChange = { newValue, isChoosingFromSuggestion ->
                        onSecondInputValueChange(newValue, isChoosingFromSuggestion)
                    },
                    suggestions = secondInputSuggestions,
                    placeholder = firstInputPlaceholder,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = if (isLastItem) ImeAction.Done else ImeAction.Next
                    ),
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
                                        onSecondInputValueChange("", false)
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
                    isError = secondInputError != null,
                    errorText = secondInputError?.resolve(),
                )
            }
        }
        VerticalSpacer(Spacing.XSMALL)
        MaxWidthRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(Modifier.weight(1f)) {
                subtitle?.let {
                    Text(
                        text = it,
                        style = MangalaTypography.Size13Medium(),
                        color = MaterialTheme.mangalaColors.textSecondary
                    )
                    HorizontalSpacer(Spacing.XTINY)
                    Icon(
                        imageVector = MangalaWalletPack.QuestionMark,
                        contentDescription = "Help",
                        tint = MaterialTheme.mangalaColors.iconSecondary,
                    )
                }
            }
            if (shouldShowRemoveButton) {
                ProposalTextButton(
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