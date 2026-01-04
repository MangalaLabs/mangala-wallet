package com.mangala.wallet.features.chains.antelope.create_account.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step2.Step2SelectAccountNameUiState
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountCharacterValidationResult
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.EditCircle
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.component.CreateImportTextField
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.transformation.SuffixTransformation
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun ColumnScope.SelectAccountName(
    uiState: Step2SelectAccountNameUiState.Ready,
    modifier: Modifier = Modifier,
    onClickSetAccountType: ((AccountNameType) -> Unit)?,
    onAccountNameChange: (TextFieldValue) -> Unit,
    onClickSuggest: () -> Unit
) {
    MaxWidthColumn(
        modifier = modifier,
    ) {
        TextSubTitle(
            text = MR.strings.title_step_2_select_account_name.desc().localized(),
            fontWeight = FontWeight.Medium,
            color = Colors.darkDarkGray
        )
        val accountTypeText = getAccountTypeText(uiState.accountType)
        FlowRow {
            TextDescription2(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = MR.strings.label_step_2_select_account_name_account_type.desc()
                    .localized() + " ",
                color = Colors.caption
            )
            TextDescription2(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = accountTypeText,
                color = Colors.darkDarkGray,
                fontWeight = FontWeight.SemiBold
            )
            onClickSetAccountType?.let {
                IconButton(onClick = {
                    onClickSetAccountType.invoke(uiState.accountType)
                }, modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(MangalaWalletPack.EditCircle, contentDescription = null)
                }
            }
        }
        VerticalSpacer(Spacing.BASE)
        TextDescription2(
            text = MR.strings.label_step_2_select_account_name_account_name.desc()
                .localized(),
            color = Colors.darkDarkGray
        )
        VerticalSpacer(Spacing.TINY)
        AccountNameTextField(uiState, onAccountNameChange)
        MaxWidthRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextDescription2(
                StringDesc.ResourceFormatted(
                    MR.strings.label_step_2_select_account_name_character_limit,
                    uiState.accountNameLengthWithSuffix,
                    AntelopeAccount.MAX_LENGTH_ACCOUNT_NAME
                ).localized(),
                color = Colors.appleSubText
            )
            TextDescription2(
                modifier = Modifier.padding(vertical = Dimensions.Padding.small)
                    .clickable { onClickSuggest() }, // Use padding to increase clickable area
                text = StringDesc.ResourceFormatted(
                    MR.strings.button_step_2_select_account_name_character_suggest,
                    uiState.accountName.text.length,
                    AntelopeAccount.MAX_LENGTH_ACCOUNT_NAME
                ).localized(),
                color = Colors.second
            )
        }
        VerticalSpacer(Spacing.XSMALL)
        ValidationChecklist(uiState)
    }
}

@Composable
private fun AccountNameTextField(
    uiState: Step2SelectAccountNameUiState.Ready,
    onAccountNameChange: (TextFieldValue) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    CreateImportTextField(
        value = uiState.accountName,
        onValueChange = {
            onAccountNameChange(it)
        },
        placeholderText = MR.strings.hint_step_2_select_account_name_account_name.desc()
            .localized(),
        isError = uiState.accountName.text.isNotEmpty() && uiState.validationResult.isValid.not(),
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentSize()
            ) {
                if (uiState.accountName.text.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Colors.alto, shape = CircleShape)
                            .clickable { onAccountNameChange(TextFieldValue()) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            MangalaWalletPack.Clear,
                            contentDescription = "Clear",
                            tint = Colors.white,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    HorizontalSpacer(Spacing.XSMALL)
                }
            }
        },
        interactionSource = interactionSource,
        visualTransformation = if (uiState.accountNameSuffix != null) {
            SuffixTransformation(uiState.accountNameSuffix)
        } else {
            VisualTransformation.None
        },
    )
}

@Composable
fun ValidationChecklist(uiState: Step2SelectAccountNameUiState.Ready) {
    val validationResult = uiState.validationResult

    MaxWidthColumn(
        Modifier
            .clip(RoundedCornerShape(CornerRadius.Medium))
            .background(Colors.white)
            .padding(
                horizontal = Dimensions.Padding.default,
                vertical = Dimensions.Padding.small
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
    ) {
        val conditions = if (validationResult is AccountCharacterValidationResult.PremiumAccount) {
            listOf(
                MR.strings.label_step_2_select_account_name_premium_account_length to validationResult.isValidLength,
                MR.strings.label_step_2_select_account_name_valid_characters to validationResult.containsOnlyValidCharacters,
                MR.strings.label_step_2_select_account_name_account_name_start_end to validationResult.startsAndEndsCorrectly
            )
        } else {
            listOf(
                MR.strings.label_step_2_select_account_name_standard_account_length to validationResult.isValidLength,
                MR.strings.label_step_2_select_account_name_valid_characters to validationResult.containsOnlyValidCharacters,
                MR.strings.label_step_2_select_account_name_account_name_start_end to validationResult.startsAndEndsCorrectly,
            )
        }

        conditions.forEach { (condition, isValid) ->
            ValidationItem(isValid, condition.desc().localized())
        }
        if (uiState.isCheckingAccountExistence) {
            TextDescription2(
                MR.strings.label_step_2_select_account_name_checking_existence.desc()
                    .localized(),
                color = Colors.darkDarkGray,
            )
        } else if (uiState.isAccountNotTaken != null) {
            ValidationItem(
                uiState.isAccountNotTaken == true,
                MR.strings.label_step_2_select_account_name_name_not_taken.desc().localized()
            )
        }
    }
}

@Composable
private fun ValidationItem(isValid: Boolean, condition: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(
                    if (isValid) Colors.lightGreen else Colors.pink,
                    shape = CircleShape
                )
                .padding(3.dp)
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = if (isValid) Icons.Filled.Check else Icons.Filled.Clear,
                contentDescription = if (isValid) "Condition met" else "Condition not met",
                tint = if (isValid) Colors.second else Colors.deepCrimson
            )
        }
        HorizontalSpacer(Spacing.XTINY)
        TextDescription2(
            condition,
            color = Colors.darkDarkGray,
            modifier = Modifier.weight(1f)
        )
    }
}