package com.mangala.wallet.features.chains.antelope.ram.presentation.details

import androidx.compose.ui.graphics.Color
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.api.model.EosAction
import com.mangala.antelope.base.model.AntelopeRamOhlcData
import com.mangala.antelope.base.model.RamMarketData
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.features.chains.antelope_base.domain.model.ActionPagingModelBuilder
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.ActionId
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.ActionDataSummaryHeaderUiModel
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.TransactionHistoryItemAntelope
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.getHeaderItem
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.toListActionDataUiModel
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.model.util.isLoadingWithEmptyData
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.sumOf
import com.mangala.wallet.utils.calculatingDecimalMode
import com.mangala.wallet.utils.ext.format
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.ext.toBigDecimal
import com.mangala.wallet.utils.ext.wrapInParenthesis

data class RamDetailUiModel(
    val showBalance: Boolean?,
    val account: Resource<AntelopeAccount?>,
    val ramMarketData: Resource<RamMarketData?>,
    val buyRam: Resource<List<EosAction>?>,
    val sellRamTransferActions: Resource<List<EosAction>?>,
    val logRam: Resource<List<EosAction>?>,
    val ramFee: Resource<List<EosAction>?>,
    val last24hLogRamChangeAction: Resource<List<EosAction>?>,
    val ramChartData: Resource<AntelopeRamOhlcData?>,
    val ramUnitSelected: RamUnit = RamUnit.KB,
    val generalErrorMessage: String? = null,
    val blockchainType: BlockchainType? = null
) {
    private val precision = account.data?.safeCoreBalance?.precision?.toLong() ?: 4L
    private val roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
    private val decimalMode = calculatingDecimalMode

    // Calculate RAM-related metrics and profits
    val nativeCoinSymbol: String = blockchainType?.getNativeTokenSymbol() ?: ramMarketData.data?.currency ?: "A"
    val ramPrice: BigDecimal = ramMarketData.data?.price ?: BigDecimal.ZERO
    val ramPriceFormatted =
        "${ramPrice.format(precision, roundingMode)} $nativeCoinSymbol/ KB"
    private val totalRamInKb by lazy {
        ((account.data?.ramQuota?.toBigDecimal().orZero()
            .divide(BigDecimal.fromLong(1024), decimalMode)))
    }
    private val totalRamInMb by lazy {
        ((account.data?.ramQuota?.toBigDecimal().orZero()
            .divide(BigDecimal.fromLong(1048576), decimalMode)))
    }
    private val totalRamInGb by lazy {
        ((account.data?.ramQuota?.toBigDecimal().orZero()
            .divide(BigDecimal.fromLong(1073741824), decimalMode)))
    }
    private val totalRamInNativeCoin by lazy { ramPrice * totalRamInKb }

    val ramUnitFormatted =
        if (ramUnitSelected == RamUnit.NATIVE_COIN) nativeCoinSymbol else ramUnitSelected.unitString

    val totalRam = if (account.data?.ramQuota == null) null else when (ramUnitSelected) {
        RamUnit.BYTES -> account.data?.ramQuota?.toString().orEmpty()
        RamUnit.KB -> totalRamInKb.format(precision, roundingMode)
        RamUnit.MB -> totalRamInMb.format(precision, roundingMode)
        RamUnit.GB -> totalRamInGb.format(precision, roundingMode)
        RamUnit.NATIVE_COIN -> totalRamInNativeCoin.format(precision, roundingMode)
    }
    private val oldRamPrice = ramChartData.data?.dataPoints?.firstOrNull()?.close
    private val newRamPrice = ramChartData.data?.dataPoints?.lastOrNull()?.close

    private val priceChange =
        if (oldRamPrice != null && newRamPrice != null) (newRamPrice - oldRamPrice) else null
    private val priceChangePercentage =
        if (oldRamPrice != null && newRamPrice != null) (newRamPrice - oldRamPrice) / oldRamPrice else null
    val priceChangePercentageString by lazy {
        val roundedPercentage =
            priceChangePercentage?.toBigDecimal()?.times(100) ?: return@lazy null

        if (roundedPercentage > BigDecimal.ZERO) "+${roundedPercentage.format(roundingMode = roundingMode)}%" else "${roundedPercentage.format(roundingMode = roundingMode)}%"
    }

    val pnlValueAndPercentage by lazy {
        val action = last24hLogRamChangeAction.data ?: return@lazy null to null

        if (action.isEmpty()) {
            // No RAM changes in last 24 hours => PnL based on price change
            totalRamInKb.multiply(
                priceChangePercentage.orZero().toBigDecimal()
            ) to priceChangePercentage?.toBigDecimal()
        } else {
            // RAM balance changed => PnL based on balance
            val lastRamBalance = action.firstOrNull()?.act?.ramBytes
            val currentRamBalance = account.data?.ramQuota

            if (lastRamBalance != null && currentRamBalance != null && oldRamPrice != null && newRamPrice != null) {
                val oldAccountValue =
                    lastRamBalance.toBigDecimal()
                        .divide(BigDecimal.fromLong(1024), decimalMode) * oldRamPrice.toBigDecimal()
                val newAccountValue =
                    currentRamBalance.toBigDecimal()
                        .divide(BigDecimal.fromLong(1024), decimalMode) * newRamPrice.toBigDecimal()

                val pnlValue = newAccountValue - oldAccountValue
                val pnlPercentage = pnlValue.divide(oldAccountValue, decimalMode)

                pnlValue to pnlPercentage
            } else {
                null to null
            }
        }
    }
    val pnlStringPercent: String? by lazy {
        val pnlPercentage = pnlValueAndPercentage.second ?: return@lazy null
        val roundedPercentage = pnlPercentage.times(100).format(roundingMode = roundingMode)
        if (pnlPercentage > BigDecimal.ZERO) "+${roundedPercentage}%" else "${roundedPercentage}%"
    }
    private val pnlStringValue: String? by lazy {
        val pnlValue = pnlValueAndPercentage.first ?: return@lazy null
        val roundedValue = pnlValue.format(precision, roundingMode)
        if (pnlValue > BigDecimal.ZERO) "+$roundedValue" else roundedValue
    }
    val pnlValueString by lazy {
        when (showBalance) {
            true -> {
                if (pnlStringValue == null) null else "$pnlStringValue $nativeCoinSymbol ${pnlStringPercent?.wrapInParenthesis().orEmpty()}"
            }

            false -> HIDDEN_BALANCE_STRING
            null -> "placeholder"
        }
    }

    val pnlColor: Color
        get() {
            return when {
                showBalance != true -> Colors.gray
                priceChange == null -> Colors.gray // or some default color when pnlValue is null
                priceChange > 0 -> Color(0xFF00A699)
                else -> Color(0xFFFF5A5F)
            }
        }

    private val accountName = account.data?.accountName

    private val totalSellBigDecimal by lazy {
        if (sellRamTransferActions.isLoading() && sellRamTransferActions.data.isNullOrEmpty()) {
            null
        } else sellRamTransferActions.data?.map {
            it.act?.getDataAmountAsDouble().orZero().toBigDecimal()
        }?.sumOf()
    }
    private val totalSellString by lazy {
        totalSellBigDecimal?.format(
            decimalPlaces = precision,
            roundingMode
        )
    }
    val totalSellFormatted by lazy { if (totalSellString == null) null else "$totalSellString $nativeCoinSymbol" }

    private val totalFeeBigDecimal by lazy {
        if (ramFee.isLoading() && ramFee.data.isNullOrEmpty()) {
            null
        } else ramFee.data?.map {
            it.act?.getDataAmountAsDouble().orZero().toBigDecimal()
        }?.sumOf()
    }
    private val totalFeeString by lazy {
        totalFeeBigDecimal?.format(
            decimalPlaces = precision,
            roundingMode
        )
    }
    val totalFeeFormatted by lazy { if (totalFeeString == null) null else "$totalFeeString $nativeCoinSymbol" }

    private val buyRamTransferActions = buyRam.data
    private val totalBuyBigDecimal by lazy {
        if ((buyRam.isLoading() && buyRamTransferActions.isNullOrEmpty()) || totalFeeBigDecimal == null) {
            null
        } else {
            buyRamTransferActions?.map {
                it.act?.getDataAmountAsDouble().orZero().toBigDecimal()
            }?.sumOf().orZero() + totalFeeBigDecimal.orZero()
        }

    }
    private val totalBuyString by lazy {
        totalBuyBigDecimal?.format(
            decimalPlaces = precision,
            roundingMode
        )
    }
    val totalBuyFormatted by lazy { if (totalBuyString == null) null else "$totalBuyString $nativeCoinSymbol" }

    private val sortedRamTransfer by lazy {
        logRam.data?.filter { it.act?.actionId == ActionId.RAM_TRANSFER }?.partition {
            it.act?.to == accountName
        }
    }
    private val ramTransferIn = sortedRamTransfer?.first
    private val ramTransferOut = sortedRamTransfer?.second

    private val profit =
        if (totalSellBigDecimal == null || totalFeeBigDecimal == null || totalBuyBigDecimal == null) {
            null
        } else {
            (totalRamInNativeCoin + totalSellBigDecimal.orZero()) - totalBuyBigDecimal.orZero()
        }
    private val profitString = profit?.format(decimalPlaces = precision, roundingMode)
    val profitFormatted = if (profitString == null) null else "$profitString $nativeCoinSymbol"

    // Combine all RAM actions into a single list and sort them by timestamp
    private val allRamActions: List<EosAction>? =
        if (buyRamTransferActions == null && sellRamTransferActions.isLoadingWithEmptyData() || logRam.isLoadingWithEmptyData() || ramFee.isLoadingWithEmptyData() || logRam.isLoadingWithEmptyData()) {
            null
        } else {
            (buyRamTransferActions.orEmpty() + sellRamTransferActions.data.orEmpty()) + logRam.data.orEmpty() + ramFee.data.orEmpty() + ramTransferIn.orEmpty() + ramTransferOut.orEmpty()
        }

    val allRamActionsSorted: List<TransactionHistoryItemAntelope>? by lazy {
        if (allRamActions == null) {
            null
        } else {

            val groupedByTrxId = mutableMapOf<String, ActionPagingModelBuilder>()
            allRamActions.forEach { action ->
                val trxId = action.trxId
                if (trxId != null) {
                    if (!groupedByTrxId.containsKey(trxId)) {
                        groupedByTrxId[trxId] = ActionPagingModelBuilder(trxId)
                    }
                    groupedByTrxId[trxId]?.addAct(action)
                }
            }

            val result = mutableListOf<TransactionHistoryItemAntelope>()
            val txns =
                groupedByTrxId.values.toList()
                    .sortedByDescending { it.actionTraces.first().blockTime }
            txns.forEachIndexed { index, it ->
                val beforeItem = if (index > 0) txns[index - 1] else null
                val afterItem = if (index < txns.size - 1) txns[index] else null

                val model = it.build().toListActionDataUiModel(accountName.orEmpty())

                if (beforeItem == null && afterItem != null) {
                    val headerItem = afterItem.actionTraces.first().blockTime?.getHeaderItem()
                    headerItem?.let { result.add(it) }
                } else if (beforeItem != null && afterItem != null) {
                    val beforeDate =
                        beforeItem.actionTraces.first().blockTime?.getHeaderItem()
                    val afterDate =
                        afterItem.actionTraces.first().blockTime?.getHeaderItem()

                    if (beforeDate != afterDate && afterDate != null) {
                        result.add(afterDate)
                    }
                }

                result.add(
                    TransactionHistoryItemAntelope.TransactionItem(
                        listActionDataUiModel = model
                    )
                )
            }

            result
        }
    }

    private val dcaValue: BigDecimal?
        get() {
            val buyRamAction = allRamActionsSorted
                ?.asSequence()
                ?.filterIsInstance<TransactionHistoryItemAntelope.TransactionItem>()
                ?.flatMap { it.listActionDataUiModel.summaryHeaders.asSequence() }
                ?.filterIsInstance<ActionDataSummaryHeaderUiModel.RamBuy>()
                ?.filter { it.buyRamType == ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BUY_FOR_SELF && (it.ramBytesBought ?: 0) > 0 }
                ?.toList()
            val totalRamBytesBought =
                buyRamAction?.sumOf { it.ramBytesBought?.toBigDecimal() ?: BigDecimal.ZERO }
            val totalCost = buyRamAction?.sumOf { it.totalCost.amount.toBigDecimal() }

            // Return DCA value if both totalRamBought and totalCost are not null and calculate by dividing totalCost by (totalRamBytesBought * 1024)
            return if (totalRamBytesBought != null && totalCost != null) {
                if (totalRamBytesBought > BigDecimal.ZERO)
                    totalCost.times(BigDecimal.fromLong(1024)).divide(totalRamBytesBought, decimalMode)
                else BigDecimal.ZERO
            } else {
                null
            }
        }

    val dcaValueFormatted: String?
        get() {
            val dcaValueString = dcaValue?.format(decimalPlaces = precision, roundingMode)
            return dcaValueString?.let { "$it $nativeCoinSymbol/KB" }
        }

    enum class RamUnit(val unitString: String) {
        BYTES("Bytes"),
        KB("KB"),
        MB("MB"),
        GB("GB"),
        NATIVE_COIN("")
    }
}