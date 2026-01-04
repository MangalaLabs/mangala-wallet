package com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.stakeforresource

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.DecimalFormat

data class StakeForResourceUiModel(
    val balance: Balance = Balance(0.0, "", 0),
    val nativeToken: String = "",
    val eosAmount: String = "",
    val receiveAccountName: String = "",
    val suggestionInputUiModels: List<StakeForResourceInputUiModel> = emptyList(),
    val isSelectMax: Boolean = false,
    val isStake: Boolean = true,
    val isInsufficientInputAmount: Boolean = false ,
    val accountName: String = "",
    val account: AntelopeAccount? = null,
    val isBalanceVisible: Boolean = false,
    val isLoading: Boolean = true,
    val promptConfirmTransaction: Boolean = false,
    val resourceRequiredBreakdown: FeeBreakdown? = null,
    val resourceRequiredTotal: String? = null,
    val inputSectionEnabled : Boolean = false
) : BaseScreenModel() {
    private val decimalFormat = DecimalFormat("#.##")

    val netUsagePercentage = decimalFormat.format(account?.netLimit?.getUsedPercentage()?.times(100) ?: 0.0)
    val cpuUsagePercentage = decimalFormat.format(account?.cpuLimit?.getUsedPercentage()?.times(100) ?: 0.0)

    val eosBalanceWithSymbol: String
        get() = BalanceFormatter.formatEosBalance(balance, ignoreLocale = false)

    val eosBalance: Double
        get() = balance.amount

    val isEnableExecuteButton: Boolean
        get() = eosAmount.isNotBlank() && !isInsufficientInputAmount && isLoading.not()
}