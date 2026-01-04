package com.mangala.wallet.features.chains.antelope.presentation.ramDetail

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.antelope.base.api.model.Act
import com.mangala.antelope.base.api.model.EosAction
import com.mangala.antelope.base.model.AntelopeRamOhlcData
import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.ActionDataSummaryHeaderUiModel
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.ActionDataUiModel
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.ListActionDataUiModel
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.TransactionHistoryItemAntelope
import com.mangala.wallet.model.util.Resource
import io.mockk.every
import io.mockk.spyk
import kotlinx.datetime.Clock
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Test

class RamDetailUiModelTest {

//    @Test
//    fun `Given Transfer RAM transaction, when getting DCA RAM value, then return null value`() {
//        val testInputs = listOf(
//            // RamTransfer (2 examples)
//            ActionDataSummaryHeaderUiModel.RamTransfer(
//                senderAccount = "user1",
//                recipientAccount = "user2",
//                ramBytes = 2048,
//                memo = "Test RAM transfer",
//                newRamBalance = 10240,
//                currentAccountName = "user1"
//            ),
//            ActionDataSummaryHeaderUiModel.RamTransfer(
//                senderAccount = "user3",
//                recipientAccount = "user3",
//                ramBytes = 4096,
//                memo = "Self RAM transfer",
//                newRamBalance = 20480,
//                currentAccountName = "user3"
//            ),
//        )
//
//        val sut = spyk(
//            createRamDetailUiModel(
//                oldPrice = 1.0,
//                newPrice = 1.5,
//            )
//        )
//
//        every { sut.allRamActionsSorted } returns listOf(
//            TransactionHistoryItemAntelope.TransactionItem(
//                ListActionDataUiModel(
//                    summaryHeaders = testInputs,
//                    blockTime = "00:00",
//                    actionDataUiModel = ActionDataUiModel(
//                        actionTraces = emptyList(),
//                        trxId = "trxId"
//                    ),
//                    txId = "trxId"
//                )
//            )
//        )
//
//        assertEquals(null, sut.dcaValueFormatted)
//    }
//
//    @Test
//    fun `Given buy RAM for other transaction, when getting DCA RAM value, then return null value`() {
//        val testInputs = listOf(
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 2048,
//                ramFee = Balance(0.05, "EOS", 2),
//                totalCost = Balance(1.05, "EOS", 2),
//                newRamBalance = 12288,
//                recipientAccount = "user7",
//                payerAccount = "user8",
//                currentAccountName = "user8"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 4096,
//                ramFee = Balance(0.10, "EOS", 2),
//                totalCost = Balance(2.10, "EOS", 2),
//                newRamBalance = 16384,
//                recipientAccount = "user9",
//                payerAccount = "user10",
//                currentAccountName = "user10"
//            ),
//        )
//
//        val sut = spyk(
//            createRamDetailUiModel(
//                oldPrice = 1.0,
//                newPrice = 1.5,
//            )
//        )
//
//        every { sut.allRamActionsSorted } returns listOf(
//            TransactionHistoryItemAntelope.TransactionItem(
//                ListActionDataUiModel(
//                    summaryHeaders = testInputs,
//                    blockTime = "00:00",
//                    actionDataUiModel = ActionDataUiModel(
//                        actionTraces = emptyList(),
//                        trxId = "trxId"
//                    ),
//                    txId = "trxId"
//                )
//            )
//        )
//
//        assertEquals(null, sut.dcaValueFormatted)
//    }
//
//    @Test
//    fun `Given RAM bought by other transaction, when getting DCA RAM value, then return null value`() {
//        val testInputs = listOf(
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 3072,
//                ramFee = Balance(0.08, "EOS", 2),
//                totalCost = Balance(1.58, "EOS", 2),
//                newRamBalance = 12288,
//                recipientAccount = "user11",
//                payerAccount = "user12",
//                currentAccountName = "user11"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 1024,
//                ramFee = Balance(0.02, "EOS", 2),
//                totalCost = Balance(0.52, "EOS", 2),
//                newRamBalance = 11264,
//                recipientAccount = "user13",
//                payerAccount = "user14",
//                currentAccountName = "user13"
//            ),
//        )
//
//        val sut = spyk(
//            createRamDetailUiModel(
//                oldPrice = 1.0,
//                newPrice = 1.5,
//            )
//        )
//
//        every { sut.allRamActionsSorted } returns listOf(
//            TransactionHistoryItemAntelope.TransactionItem(
//                ListActionDataUiModel(
//                    summaryHeaders = testInputs,
//                    blockTime = "00:00",
//                    actionDataUiModel = ActionDataUiModel(
//                        actionTraces = emptyList(),
//                        trxId = "trxId"
//                    ),
//                    txId = "trxId"
//                )
//            )
//        )
//
//        assertEquals(null, sut.dcaValueFormatted)
//    }
//
//    @Test
//    fun `Given buy RAM for self transaction include RAM bought amount, when getting DCA RAM value, then return correct amount`() {
//        val testInputs = listOf(
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 2048,
//                ramFee = Balance(0.05, "EOS", 2),
//                totalCost = Balance(1.05, "EOS", 2),
//                newRamBalance = 12288,
//                recipientAccount = "user1",
//                payerAccount = "user1",
//                currentAccountName = "user1"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 4096,
//                ramFee = Balance(0.10, "EOS", 2),
//                totalCost = Balance(2.10, "EOS", 2),
//                newRamBalance = 16384,
//                recipientAccount = "user2",
//                payerAccount = "user2",
//                currentAccountName = "user2"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 8192,
//                ramFee = Balance(0.15, "EOS", 2),
//                totalCost = Balance(3.15, "EOS", 2),
//                newRamBalance = 24576,
//                recipientAccount = "user3",
//                payerAccount = "user3",
//                currentAccountName = "user3"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 1024,
//                ramFee = Balance(0.01, "EOS", 2),
//                totalCost = Balance(0.51, "EOS", 2),
//                newRamBalance = 11264,
//                recipientAccount = "user4",
//                payerAccount = "user4",
//                currentAccountName = "user4"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 3072,
//                ramFee = Balance(0.08, "EOS", 2),
//                totalCost = Balance(1.58, "EOS", 2),
//                newRamBalance = 12288,
//                recipientAccount = "user5",
//                payerAccount = "user5",
//                currentAccountName = "user5"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 5120,
//                ramFee = Balance(0.12, "EOS", 2),
//                totalCost = Balance(2.62, "EOS", 2),
//                newRamBalance = 17408,
//                recipientAccount = "user6",
//                payerAccount = "user6",
//                currentAccountName = "user6"
//            ),
//        )
//
//        val sut = spyk(
//            createRamDetailUiModel(
//                oldPrice = 1.0,
//                newPrice = 1.5,
//            )
//        )
//
//        every { sut.allRamActionsSorted } returns listOf(
//            TransactionHistoryItemAntelope.TransactionItem(
//                ListActionDataUiModel(
//                    summaryHeaders = testInputs,
//                    blockTime = "00:00",
//                    actionDataUiModel = ActionDataUiModel(
//                        actionTraces = emptyList(),
//                        trxId = "trxId"
//                    ),
//                    txId = "trxId"
//                )
//            )
//        )
//
//        assertEquals("0.4787 EOS/KB", sut.dcaValueFormatted)
//    }
//
//    @Test
//    fun `Given buy RAM for self transaction but not include RAM bought amount, when getting DCA RAM value, then return 0`() {
//        val testInputs = listOf(
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 0,
//                ramFee = Balance(0.05, "EOS", 2),
//                totalCost = Balance(1.05, "EOS", 2),
//                newRamBalance = 12288,
//                recipientAccount = "user1",
//                payerAccount = "user1",
//                currentAccountName = "user1"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = null,
//                ramFee = Balance(0.08, "EOS", 2),
//                totalCost = Balance(1.58, "EOS", 2),
//                newRamBalance = 12288,
//                recipientAccount = "user5",
//                payerAccount = "user5",
//                currentAccountName = "user5"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 0,
//                ramFee = Balance(0.12, "EOS", 2),
//                totalCost = Balance(2.62, "EOS", 2),
//                newRamBalance = 17408,
//                recipientAccount = "user6",
//                payerAccount = "user6",
//                currentAccountName = "user6"
//            ),
//        )
//
//        val sut = spyk(
//            createRamDetailUiModel(
//                oldPrice = 1.0,
//                newPrice = 1.5,
//            )
//        )
//
//        every { sut.allRamActionsSorted } returns listOf(
//            TransactionHistoryItemAntelope.TransactionItem(
//                ListActionDataUiModel(
//                    summaryHeaders = testInputs,
//                    blockTime = "00:00",
//                    actionDataUiModel = ActionDataUiModel(
//                        actionTraces = emptyList(),
//                        trxId = "trxId"
//                    ),
//                    txId = "trxId"
//                )
//            )
//        )
//
//        assertEquals("0 EOS/KB", sut.dcaValueFormatted)
//    }
//
//    @Test
//    fun `Given both buy RAM legacy and new for self, when getting DCA RAM value, then return correct amount`() {
//        val testInputs = listOf(
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 2048,
//                ramFee = Balance(0.05, "EOS", 2),
//                totalCost = Balance(1.05, "EOS", 2),
//                newRamBalance = 12288,
//                recipientAccount = "user1",
//                payerAccount = "user1",
//                currentAccountName = "user1"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 4096,
//                ramFee = Balance(0.10, "EOS", 2),
//                totalCost = Balance(2.10, "EOS", 2),
//                newRamBalance = 16384,
//                recipientAccount = "user2",
//                payerAccount = "user2",
//                currentAccountName = "user2"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 8192,
//                ramFee = Balance(0.15, "EOS", 2),
//                totalCost = Balance(3.15, "EOS", 2),
//                newRamBalance = 24576,
//                recipientAccount = "user3",
//                payerAccount = "user3",
//                currentAccountName = "user3"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 1024,
//                ramFee = Balance(0.01, "EOS", 2),
//                totalCost = Balance(0.51, "EOS", 2),
//                newRamBalance = 11264,
//                recipientAccount = "user4",
//                payerAccount = "user4",
//                currentAccountName = "user4"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 3072,
//                ramFee = Balance(0.08, "EOS", 2),
//                totalCost = Balance(1.58, "EOS", 2),
//                newRamBalance = 12288,
//                recipientAccount = "user5",
//                payerAccount = "user5",
//                currentAccountName = "user5"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 5120,
//                ramFee = Balance(0.12, "EOS", 2),
//                totalCost = Balance(2.62, "EOS", 2),
//                newRamBalance = 17408,
//                recipientAccount = "user6",
//                payerAccount = "user6",
//                currentAccountName = "user6"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 0,
//                ramFee = Balance(0.05, "EOS", 2),
//                totalCost = Balance(1.05, "EOS", 2),
//                newRamBalance = 12288,
//                recipientAccount = "user1",
//                payerAccount = "user1",
//                currentAccountName = "user1"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = null,
//                ramFee = Balance(0.08, "EOS", 2),
//                totalCost = Balance(1.58, "EOS", 2),
//                newRamBalance = 12288,
//                recipientAccount = "user5",
//                payerAccount = "user5",
//                currentAccountName = "user5"
//            ),
//            ActionDataSummaryHeaderUiModel.RamBuy(
//                ramBytesBought = 0,
//                ramFee = Balance(0.12, "EOS", 2),
//                totalCost = Balance(2.62, "EOS", 2),
//                newRamBalance = 17408,
//                recipientAccount = "user6",
//                payerAccount = "user6",
//                currentAccountName = "user6"
//            ),
//        )
//
//        val sut = spyk(
//            createRamDetailUiModel(
//                oldPrice = 1.0,
//                newPrice = 1.5,
//            )
//        )
//
//        every { sut.allRamActionsSorted } returns listOf(
//            TransactionHistoryItemAntelope.TransactionItem(
//                ListActionDataUiModel(
//                    summaryHeaders = testInputs,
//                    blockTime = "00:00",
//                    actionDataUiModel = ActionDataUiModel(
//                        actionTraces = emptyList(),
//                        trxId = "trxId"
//                    ),
//                    txId = "trxId"
//                )
//            )
//        )
//
//        assertEquals("0.4787 EOS/KB", sut.dcaValueFormatted)
//    }
//
//    @Test
//    fun `Given RAM price change without balance change, when getting PnL, then return correct result`() {
//        val oldPrice = 1.0
//        val newPrice = 1.5
//        val sut = createRamDetailUiModel(oldPrice, newPrice, emptyList())
//
//        val pnlValue = sut.pnlValueAndPercentage.first
//        val pnlPercentage = sut.pnlValueAndPercentage.second
//
//        assertEquals(BigDecimal.parseString("0.5"), pnlValue)
//        assertEquals(BigDecimal.parseString(".5"), pnlPercentage)
//        assertEquals("+50%", sut.priceChangePercentageString)
//        assertEquals("+50%", sut.pnlStringPercent)
//    }
//
//    @Test
//    fun `Given balance change without RAM price change, when getting PnL, then return correct result`() {
//        val oldPrice = 1.0
//        val sut = createRamDetailUiModel(
//            oldPrice = oldPrice,
//            newPrice = oldPrice,
//            last24hLogRamChange = listOf(
//                EosAction(
//                    act = Act(
//                        "eosio", "logramchange", emptyList(), buildJsonObject {
//                            put("ram_bytes", 1024L)
//                        }
//                    ),
//                    receipts = emptyList()
//                )
//            ),
//            currentRamBalance = 2048
//        )
//
//        val pnlValue = sut.pnlValueAndPercentage.first
//        val pnlPercentage = sut.pnlValueAndPercentage.second
//
//        assertEquals(BigDecimal.parseString("1"), pnlValue)
//        assertEquals(BigDecimal.parseString("1"), pnlPercentage)
//        assertEquals("0%", sut.priceChangePercentageString)
//        assertEquals("+100%", sut.pnlStringPercent)
//    }
//
//    @Test
//    fun `Given balance change with both RAM price and balance changes, when getting PnL, then return correct result`() {
//        val oldPrice = 1.0
//        val newPrice = 1.5
//        val sut = createRamDetailUiModel(
//            oldPrice = oldPrice,
//            newPrice = newPrice,
//            last24hLogRamChange = listOf(
//                EosAction(
//                    act = Act(
//                        "eosio", "logramchange", emptyList(), buildJsonObject {
//                            put("ram_bytes", 1024L)
//                        }
//                    ),
//                    receipts = emptyList()
//                )
//            ),
//            currentRamBalance = 2048
//        )
//
//        val pnlValue = sut.pnlValueAndPercentage.first
//        val pnlPercentage = sut.pnlValueAndPercentage.second
//
//        assertEquals(BigDecimal.parseString("2"), pnlValue)
//        assertEquals(BigDecimal.parseString("2"), pnlPercentage)
//        assertEquals("+50%", sut.priceChangePercentageString)
//        assertEquals("+200%", sut.pnlStringPercent)
//    }
//
//    private fun createRamDetailUiModel(
//        oldPrice: Double,
//        newPrice: Double,
//        last24hLogRamChange: List<EosAction> = emptyList(),
//        currentRamBalance: Long = 1024
//    ): com.mangala.wallet.features.chains.antelope.ram.presentation.details.RamDetailUiModel {
//        return com.mangala.wallet.features.chains.antelope.ram.presentation.details.RamDetailUiModel(
//            null,
//            AntelopeAccount(
//                accountName = "",
//                permissions = emptyList(),
//                isActive = true,
//                isTemp = false,
//                createAccountState = AntelopeAccount.CreateAccountState.DONE,
//                purchaseToken = null,
//                purchaseId = null,
//                coreLiquidBalance = "1.0000 EOS",
//                cpuLimit = null,
//                netLimit = null,
//                ramQuota = currentRamBalance,
//                ramUsage = currentRamBalance,
//                rexBalance = null,
//                selfDelegatedBandwidthCpuWeight = null,
//                selfDelegatedBandwidthNetWeight = null,
//                totalResources = null,
//                lastUpdated = null
//            ),
//            null,
//            Resource.Loading(null),
//            Resource.Loading(null),
//            Resource.Loading(null),
//            Resource.Loading(null),
//            last24hLogRamChangeAction = Resource.Success(last24hLogRamChange),
//            ramChartData = AntelopeRamOhlcData(
//                listOf(
//                    AntelopeRamOhlcData.OhlcDataPoint(
//                        Clock.System.now(),
//                        0.0,
//                        0.0,
//                        0.0,
//                        oldPrice,
//                        0.0,
//                        0L
//                    ),
//                    AntelopeRamOhlcData.OhlcDataPoint(
//                        Clock.System.now(),
//                        0.0,
//                        0.0,
//                        0.0,
//                        newPrice,
//                        0.0,
//                        0L
//                    )
//                ),
//                SamplingInterval.ONE_HOUR
//            )
//        )
//    }
}