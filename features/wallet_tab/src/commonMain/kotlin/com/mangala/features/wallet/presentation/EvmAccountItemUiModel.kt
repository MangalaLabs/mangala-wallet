package com.mangala.features.wallet.presentation

import androidx.compose.ui.graphics.Color
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.token.domain.totalValue
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.utils.PNL_DECIMAL_PLACES
import com.mangala.wallet.utils.ext.formatCompact

data class EvmAccountItemUiModel(
    val account: AccountBlockchainModel,
    val balances: List<TokenBalanceModel>? = null,
    override val isBalanceVisible: Boolean,
    override val currencySymbol: String
): BaseAccountItemUiModel {
    val totalValue: BigDecimal? by lazy {
        balances.totalValue()
    }
    val totalValuePlaceholderEnabled = balances.isNullOrEmpty()
    val totalValueFormatted: String by lazy {
        if (totalValue == null) return@lazy "$0.00"

        if (isBalanceVisible) currencySymbol + totalValue!!.formatCompact(PNL_DECIMAL_PLACES)
        else HIDDEN_BALANCE_STRING
    }

    private val pnl: BigDecimal? by lazy {
        if (balances == null) return@lazy null

        var pnlValue = BigDecimal.ZERO
        balances.forEach {
            if (it.pnl != null) {
                pnlValue += it.pnl ?: BigDecimal.ZERO
            }
        }
        pnlValue
    }

    val formattedPnlPlaceholderEnabled = balances == null
    override val formattedPnl: String by lazy {
        if (balances == null) return@lazy "00%"
        if (!isBalanceVisible) return@lazy HIDDEN_BALANCE_STRING

        var pnlValue = BigDecimal.ZERO
        var totalOldValue = BigDecimal.ZERO
        var totalNewValue = BigDecimal.ZERO
        balances.forEach {
            if (it.pnl != null && it.yesterdaysValue != null && it.todaysValue != null) {
                pnlValue += it.pnl ?: BigDecimal.ZERO
                totalOldValue += it.yesterdaysValue ?: BigDecimal.ZERO
                totalNewValue += it.todaysValue ?: BigDecimal.ZERO
            }
        }
        val pnlSign = if (pnlValue > BigDecimal.ZERO) "+" else ""
        val pnlPercentage = if (totalOldValue != BigDecimal.ZERO) {
            pnlValue.divide(totalOldValue, DecimalMode(10, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 10)).multiply(
                BigDecimal.parseString("100"))
        } else BigDecimal.ZERO
        pnlSign + pnlPercentage.scale(
            PNL_DECIMAL_PLACES
        ).toStringExpanded() + " %"
    }

    val pnlColor: Color
        get() {
            return if (isBalanceVisible.not() || (pnl != null && pnl!! == BigDecimal.ZERO)) Colors.gray else if (pnl != null && pnl!! > BigDecimal.ZERO) Color(0xFF00A699) else Colors.coral
        }

    //
    // TODO: Share logic with wallet details screen
}
