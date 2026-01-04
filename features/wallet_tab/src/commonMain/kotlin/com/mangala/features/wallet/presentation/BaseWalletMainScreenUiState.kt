package com.mangala.features.wallet.presentation

import com.mangala.wallet.model.blockchain.BlockchainNetworkData

// Make sure to handle is checks if you add or remove subclasses of this interface
sealed interface BaseWalletMainScreenDataUiState<AccountType> {
    val networkSelected: BlockchainNetworkData?
    val fiatCurrencySymbol: String
    val isBalanceVisible: Boolean
    val manageAccountButtonEnabled: Boolean
    val accounts: List<AccountType>
    val selectedAccountIndex: Int

    interface BaseEvmDataState: BaseWalletMainScreenDataUiState<EvmAccountItemUiModel> {
        override val accounts: List<EvmAccountItemUiModel>
        val selectedAccount: EvmAccountItemUiModel?
    }

    interface BaseAntelopeDataState: BaseWalletMainScreenDataUiState<AntelopeAccountItemUiModel> {
        override val accounts: List<AntelopeAccountItemUiModel>
        val selectedAccount: AntelopeAccountItemUiModel?
    }

    interface BaseBitcoinDataState: BaseWalletMainScreenDataUiState<BitcoinAccountItemUiModel> {
        override val accounts: List<BitcoinAccountItemUiModel>
        val selectedAccount: BitcoinAccountItemUiModel?
    }
}
