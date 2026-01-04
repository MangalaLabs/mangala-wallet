package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowDownExpand
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProposalDropdownMenu(
    label: String,
    placeholder: String,
    value: String,
    items: List<String>,
    onValueChange: (String) -> Unit,
    requiredInput: Boolean = true
) {
    var dropdownOpen by remember { mutableStateOf(false) }
    val dropdownIconRotationState by animateFloatAsState(
        targetValue = if (dropdownOpen) 180f else 0f
    )

    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = dropdownOpen,
        onExpandedChange = { dropdownOpen = it }
    ) {
        ProposalTextInputField(
            label = label,
            query = value,
            onQueryChange = {},
            placeholder = placeholder,
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            trailingIcon = {
                Icon(
                    imageVector = MangalaWalletPack.ArrowDownExpand,
                    contentDescription = null,
                    tint = MaterialTheme.mangalaColors.iconPrimary,
                    modifier = Modifier.size(20.dp).rotate(dropdownIconRotationState)
                )
            },
            requiredInput = requiredInput
        )

        DropdownMenu(
            expanded = dropdownOpen,
            onDismissRequest = {
                dropdownOpen = false
            },
            modifier = Modifier
                .exposedDropdownSize()
                .background(color = MaterialTheme.mangalaColors.bgInnerCard)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(item)
                        dropdownOpen = false
                    },
                    contentPadding = PaddingValues(horizontal = Dimensions.Padding.default),
                ) {
                    Text(
                        text = item,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        style = MangalaTypography.Size13Medium()
                    )
                }
            }
        }
    }
}