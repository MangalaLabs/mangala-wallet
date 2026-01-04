package com.mangala.wallet.features.send.presentation.step4.antelope

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.AddressConfirmationItem
import com.mangala.wallet.ui.ConfirmationItem
import com.mangala.wallet.ui.ConfirmationLocalItem
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun AntelopeConfirmationItems(
    blockchainType: BlockchainType,
    recipientName: String,
    contactAddress: String,
    addressCompact: String?,
    assetLogoUrl: String,
    amount: String,
    symbol: String,
    fiatValue: String,
    memo: String,
    isNetworkConfirmed: Boolean,
    onUpdateNetworkConfirmed: () -> Unit,
    isAddressConfirmed: Boolean,
    onUpdateAddressConfirmed: () -> Unit,
    isAmountConfirmed: Boolean,
    onUpdateAmountConfirmed: () -> Unit,
    isMemoConfirmed: Boolean,
    onUpdateMemoConfirmed: () -> Unit
) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)) {
        ConfirmationLocalItem(
            imageUrl = blockchainType.localImage,
            label = MR.strings.label_verify_transaction_check_network.desc()
                .localized(),
            value = blockchainType.name,
            isChecked = isNetworkConfirmed,
            onClick = onUpdateNetworkConfirmed
        )

        AddressConfirmationItem(
            address = contactAddress,
            label = MR.strings.label_verify_transaction_check_account.desc()
                .localized(),
            value = recipientName,
            subtitleValue = addressCompact.orEmpty(),
            isChecked = isAddressConfirmed,
            onClick = onUpdateAddressConfirmed
        )

        when {
            symbol == "A" -> {
                ConfirmationLocalItem(
                    imageUrl = MR.images.vaulta,
                    label = MR.strings.label_verify_transaction_check_amount.desc()
                        .localized(),
                    value = "$amount $symbol",
                    subtitleValue = fiatValue,
                    isChecked = isAmountConfirmed,
                    onClick = onUpdateAmountConfirmed
                )
            }
            symbol == "V" && blockchainType.isEosNetwork() -> {
                ConfirmationLocalItem(
                    imageUrl = MR.images.vaultram,
                    label = MR.strings.label_verify_transaction_check_amount.desc()
                        .localized(),
                    value = "$amount $symbol",
                    subtitleValue = fiatValue,
                    isChecked = isAmountConfirmed,
                    onClick = onUpdateAmountConfirmed
                )
            }
            symbol == "EOS" -> {
                ConfirmationLocalItem(
                    imageUrl = MR.images.eos_new,
                    label = MR.strings.label_verify_transaction_check_amount.desc()
                        .localized(),
                    value = "$amount $symbol",
                    subtitleValue = fiatValue,
                    isChecked = isAmountConfirmed,
                    onClick = onUpdateAmountConfirmed
                )
            }
            else -> {
                ConfirmationItem(
                    imageUrl = assetLogoUrl,
                    label = MR.strings.label_verify_transaction_check_amount.desc()
                        .localized(),
                    value = "$amount $symbol",
                    subtitleValue = fiatValue,
                    isChecked = isAmountConfirmed,
                    onClick = onUpdateAmountConfirmed
                )
            }
        }

        ConfirmationItem(
            imageUrl = null,
            label = MR.strings.label_verify_transaction_check_memo.desc().localized(),
            value = memo.ifBlank { "No memo specified" },
            isChecked = isMemoConfirmed,
            onClick = onUpdateMemoConfirmed,
        )
    }
}