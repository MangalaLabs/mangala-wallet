package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.presentation.contact.model.WalletAddressUiState
import com.mangala.wallet.features.addressbook.presentation.contact.validation.ValidationLoadingState
import com.mangala.wallet.features.addressbook.icon.contacticon.ClearText
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * Section containing wallet addresses with their network icons and alias input
 * Updated to match the Figma design
 */
@Composable
fun WalletAddressSection(
    addresses: List<Pair<String, BlockchainTypeEntity>>, // (address, network) pairs
    walletAddresses: List<WalletAddressUiState> = emptyList(), // Full wallet states with validation
    addressTypes: Map<Int, String?>, // Index to address type mapping
    onAddressChange: (Int, String) -> Unit,
    onNetworkChange: (Int, BlockchainTypeEntity) -> Unit, // Callback for network selection
    onAddressTypeSelect: (Int, String) -> Unit,
    onAddAddress: () -> Unit,
    onRemoveAddress: (Int) -> Unit,
    onPasteClick: (Int) -> Unit,
    onScanQrClick: (Int) -> Unit = {},
    onAddCustomType: () -> Unit,
    onPrimaryAddressSelect: (Int) -> Unit,
    primaryAddressIndex: Int = 0,
    sensitiveFlagList: List<Boolean> = emptyList(),
    onSensitiveToggle: (Int, Boolean) -> Unit = { _, _ -> },
    // Blockchain entities from database instead of string networks for efficient data flow
    availableBlockchains: List<BlockchainTypeEntity> = emptyList(),
    // Focus state management for AC-12.3, AC-12.5, AC-12.6
    focusedFieldId: String? = null,
    onFocusChanged: (String?) -> Unit = {}
) {
    Column {
        Spacer(modifier = Modifier.height(Spacing.TINY))
        addresses.forEachIndexed { index, (address, network) ->
            Spacer(modifier = Modifier.height(Spacing.SMALL))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.mangalaColors.bgInnerCard,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Section header with Primary indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Text label for the section - only show number if multiple addresses
                        if (addresses.size > 1) {
                            Text(
                                text = "Address ${index + 1}",
                                style = MangalaTypography.Size14Medium(),
                                color = MaterialTheme.mangalaColors.textPrimary,
                            )
                        }

                        // Figma sync: Show "Set as default" only when ≥2 addresses
                        if (addresses.size > 1) {
                            ButtonSetPrimary(
                                isPrimary = index == primaryAddressIndex,
                                index = index,
                                onPrimaryAddressSelect
                            )
                        }
                    }

                    // Address field with network selection
                    val walletState = walletAddresses.getOrNull(index)
                    val walletId = walletState?.id ?: "wallet_$index"
                    val isFocused = focusedFieldId == walletId
                    
                    AddressFieldRow(
                        address = address,
                        network = network,
                        onNetworkSelected = { newNetwork ->
                            onNetworkChange(
                                index,
                                newNetwork
                            )
                        },
                        onAddressChange = { onAddressChange(index, it) },
                        onRemoveAddress = { onRemoveAddress(index) },
                        onPasteClick = { onPasteClick(index) },
                        onScanQrClick = { onScanQrClick(index) },
                        // AC-12.5, AC-12.6: Show delete button only when not focused and have multiple fields
                        showDeleteButton = addresses.size > 1 && !isFocused,
                        // Pass validation error from wallet state
                        validationError = walletState?.error,
                        isValidating = walletState?.validationState == ValidationLoadingState.VALIDATING,
                        availableBlockchains = availableBlockchains,
                        // Enable validation UI to show errors and warnings
                        showValidationUI = true,
                        isFocused = isFocused,
                        onFocusChanged = { focused ->
                            onFocusChanged(if (focused) walletId else null)
                        }
                    )

                    Spacer(modifier = Modifier.height(Spacing.TINY))

                    // Replace wallet type chips with alias input field
                    AliasInputField(
                        value = addressTypes[index] ?: "",
                        onValueChange = { alias -> onAddressTypeSelect(index, alias) },
                        placeholder = "Enter alias"
                    )

                    Spacer(modifier = Modifier.height(Spacing.TINY))

                    // Sensitive toggle switch
                    val isSensitive =
                        if (index < sensitiveFlagList.size) sensitiveFlagList[index] else false
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sensitive",
                            style = MangalaTypography.Size14Regular(),
                            color = MaterialTheme.mangalaColors.textSecondary,
                            modifier = Modifier.defaultMinSize(minHeight = 20.dp)
                        )

                        Box(
                            contentAlignment = Alignment.CenterEnd,
                            modifier = Modifier.width(36.dp).height(20.dp) // Fixed width for switch container
                        ) {
                            Switch(
                                checked = isSensitive,
                                modifier = Modifier.scale(
                                    scaleX = 0.692f, // Scale width from 52dp to 36dp
                                    scaleY = 0.692f  // Scale height from 32dp to 20dp
                                ),
                                onCheckedChange = { onSensitiveToggle(index, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.mangalaColors.textLink,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = MaterialTheme.mangalaColors.iconSecondary,
                                    checkedBorderColor = MaterialTheme.mangalaColors.textLink,
                                    uncheckedBorderColor = MaterialTheme.mangalaColors.iconSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        AddAnotherButton(
            text = "Add other address",
            onClick = onAddAddress,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}


// New component for the alias input field
@Composable
fun AliasInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    var isFocused by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth().padding(vertical = 12.dp), // Fixed height to prevent layout shift
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = MangalaTypography.Size14Regular().copy(
                    color = MaterialTheme.mangalaColors.textPrimary
                ),
                cursorBrush = SolidColor(MaterialTheme.mangalaColors.textPrimary),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MangalaTypography.Size14Regular(),
                                color = MaterialTheme.mangalaColors.textSecondary
                            )
                        }
                        innerTextField()
                    }
                }
            )

            // Clear button - Always reserve space to prevent layout shift
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = ContactIcon.ClearText,
                            contentDescription = "Clear text",
                            tint = Color.White,
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(color = MaterialTheme.mangalaColors.iconSecondary) // Reduced icon size
                                .padding(2.dp)// Clip the background
                        )
                    }
                }
            }
        }

        Divider(
            color = MaterialTheme.mangalaColors.bgButton,
            thickness = 0.5.dp
        )
    }
}

@Composable
fun ButtonSetPrimary(
    isPrimary: Boolean,
    index: Int,
    onPrimaryAddressSelect: (Int) -> Unit
) {
    val backgroundColor =
        if (isPrimary) MaterialTheme.mangalaColors.textPrimary else MaterialTheme.mangalaColors.bgInnerCard
    val textColor =
        if (isPrimary) MaterialTheme.mangalaColors.bg else MaterialTheme.mangalaColors.textSecondary
    val borderWidth = if (isPrimary) 0.dp else 1.dp
    val borderColor = if (isPrimary) Color.Transparent else MaterialTheme.mangalaColors.textSecondary

    val clickModifier = if (isPrimary)
        Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    else
        Modifier
            .clickable { onPrimaryAddressSelect(index) }
            .padding(horizontal = 4.dp, vertical = 2.dp)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(8.dp),
            )
            .background(backgroundColor)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        BasicText(
            text = "Set as default",
            style = MangalaTypography.Size12Medium().copy(
                color = textColor
            ),
            modifier = clickModifier
        )
    }
}
