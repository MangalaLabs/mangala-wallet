package com.mangala.features.wallet.presentation

import androidx.compose.ui.graphics.Color
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.features.chains.bitcoin.domain.model.account.BitcoinAccount
import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.utils.ext.formatCompact

data class BitcoinAccountItemUiModel(
    val account: BitcoinAccount,
    override val isBalanceVisible: Boolean = true,
    override val currencySymbol: String = "$",
    val balanceBtc: BigDecimal? = null,
    val nativeCoinPrice: BigDecimal? = null,
    val nativeCoinPnl: BigDecimal? = null,
    val bitcoinIcon: ImageSource? = null,
    val balanceModel: TokenBalanceModel?,
    val isLoading: Boolean = false,
    val utxos: List<BitcoinUtxo> = emptyList()
): BaseAccountItemUiModel {
    
    val totalValue: BigDecimal? = balanceModel?.todaysValue
    
    val totalValuePlaceholderEnabled: Boolean = balanceModel == null

    val totalValueFormatted: String
        get() {
            if (totalValue == null) return "$0.00"
            return if (isBalanceVisible) currencySymbol + (totalValue.formatCompact())
                   else HIDDEN_BALANCE_STRING
        }
    
    override val formattedPnl: String?
        get() {
            if (!isBalanceVisible) return HIDDEN_BALANCE_STRING
            if (nativeCoinPnl == null) return null
            
            val pnlSign = if (nativeCoinPnl > BigDecimal.ZERO) "+" else ""
            return pnlSign + nativeCoinPnl.toStringExpanded() + " %"
        }
    
    val formattedPnlPlaceholderEnabled: Boolean
        get() = nativeCoinPnl == null || isLoading
    
    val pnlColor: Color
        get() {
            return if (isBalanceVisible.not() || (nativeCoinPnl != null && nativeCoinPnl == BigDecimal.ZERO)) 
                Colors.gray 
            else if (nativeCoinPnl != null && nativeCoinPnl > BigDecimal.ZERO) 
                Color(0xFF00A699) 
            else 
                Colors.coral
        }
        
    val formattedBalanceBtc: String
        get() = if (balanceBtc != null) {
            "${balanceBtc.scale(8)} BTC"
        } else {
            "-- BTC"
        }

    val formattedBalanceFiat: String
        get() = balanceModel?.todaysValue?.let {
            "$currencySymbol${it.scale(2).toStringExpanded()}"
        } ?: "$currencySymbol--"
}