package com.mangala.wallet.features.portfolio.presentation

import androidx.compose.ui.graphics.Color
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.features.wallet.presentation.AntelopeAccountItemUiModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.token.domain.totalValue
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.ui.utils.PNL_DECIMAL_PLACES
import com.mangala.wallet.utils.ext.formatFiat

sealed interface PortfolioScreenUiModel{
    val isBalanceVisible: Boolean
    val pnlColor: Color
    val hideZeroBalances: Boolean
    val tokenQuery : String
        get() = ""
    val isLoading: Boolean
        get() = false
    val networkSelected: BlockchainNetworkData?

    data class Evm(
        val accountName: String = "",
        val tokenBalances: List<TokenBalanceModel> = emptyList(),
        override val hideZeroBalances: Boolean  = false,
        override val tokenQuery: String = "",
        val currencySymbol: String = "",
        override val isBalanceVisible: Boolean = true,
        override val isLoading: Boolean = false,
        override val networkSelected: BlockchainNetworkData?,
    ): PortfolioScreenUiModel {
        val totalValueFormatted by lazy {
            val value = tokenBalances.totalValue() ?: BigDecimal.ZERO
            value.formatFiat(currencySymbol, PNL_DECIMAL_PLACES)
        }
        val pnl: BigDecimal by lazy {
            var pnlValue = BigDecimal.ZERO
            tokenBalances.forEach {
                if (it.pnl != null) {
                    pnlValue += it.pnl ?: BigDecimal.ZERO
                }
            }
            pnlValue
        }
        override val pnlColor: Color get() {
            return if (isBalanceVisible.not()) Colors.gray else if (pnl > BigDecimal.ZERO) Color(0xFF00A699) else Colors.coral
        }
        val filteredTokenBalances by lazy {
            var filteredBalances = tokenBalances

            if (tokenQuery.isNotEmpty()) {
                filteredBalances = filteredBalances.filter {
                    it.contractSymbol.contains(tokenQuery, ignoreCase = true)
                        || it.contractName.contains(tokenQuery, ignoreCase = true)
                }
            }

            if (hideZeroBalances) {
                filteredBalances = filteredBalances.filter { it.balance.toDouble() > 0 }
            }

            filteredBalances.sortedByDescending { it.todaysValue }
        }

        val formattedPnl: String by lazy {
            var pnlValue = BigDecimal.ZERO
            var totalOldValue = BigDecimal.ZERO
            var totalNewValue = BigDecimal.ZERO
            tokenBalances.forEach {
                if (it.pnl != null && it.yesterdaysValue != null && it.todaysValue != null) {
                    pnlValue += it.pnl ?: BigDecimal.ZERO
                    totalOldValue += it.yesterdaysValue ?: BigDecimal.ZERO
                    totalNewValue += it.todaysValue ?: BigDecimal.ZERO
                }
            }
            val pnlSign = if (pnlValue > BigDecimal.ZERO) "+" else ""
            val pnlPercentage = if (totalOldValue != BigDecimal.ZERO) {
                pnlValue.divide(totalOldValue, DecimalMode(10, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 10)).multiply(BigDecimal.parseString("100"))
            } else BigDecimal.ZERO
            pnlSign + pnlValue.scale(PNL_DECIMAL_PLACES).toStringExpanded() + currencySymbol + "/" + pnlSign + pnlPercentage.scale(
                PNL_DECIMAL_PLACES
            ).toStringExpanded() + " %"
        }
    }
    data class Antelope(
        val accountName: String? = null,
        override val hideZeroBalances: Boolean = false,
        override val tokenQuery: String = "",
        val fiatCurrencySymbol: String  = "",
        val account: AntelopeAccountItemUiModel? = null,
        override val networkSelected: BlockchainNetworkData? = null,
        val selectedAccountIndex: Int  = 0,
        override val isBalanceVisible: Boolean = true,
        override val isLoading: Boolean = false
    ): PortfolioScreenUiModel {

        //        val pnl: BigDecimal by lazy {
//            var pnlValue = BigDecimal.ZERO
//            tokenBalances.forEach {
//                if (it.pnl != null) {
//                    pnlValue += it.pnl ?: BigDecimal.ZERO
//                }
//            }
//            pnlValue
//        }
//        val pnlColor: Color
//            get() {
//            return if (isBalanceVisible.not()) Colors.gray else if (10.0 > BigDecimal.ZERO) Color(0xFF00A699) else Colors.coral
//        }
        override val pnlColor: Color
            get() {
                return Colors.coral
        }

        val filteredTokenBalances by lazy {
            var filteredBalances = account?.assets

            if (tokenQuery.isNotEmpty()) {
                filteredBalances = filteredBalances?.filter {
                    it.name.contains(tokenQuery, ignoreCase = true)
                        || it.symbol.contains(tokenQuery, ignoreCase = true)
                }
            }

            if (hideZeroBalances) {
                filteredBalances = filteredBalances?.filter { (it.balance ?: BigDecimal.ZERO) > 0 }
            }

            filteredBalances?.sortedByDescending { it.formattedPrice }
        }

        val totalValue: BigDecimal? get() {
            // TODO: Handle
            return null
//            val totalAccountValues = accounts.map { it.totalValue }.filterNotNull()
//            if (totalAccountValues.isEmpty()) return null // Values hasn't finished loading/ error
//
//            return totalAccountValues.reduce { acc, bigDecimal -> acc + bigDecimal }
        }
        val priceLoaded = totalValue != null
//        val manageAccountButtonEnabled: Boolean = priceLoaded
    }
    
