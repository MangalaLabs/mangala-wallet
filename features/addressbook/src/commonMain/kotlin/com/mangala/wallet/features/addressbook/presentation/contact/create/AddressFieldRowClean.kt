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
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * Clean address field row matching Figma design without visible validation UI
 * Validation still runs in background but UI remains clean
 */
@Composable
fun AddressFieldRowClean(
    address: String,
    network: BlockchainTypeEntity,
    onNetworkSelected: (BlockchainTypeEntity) -> Unit,
    onAddressChange: (String) -> Unit,
    onRemoveAddress: () -> Unit,
    onPasteClick: () -> Unit,
    onScanQrClick: () -> Unit = {},
    showDeleteButton: Boolean = true,
    availableBlockchains: List<BlockchainTypeEntity> = emptyList(),
    // Optional: Pass validation state if you want to track it without showing UI
    onValidationStateChange: ((isValid: Boolean, error: String?) -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

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
                    onValueChange = onAddressChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused },
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
                // Paste button
                IconButton(
                    onClick = onPasteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Paste",
                        tint = MaterialTheme.mangalaColors.textLink,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Delete button (only when multiple addresses)
                if (showDeleteButton) {
                    IconButton(
                        onClick = {
                            onRemoveAddress()
                            keyboardController?.hide()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = ContactIcon.DeleteButton,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF3B30),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Simple divider without validation colors
        Divider(
            color = MaterialTheme.mangalaColors.bg,
            thickness = 0.5.dp
        )
    }
}

/**
 * Minimal version with just the essentials
 */
@Composable
fun AddressFieldMinimal(
    address: String,
    network: BlockchainTypeEntity,
    onNetworkSelected: (BlockchainTypeEntity) -> Unit,
    onAddressChange: (String) -> Unit,
    onPasteClick: () -> Unit,
    modifier: Modifier = Modifier,
    availableBlockchains: List<BlockchainTypeEntity> = emptyList(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Network icon
        NetworkIcon(
            selectedBlockchain = network,
            availableBlockchains = availableBlockchains,
            onBlockchainSelected = onNetworkSelected
        )

        // Address field
        BasicTextField(
            value = address,
            onValueChange = onAddressChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
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

        // Paste icon
        IconButton(
            onClick = onPasteClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentPaste,
                contentDescription = "Paste",
                tint = MaterialTheme.mangalaColors.textLink,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}