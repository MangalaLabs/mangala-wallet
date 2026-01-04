package com.mangala.wallet.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Scan
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.BasicTextFieldWithHintAndTrailingIcons
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun SelectAddress(
    modifier: Modifier = Modifier,
    address: String?,
    recipientValidationStatus: RecipientValidationStatus,
    isAddressFocus: Boolean,
    addressFocusRequester: FocusRequester,
    onAddressChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onDoneAddress: () -> Unit,
    onClickScanQRCode: () -> Unit,
    onClickPaste: () -> Unit,
    networkType: NetworkType
) {
    SelectAddress(
        modifier = modifier,
        address = address,
        addressFocusRequester = addressFocusRequester,
        onAddressChange = onAddressChange,
        onFocusChanged = onFocusChanged,
        onDoneAddress = onDoneAddress,
        onNextAction = onDoneAddress,
        imeAction = ImeAction.Next,
        trailingIcon = {
            if (recipientValidationStatus is RecipientValidationStatus.Validating) {
                CircularProgressIndicator(Modifier.size(Dimensions.IconButtonSize))
            } else {
                if (address?.isNotEmpty() == true) {
                    if (isAddressFocus) {
                        IconButton(
                            onClick = { onAddressChange("") },
                            modifier = Modifier.size(Dimensions.IconButtonSize)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null,
                                tint = MaterialTheme.mangalaColors.iconPrimary,
                                modifier = Modifier.size(Dimensions.IconSize)
                            )
                        }
                    } else {
                        if (recipientValidationStatus is RecipientValidationStatus.Valid) {
                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(Dimensions.IconButtonSize)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = ColorsNew.success_500,
                                    modifier = Modifier.size(Dimensions.IconSize)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(Dimensions.IconButtonSize)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = ColorsNew.error_500,
                                    modifier = Modifier.size(Dimensions.IconSize)
                                )
                            }
                        }
                    }
                } else {
                    Row {
                        IconButton(
                            onClick = { onClickScanQRCode() },
                            modifier = Modifier.size(Dimensions.IconButtonSize)
                        ) {
                            Icon(
                                MangalaWalletPack.Scan,
                                contentDescription = null,
                                tint = MaterialTheme.mangalaColors.iconPrimary,
                                modifier = Modifier.size(Dimensions.IconSize)
                            )
                        }
                        IconButton(
                            onClick = { onClickPaste() },
                            modifier = Modifier.size(Dimensions.IconButtonSize)
                        ) {
                            Icon(
                                MangalaWalletPack.Copy,
                                contentDescription = null,
                                tint = MaterialTheme.mangalaColors.iconPrimary,
                                modifier = Modifier.size(Dimensions.IconSize)
                            )
                        }
                    }
                }
            }
        },
        networkType = networkType
    )
}

@Composable
fun SelectAddress(
    modifier: Modifier = Modifier,
    address: String?,
    isValidAddress: Boolean,
    isAddressFocus: Boolean,
    addressFocusRequester: FocusRequester,
    onAddressChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onDoneAddress: () -> Unit,
    onClickScanQRCode: () -> Unit,
    onClickPaste: () -> Unit,
    networkType: NetworkType
) {
    SelectAddress(
        modifier = modifier,
        address = address,
        addressFocusRequester = addressFocusRequester,
        onAddressChange = onAddressChange,
        onFocusChanged = onFocusChanged,
        onDoneAddress = onDoneAddress,
        onNextAction = {},
        imeAction = ImeAction.Done,
        trailingIcon = {
            if (address?.isNotEmpty() == true) {
                if (isAddressFocus) {
                    IconButton(
                        onClick = { onAddressChange("") },
                        modifier = Modifier.size(Dimensions.IconButtonSize)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = null,
                            tint = MaterialTheme.mangalaColors.iconPrimary,
                            modifier = Modifier.size(Dimensions.IconSize)
                        )
                    }
                } else {
                    if (isValidAddress) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(Dimensions.IconButtonSize)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = ColorsNew.success_500,
                                modifier = Modifier.size(Dimensions.IconSize)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(Dimensions.IconButtonSize)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = ColorsNew.error_500,
                                modifier = Modifier.size(Dimensions.IconSize)
                            )
                        }
                    }
                }

            } else {
                Row {
                    IconButton(
                        onClick = { onClickScanQRCode() },
                        modifier = Modifier.size(Dimensions.IconButtonSize)
                    ) {
                        Icon(
                            MangalaWalletPack.Scan,
                            contentDescription = null,
                            tint = MaterialTheme.mangalaColors.iconPrimary,
                            modifier = Modifier.size(Dimensions.IconSize)
                        )
                    }
                    IconButton(
                        onClick = { onClickPaste() },
                        modifier = Modifier.size(Dimensions.IconButtonSize)
                    ) {
                        Icon(
                            MangalaWalletPack.Copy,
                            contentDescription = null,
                            tint = MaterialTheme.mangalaColors.iconPrimary,
                            modifier = Modifier.size(Dimensions.IconSize)
                        )
                    }
                }
            }
        },
        networkType = networkType
    )
}

@Composable
fun SelectAddress(
    modifier: Modifier = Modifier,
    address: String?,
    addressFocusRequester: FocusRequester,
    onAddressChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onDoneAddress: () -> Unit,
    onNextAction: () -> Unit,
    imeAction: ImeAction,
    trailingIcon: @Composable () -> Unit,
    networkType: NetworkType
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {

        TextNormal(
            MR.strings.message_select_address_to.desc().localized(),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.mangalaColors.textPrimary
        )
        HorizontalSpacer(Spacing.TINY)
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(primary = MaterialTheme.mangalaColors.iconPrimary)
        ) {
            BasicTextFieldWithHintAndTrailingIcons(
                address.orEmpty(),
                onValueChange = {
                    onAddressChange(it)
                },
                hint = (if (networkType == NetworkType.ANTELOPE) MR.strings.hint_select_address_account else MR.strings.hint_select_address_address)
                    .desc()
                    .localized(),
                hintColor = MaterialTheme.mangalaColors.textSecondary,
                fontWeight = FontWeight.Medium,
                fontSize = FontType.REGULAR,
                textColor = MaterialTheme.mangalaColors.textLink,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onDoneAddress()
                    },
                    onNext = {
                        onNextAction()
                    }
                ),
                singleLine = false,
                textFieldModifier = Modifier
                    .onFocusChanged { focusState ->
                        onFocusChanged(focusState.isFocused)
                    }
                    .fillMaxWidth()
//                                    .border(2.dp, borderColor, MaterialTheme.shapes.small)
                    .focusRequester(addressFocusRequester),
                boxModifier = Modifier.fillMaxWidth().padding(vertical = Spacing.SMALL),
                trailingIcon = trailingIcon
            )
        }
    }
}

sealed interface RecipientValidationStatus {
    data object NotValidated : RecipientValidationStatus
    data object Validating : RecipientValidationStatus
    data object Valid : RecipientValidationStatus
    data object Invalid : RecipientValidationStatus
}