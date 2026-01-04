package com.mangala.wallet.features.conversationui.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowSend
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowUp
import com.mangala.wallet.features.conversationui.presentation.model.InputMode
import com.mangala.wallet.utils.isQrCodeScanningSupported

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatInputArea(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onRecordAudio: () -> Unit = {},
    placeholder: String,
    modifier: Modifier = Modifier,
    inputMode: InputMode = InputMode.Normal,
    addressValidationState: com.mangala.wallet.features.conversationui.presentation.model.AddressValidationState? = null,
    amountValidationState: com.mangala.wallet.features.conversationui.presentation.model.AmountValidationState? = null,
    onScanQrCode: () -> Unit,
    onPasteFromClipboard: () -> Unit,
    focusRequester: FocusRequester? = null
) {
    

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Show validation error if in address mode and validation failed
        if (inputMode is InputMode.EnterAddress && addressValidationState?.isValid == false) {
            Text(
                text = addressValidationState.errorMessage ?: "Invalid address format",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        
        // Show validation error if in amount mode and validation failed
        if (inputMode is InputMode.EnterAmount && amountValidationState?.isValid == false) {
            Text(
                text = amountValidationState.errorMessage ?: "Invalid amount",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        
        // Action chips for address input mode
        if (inputMode is InputMode.EnterAddress) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Scan QR Code chip - only show if QR scanning is supported on the platform
                if (isQrCodeScanningSupported()) {
                    AssistChip(
                        onClick = onScanQrCode,
                        label = {
                            Text("Scan QR", fontSize = 12.sp, color = MaterialTheme.mangalaColors.textPrimary)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR Code",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.mangalaColors.iconPrimary
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.mangalaColors.border.copy(alpha = 0.3f),
                            labelColor = MaterialTheme.mangalaColors.textPrimary,
                            leadingIconContentColor = MaterialTheme.mangalaColors.iconPrimary
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.mangalaColors.border),
                        modifier = Modifier.height(32.dp)
                    )
                }
                
                // Paste from clipboard chip
                AssistChip(
                    onClick = onPasteFromClipboard,
                    label = {
                        Text("Paste", fontSize = 12.sp, color = MaterialTheme.mangalaColors.textPrimary)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste from clipboard",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.mangalaColors.iconPrimary
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.mangalaColors.border.copy(alpha = 0.3f),
                        labelColor = MaterialTheme.mangalaColors.textPrimary,
                        leadingIconContentColor = MaterialTheme.mangalaColors.iconPrimary
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.mangalaColors.border),
                    modifier = Modifier.height(32.dp)
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.dp,
                    color = when {
                        inputMode is InputMode.SelectNetwork -> MaterialTheme.mangalaColors.textLink.copy(alpha = 0.5f)
                        inputMode is InputMode.EnterAddress && addressValidationState?.isValid == false -> MaterialTheme.colorScheme.error
                        inputMode is InputMode.EnterAddress && addressValidationState?.isValid == true -> Color(0xFF4CAF50)
                        inputMode is InputMode.EnterAmount && amountValidationState?.isValid == false -> MaterialTheme.colorScheme.error
                        inputMode is InputMode.EnterAmount && amountValidationState?.isValid == true -> Color(0xFF4CAF50)
                        else -> MaterialTheme.mangalaColors.border
                    },
                    shape = RoundedCornerShape(20.dp)
                )
                .background(
                    color = MaterialTheme.mangalaColors.bgAlpha,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(end = 12.dp, bottom = 12.dp) // Padding start and top handled by TextField
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // TODO: Use BasicTextField. TextField has implicit start padding of 16dp, which makes the horizontal padding for the box uneven (end padding 12dp).
            TextField(
                value = messageText,
                onValueChange = onMessageTextChange,
                modifier = Modifier
                    .weight(1f)
                    .let { modifier ->
                        focusRequester?.let { modifier.focusRequester(it) } ?: modifier
                    },
                placeholder = {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.mangalaColors.textSecondary,
                        fontSize = 14.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.mangalaColors.textPrimary,
                    unfocusedTextColor = MaterialTheme.mangalaColors.textPrimary,
                    disabledTextColor = MaterialTheme.mangalaColors.textPrimary.copy(alpha = 0.5f),
                    cursorColor = MaterialTheme.mangalaColors.textLink
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = when (inputMode) {
                        is InputMode.EnterAmount -> KeyboardType.Decimal
                        else -> KeyboardType.Text
                    },
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (messageText.isNotBlank() && inputMode !is InputMode.SelectNetwork) {
                            onSendMessage(messageText)
                        }
                    }
                ),
                singleLine = false,
                maxLines = 5,
                enabled = inputMode !is InputMode.SelectNetwork
            )

            if (inputMode is InputMode.EnterAddress && addressValidationState?.isLoading == true) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 12.dp),
                    color = MaterialTheme.mangalaColors.textLink,
                    strokeWidth = 2.dp
                )
            }
        }
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentEnforcement provides false,
            LocalMinimumInteractiveComponentSize provides 0.dp,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.End)
            ) {
//                IconButton(
//                    onClick = onRecordAudio,
//                    modifier = Modifier
//                        .defaultMinSize(32.dp)
//                        .clip(CircleShape)
//                        .border(
//                            width = 1.dp,
//                            color = Color(0xFF2A3E6C),
//                            shape = CircleShape
//                        )
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Mic,
//                        contentDescription = "Record Audio",
//                        tint = Color(0xFFF1F5F9),
//                        modifier = Modifier.size(20.dp)
//                    )
//                }

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank() && inputMode !is InputMode.SelectNetwork) {
                            onSendMessage(messageText)
                        }
                    },
                    modifier = Modifier
                        .defaultMinSize(32.dp)
                        .clip(CircleShape)
                        .background(
                            brush = if (inputMode is InputMode.SelectNetwork) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.mangalaColors.textLink.copy(alpha = 0.3f),
                                        Color(0xFFC27DFF).copy(alpha = 0.3f)
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(100f, 100f)
                                )
                            } else {
                                MaterialTheme.mangalaColors.borderHighlight
                            }
                        ),
                    enabled = messageText.isNotBlank() && inputMode !is InputMode.SelectNetwork
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.ArrowSend,
                        contentDescription = "Send Message",
                        tint = when {
                            inputMode is InputMode.SelectNetwork -> Color(0x66FFFFFF)
                            messageText.isNotBlank() -> Color.White
                            else -> Color(0x66FFFFFF)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        }
    }
}