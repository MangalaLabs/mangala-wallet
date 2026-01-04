package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.Text
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.component.MangalaWalletSearchBarDefaults
import com.mangala.wallet.ui.component.MangalaWalletSearchBarWithBorder
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun ProposalTextInputField(
    label: String,
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    requiredInput: Boolean = true,
    enabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    ),
    isError: Boolean = false,
    errorText: String? = null,
    containerModifier: Modifier = Modifier.fillMaxWidth()
) {
    ProposalTextInputField(
        query = query,
        onQueryChange = onQueryChange,
        placeholder = placeholder,
        enabled = enabled,
        label = {
            ProposalInputLabel(label, requiredInput)
        },
        trailingIcon = trailingIcon,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        isError = isError,
        errorText = errorText,
        containerModifier = containerModifier
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProposalTextInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean = true,
    label: @Composable () -> Unit = {},
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    ),
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    containerModifier: Modifier = Modifier.fillMaxWidth()
) {
    val trailingIconWrapped = @Composable {
        if (trailingIcon != null) {
            CompositionLocalProvider(
                LocalMinimumInteractiveComponentEnforcement provides false,
                LocalMinimumInteractiveComponentSize provides 0.dp
            ) {
                trailingIcon()
            }
        }
    }

    Column(modifier = containerModifier, verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        label()
        MangalaWalletSearchBarWithBorder(
            query = query,
            placeholder = placeholder,
            onQueryChange = onQueryChange,
            leadingIcon = null,
            enabled = enabled,
            trailingIcon = if (trailingIcon == null) null else trailingIconWrapped,
            modifier = modifier.then(Modifier.defaultMinSize(minHeight = 48.dp)),
            colors = MangalaWalletSearchBarDefaults.searchBarColors(
                textColor = MaterialTheme.mangalaColors.textPrimary,
                focusedBorderColor = MaterialTheme.mangalaColors.border,
                unfocusedBorderColor = MaterialTheme.mangalaColors.border,
                backgroundColor = MaterialTheme.mangalaColors.bgInnerCard,
                placeholderColor = MaterialTheme.mangalaColors.textSecondary,
            ),
            keyboardOptions = keyboardOptions
        )
        AnimatedVisibility(visible = isError && errorText != null) {
            Text(
                errorText.orEmpty(),
                color = ColorsNew.error_600,
                style = MangalaTypography.Size12Regular()
            )
        }
    }
}