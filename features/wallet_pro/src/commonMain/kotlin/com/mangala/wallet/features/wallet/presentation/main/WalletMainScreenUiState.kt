package com.mangala.wallet.features.wallet.presentation.main

import androidx.compose.ui.graphics.Color
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.features.wallet.presentation.AntelopeAccountItemUiModel
import com.mangala.features.wallet.presentation.AntelopeAssetsUiModel
import com.mangala.features.wallet.presentation.EvmAccountItemUiModel
import com.mangala.features.wallet.presentation.BaseWalletMainScreenDataUiState
import com.mangala.features.wallet.presentation.BitcoinAccountItemUiModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.common.mokoresources.Colors

sealed class WalletMainScreenUiState {
    val selectedNetwork
        get() = run {
            (this as? BaseWalletMainScreenDataUiState<*>)?.networkSelected
                ?: (this as? NoWallet)?.networkSelected
        }

    data object Loading : WalletMainScreenUiState()
    data class NoWallet(val networkSelected: BlockchainNetworkData?) : WalletMainScreenUiState()
    data class EvmData(
        override val fiatCurrencySymbol: String,
        override val accounts: List<EvmAccountItemUiModel>,
        override val selectedAccountIndex: Int,
        override val isBalanceVisible: Boolean,
        override val networkSelected: BlockchainNetworkData?,
    ) : WalletMainScreenUiState(), BaseWalletMainScreenDataUiState.BaseEvmDataState {
        override val selectedAccount = accounts.getOrNull(selectedAccountIndex)
        val totalValue: BigDecimal? get() {
            val totalAccountValues = accounts.map { it.totalValue }.filterNotNull()
            if (totalAccountValues.isEmpty()) return null // Values hasn't finished loading/ error

            return totalAccountValues.reduce { acc, bigDecimal -> acc + bigDecimal }
        }
        private val priceLoaded = totalValue != null
        val totalValueFormatted: String? by lazy {
            totalValue?.let {
                fiatCurrencySymbol + it.scale(PNL_DECIMAL_PLACES).toStringExpanded()
            }
        }
        val placeholderAssetsList =
            if (accounts.isEmpty() || selectedAccount == null) emptyList() else List(3) {
                TokenBalanceModel(
                    tokenId = it.toLong(),
                    accountId = "placeholder",
                    totalAmount = 0.0,
                    balance = "0",
                    balance24h = "0",
                    balanceLocked = "0",
                    orderNumber = it,
                    contractDecimals = 0,
                    contractName = "",
                    contractSymbol = "",
                    contractAddress = "",
                    logoUrl = "",
                    localImage = null,
                    coinUid = "placeholder$it",
                    currencyCode = "",
                    currentPrice = null,
                    marketCap = null,
                    marketCapRank = null,
                    totalVolume = null,
                    high24h = null,
                    low24h = null,
                    priceChange24h = null,
                    priceChangePercentage24h = null,
                    priceChangePercentage7d = null,
                    marketCapChange24h = null,
                    marketCapChangePercentage24h = null,
                    sparklineIn7d = null
                )
            }

        val pnlColor: Color
            get() {
                return if (isBalanceVisible.not() || (totalValue != null && totalValue!! == BigDecimal.ZERO)) Colors.gray else if (totalValue != null && totalValue!! > BigDecimal.ZERO) Color(0xFF00A699) else Colors.coral // TODO: Refactor to common function
            }

        override val manageAccountButtonEnabled: Boolean = priceLoaded
    }
    data class AntelopeData(
        override val fiatCurrencySymbol: String,
        override val accounts: List<AntelopeAccountItemUiModel>,
        override val networkSelected: BlockchainNetworkData?,
        override val selectedAccountIndex: Int,
        override val isBalanceVisible: Boolean,
    ): WalletMainScreenUiState(), BaseWalletMainScreenDataUiState.BaseAntelopeDataState {
        override val selectedAccount = accounts.getOrNull(selectedAccountIndex)
        val totalValue: BigDecimal? get() {
            // TODO: Handle
            return null
//            val totalAccountValues = accounts.map { it.totalValue }.filterNotNull()
//            if (totalAccountValues.isEmpty()) return null // Values hasn't finished loading/ error
//
//            return totalAccountValues.reduce { acc, bigDecimal -> acc + bigDecimal }
        }
        val priceLoaded = selectedAccount?.assets?.isNotEmpty() == true
        override val manageAccountButtonEnabled: Boolean = priceLoaded
        val placeholderAssetList =
            if (accounts.isEmpty() || selectedAccount == null) emptyList() else listOf(
            AntelopeAssetsUiModel.RamBalanceUiModel(
                "placeholder",
                emptyList(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "",
                "",
                null
            )
        ) + List(3) {
            AntelopeAssetsUiModel.TokenBalanceUiModel(
                "placeholder$it",
                null,
                "",
                "",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                BigDecimal.ZERO,
                null,
                ""
            )
        }
    }

    data class BitcoinData(
        override val fiatCurrencySymbol: String,
        override val accounts: List<BitcoinAccountItemUiModel>,
        override val networkSelected: BlockchainNetworkData?,
        override val selectedAccountIndex: Int,
        override val isBalanceVisible: Boolean,
    ): WalletMainScreenUiState(), BaseWalletMainScreenDataUiState.BaseBitcoinDataState {
        override val selectedAccount = accounts.getOrNull(selectedAccountIndex)
        private val priceLoaded = selectedAccount?.balanceModel?.balance?.isNotEmpty() == true
        override val manageAccountButtonEnabled: Boolean = priceLoaded
    }
}

private const val PNL_DECIMAL_PLACES = 2L