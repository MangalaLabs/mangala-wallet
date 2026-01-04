package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.ClearText
import com.mangala.wallet.features.addressbook.icon.contacticon.DeleteButton
import com.mangala.wallet.features.addressbook.presentation.components.ValidationColors
import com.mangala.wallet.features.addressbook.presentation.components.ValidationState
import com.mangala.wallet.features.addressbook.presentation.components.ValidationStateIndicator
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors


@Composable
fun AddressFieldRow(
    address: String,
    network: BlockchainTypeEntity,
    onNetworkSelected: (BlockchainTypeEntity) -> Unit,
    onAddressChange: (String) -> Unit,
    onRemoveAddress: () -> Unit,
    onPasteClick: () -> Unit,
    onScanQrClick: () -> Unit,
    // Figma sync: Hide delete when only 1 address
    showDeleteButton: Boolean = true,
    // Validation parameters
    validationError: String? = null,
    isValidating: Boolean = false,
    availableBlockchains: List<BlockchainTypeEntity> = emptyList(),
    // New parameter to control validation UI display
    showValidationUI: Boolean = true,
    // Focus state management for AC-12.3, AC-12.5
    isFocused: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    var localIsFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Determine validation state
    val validationState = when {
        isValidating -> ValidationState.VALIDATING
        validationError != null && validationError.contains(
            "WARNING:",
            ignoreCase = true
        ) -> ValidationState.WARNING

        validationError != null -> ValidationState.ERROR
        address.isNotBlank() && !isValidating && validationError == null -> ValidationState.VALID
        else -> ValidationState.IDLE
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Network icon
            NetworkIcon(
                selectedBlockchain = network,
                availableBlockchains = availableBlockchains,
                onBlockchainSelected = onNetworkSelected
            )
            
            // Address input field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                BasicTextField(
                    value = address,
                    onValueChange = { newValue ->
                        // Ensure we only pass the actual new value, not any stale state
                        // Trim trailing spaces to prevent issues
                        val trimmed = newValue.trimEnd()
                        if (trimmed != address) {
                            onAddressChange(trimmed)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { 
                            localIsFocused = it.isFocused
                            onFocusChanged(it.isFocused)
                        },
                    textStyle = MangalaTypography.Size14Regular().copy(
                        color = MaterialTheme.mangalaColors.textPrimary
                    ),
                    cursorBrush = SolidColor(MaterialTheme.mangalaColors.textLink),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box {
                            if (address.isEmpty()) {
                                Text(
                                    text = "Enter address",
                                    style = MangalaTypography.Size14Regular(),
                                    color = MaterialTheme.mangalaColors.textSecondary
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
            
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Show validation indicator for all states except IDLE and VALIDATING
                if (showValidationUI && validationState in listOf(ValidationState.ERROR, ValidationState.WARNING, ValidationState.VALID)) {
                    ValidationStateIndicator(
                        state = validationState,
                        message = null, // Only show icon, not message inline
                        modifier = Modifier.size(Spacing.XMEDIUM).padding(end = 4.dp)
                    )
                }
                
                // Paste button
                IconButton(
                    onClick = onPasteClick,
                    modifier = Modifier.size(Spacing.XBASE)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Paste from clipboard",
                        tint = MaterialTheme.mangalaColors.textLink,
                        modifier = Modifier.size(Spacing.XMEDIUM)
                    )
                }
                
                // AC-12.3, AC-12.5: Show X icon when focused, trash icon when not focused
                if (localIsFocused && address.isNotEmpty()) {
                    // Clear button when focused and has content
                    IconButton(
                        onClick = {
                            // AC-12.3: Clear text but keep field
                            onAddressChange("")
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = ContactIcon.ClearText,
                            contentDescription = "Clear text",
                            tint = Color.White,
                            modifier = Modifier
                                .size(Spacing.XMEDIUM)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color = MaterialTheme.mangalaColors.iconSecondary)
                                .padding(2.dp)
                        )
                    }
                } else if (!localIsFocused && showDeleteButton) {
                    // AC-12.5: Delete button when not focused and multiple addresses exist
                    IconButton(
                        onClick = {
                            onRemoveAddress()
                            keyboardController?.hide()
                        },
                        modifier = Modifier.size(Spacing.XBASE)
                    ) {
                        Icon(
                            imageVector = ContactIcon.DeleteButton,
                            contentDescription = "Remove wallet address",
                            tint = Color(0xFFFF3B30), // Exact red from Figma
                            modifier = Modifier.size(Spacing.XMEDIUM)
                        )
                    }
                }
            }
        }

        Divider(
            color = when {
                showValidationUI && validationState == ValidationState.ERROR -> 
                    ValidationColors.error.copy(alpha = if (localIsFocused) 0.5f else 0.3f)
                showValidationUI && validationState == ValidationState.WARNING -> 
                    ValidationColors.warning.copy(alpha = if (localIsFocused) 0.5f else 0.3f)
                showValidationUI && validationState == ValidationState.VALID -> 
                    ValidationColors.success.copy(alpha = if (localIsFocused) 0.3f else 0.2f)
                localIsFocused -> MaterialTheme.mangalaColors.textLink.copy(alpha = 0.3f)
                else -> MaterialTheme.mangalaColors.bgButton
            },
            thickness = if (localIsFocused || validationState in listOf(ValidationState.ERROR, ValidationState.WARNING)) 1.dp else 0.5.dp
        )

        // Show validation messages for both errors and warnings
        // Display regardless of focus state to ensure users don't miss important messages
        if (showValidationUI && validationError != null && validationState in listOf(ValidationState.ERROR, ValidationState.WARNING)) {
            ValidationStateIndicator(
                state = validationState,
                message = when {
                    // Handle different warning prefixes
                    validationError.startsWith("⚠️") -> validationError // Already has warning icon
                    validationError.contains("WARNING:", ignoreCase = true) -> 
                        validationError.replace("WARNING:", "").trim()
                    else -> validationError
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}