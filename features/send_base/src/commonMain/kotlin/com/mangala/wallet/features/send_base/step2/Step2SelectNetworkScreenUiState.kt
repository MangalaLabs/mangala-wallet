package com.mangala.wallet.features.send_base.step2

import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.RecipientValidationStatus

data class Step2SelectNetworkScreenUiState(
    val networks: List<BlockchainNetworkData>,
    val selectedNetwork: BlockchainNetworkData?,
    val recipientValidationStatus: RecipientValidationStatus
)