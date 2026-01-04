package com.mangala.wallet.features.send.presentation.step4.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.AddressConfirmationItem
import com.mangala.wallet.ui.ConfirmationItem
import com.mangala.wallet.ui.ConfirmationLocalItem
import com.mangala.wallet.ui.component.MaxWidthColumn
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun ConfirmationItems(
    blockchainType: BlockchainType,
    recipientName: String,
    contactAddress: String,
    addressCompact: String?,
    assetLogoUrl: String,
    amount: String,
    symbol: String,
    fiatValue: String,
    addressConfirmationLabel: String,
    isNetworkConfirmed: Boolean,
    onUpdateNetworkConfirmed: () -> Unit,
    isAddressConfirmed: Boolean,
    onUpdateAddressConfirmed: () -> Unit,
    isAmountConfirmed: Boolean,
    onUpdateAmountConfirmed: () -> Unit,
    additionalItems: @Composable () -> Unit = {}
) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)) {
        ConfirmationLocalItem(
            imageUrl = blockchainType.localImage,
            label = MR.strings.label_verify_transaction_check_network.desc()
                .localized(),
            value =  blockchainType.name,
            isChecked = isNetworkConfirmed,
            onClick = { onUpdateNetworkConfirmed() }
        )
        AddressConfirmationItem(
            address = contactAddress,
            label = addressConfirmationLabel,
            value = recipientName,
            subtitleValue = addressCompact.orEmpty(),
            isChecked = isAddressConfirmed,
            onClick = { onUpdateAddressConfirmed() },
        )
        ConfirmationItem(
            imageUrl = assetLogoUrl,
            label = MR.strings.label_verify_transaction_check_amount.desc()
                .localized(),
            value = "$amount $symbol",
            subtitleValue = fiatValue,
            isChecked = isAmountConfirmed,
            onClick = { onUpdateAmountConfirmed() }
        )
        additionalItems()
    }
}