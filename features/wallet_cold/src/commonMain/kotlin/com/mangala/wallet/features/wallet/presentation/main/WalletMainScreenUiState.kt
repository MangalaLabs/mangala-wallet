package com.mangala.wallet.features.wallet.presentation.main

import androidx.compose.ui.graphics.Color
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.features.wallet.presentation.EvmAccountItemUiModel
import com.mangala.features.wallet.presentation.BaseWalletMainScreenDataUiState
import com.mangala.wallet.model.blockchain.BlockchainNetworkData

sealed class WalletMainScreenUiState {
    data object Loading : WalletMainScreenUiState()
    data class NoWallet(val networkSelected: BlockchainNetworkData?) : WalletMainScreenUiState()
    data class Data(
        override val fiatCurrencySymbol: String,
        override val accounts: List<EvmAccountItemUiModel>,
        val selectedAccountIndex: Int,
        val isBalanceVisible: Boolean,
        override val networkSelected: BlockchainNetworkData?,
    ) : WalletMainScreenUiState(), BaseWalletMainScreenDataUiState.BaseEvmDataState {
        val totalValue: BigDecimal? get() {
            val totalAccountValues = accounts.map { it.totalValue }.filterNotNull()
            if (totalAccountValues.isEmpty()) return null // Values hasn't finished loading/ error

            return totalAccountValues.reduce { acc, bigDecimal -> acc + bigDecimal }
        }
        val priceLoaded = totalValue != null
        val totalValueFormatted: String? by lazy {
            totalValue?.let {
                fiatCurrencySymbol + it.scale(PNL_DECIMAL_PLACES).toStringExpanded()
            }
        }

        val pnlColor: Color
            get() {
                return if (isBalanceVisible.not() || (totalValue != null && totalValue!! == BigDecimal.ZERO)) Colors.gray else if (totalValue != null && totalValue!! > BigDecimal.ZERO) Color(0xFF00A699) else Colors.coral // TODO: Refactor to common function
            }

        override val manageAccountButtonEnabled = accounts.isNotEmpty()
    }

    val selectedNetworkName
        get() = run {
            (this as? Data)?.networkSelected?.name
                ?: (this as? NoWallet)?.networkSelected?.name.orEmpty()
        }
}

private const val PNL_DECIMAL_PLACES = 2L