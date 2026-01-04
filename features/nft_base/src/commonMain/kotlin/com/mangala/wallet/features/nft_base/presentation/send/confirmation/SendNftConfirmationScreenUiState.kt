package com.mangala.wallet.features.nft_base.presentation.send.confirmation

import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.utils.formattedAddress

sealed interface SendNftConfirmationScreenUiState {
    data object Loading : SendNftConfirmationScreenUiState
    data class Data(
        val account: AccountModel,
        val contact: ContactEntity?,
        val nft: NftCollection,
        val blockchainType: BlockchainType,
        val estimatedGasLimit: Long?,
        val gasPrice: GasPrice?,
        val txHash: String?,
        val transactionFeeOptions: List<EvmFeeOptionUiModel>,
        val selectedTransactionFee: EvmFeeOptionUiModel?,
        val estimateGasErrorVisible: Boolean,
        val recipientAddress: String
    ) : SendNftConfirmationScreenUiState {
        private val nftItem = nft.nft.firstOrNull()
        val recipient = contact?.name ?: recipientAddress
        val nftName = nftItem?.name?.ifBlank { nft.contractName }.orEmpty()
        val nftId = "#${nftItem?.tokenId.orEmpty()}"
        val nftUrl = nftItem?.tokenUrl
        val addressCompact = contact?.address?.formattedAddress(
            leadingCharsCount = 10,
            trailingCharsCount = 10
        )
    }
    data class Error(val message: String) : SendNftConfirmationScreenUiState
}