    data class Bitcoin(
        val accountName: String = "",
        val tokenBalances: List<TokenBalanceModel> = emptyList(),
        override val hideZeroBalances: Boolean = false,
        override val tokenQuery: String = "",
        val currencySymbol: String = "",
        override val isBalanceVisible: Boolean = true,
        override val isLoading: Boolean = false,
        override val networkSelected: BlockchainNetworkData?,
    ): PortfolioScreenUiModel {
        val totalValueFormatted by lazy {
            val value = tokenBalances.totalValue() ?: BigDecimal.ZERO
            value.formatFiat(currencySymbol, PNL_DECIMAL_PLACES)
        }
        
        val pnl: BigDecimal by lazy {
            var pnlValue = BigDecimal.ZERO
            tokenBalances.forEach {
                if (it.pnl != null) {
                    pnlValue += it.pnl ?: BigDecimal.ZERO
                }
            }
            pnlValue
        }
        
        override val pnlColor: Color get() {
            return if (isBalanceVisible.not()) Colors.gray else if (pnl > BigDecimal.ZERO) Color(0xFF00A699) else Colors.coral
        }
        
        val filteredTokenBalances by lazy {
            var filteredBalances = tokenBalances

            if (tokenQuery.isNotEmpty()) {
                filteredBalances = filteredBalances.filter {
                    it.contractSymbol.contains(tokenQuery, ignoreCase = true)
                        || it.contractName.contains(tokenQuery, ignoreCase = true)
                }
            }

            if (hideZeroBalances) {
                filteredBalances = filteredBalances.filter { it.balance.toDouble() > 0 }
            }

            filteredBalances.sortedByDescending { it.todaysValue }
        }

        val formattedPnl: String by lazy {
            var pnlValue = BigDecimal.ZERO
            var totalOldValue = BigDecimal.ZERO
            var totalNewValue = BigDecimal.ZERO
            tokenBalances.forEach {
                if (it.pnl != null && it.yesterdaysValue != null && it.todaysValue != null) {
                    pnlValue += it.pnl ?: BigDecimal.ZERO
                    totalOldValue += it.yesterdaysValue ?: BigDecimal.ZERO
                    totalNewValue += it.todaysValue ?: BigDecimal.ZERO
                }
            }
            val pnlSign = if (pnlValue > BigDecimal.ZERO) "+" else ""
            val pnlPercentage = if (totalOldValue != BigDecimal.ZERO) {
                pnlValue.divide(totalOldValue, DecimalMode(10, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 10)).multiply(BigDecimal.parseString("100"))
            } else BigDecimal.ZERO
            pnlSign + pnlValue.scale(PNL_DECIMAL_PLACES).toStringExpanded() + currencySymbol + "/" + pnlSign + pnlPercentage.scale(
                PNL_DECIMAL_PLACES
            ).toStringExpanded() + " %"
        }
    }
}