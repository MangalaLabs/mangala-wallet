package com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction

import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.domain.model.Transaction
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.account.actions.sellram.SellRamArgs
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter
import com.memtrip.eos.chain.actions.transaction.transfer.actions.TransferArgs
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ResourceProviderRequestTransactionUseCaseTest {

    private val antelopeRepository: AntelopeRepository = mockk()

    private val sut = ResourceProviderRequestTransactionUseCase(antelopeRepository)

    @Test
    fun `Given sell RAM request transaction returns fee required exactly matches maxFees, when request transaction, then return result success`() =
        runTest {
            mockValidateResultWithFee()
            val result = sut(
                constructSellRamSignTransactionRequest(),
                "",
                "",
                BlockchainType.Eos,
                maxFees = 0.0097
            )

            assertTrue(result.isSuccess)
        }

    @Test
    fun `Given sell RAM request transaction returns fee required greater than maxFees, when request transaction, then return result failure`() =
        runTest {
            mockValidateResultWithFee()
            val result = sut(
                constructSellRamSignTransactionRequest(),
                "",
                "",
                BlockchainType.Eos,
                maxFees = 0.0096
            )

            assertTrue(result.isFailure)
        }

    @Test
    fun `Given sell RAM request transaction returns sell ram transaction with different data, when request transaction, then return result failure`() =
        runTest {
            mockValidateResultWithFee(
                listOf(modifiedSellRamTransaction.copy(data = "different data"))
            )
            val result = sut(
                constructSellRamSignTransactionRequest(),
                "",
                "",
                BlockchainType.Eos
            )

            assertTrue(result.isFailure)
        }

    @Test
    fun `Given sell RAM request transaction returns sell ram transaction with different account name, when request transaction, then return result failure`() =
        runTest {
            mockValidateResultWithFee(
                listOf(modifiedSellRamTransaction.copy(account = "different account"))
            )
            val result = sut(
                constructSellRamSignTransactionRequest(),
                "",
                "",
                BlockchainType.Eos
            )

            assertTrue(result.isFailure)
        }

    @Test
    fun `Given sell RAM request transaction returns sell ram transaction with different action name, when request transaction, then return result failure`() =
        runTest {
            mockValidateResultWithFee(
                listOf(modifiedSellRamTransaction.copy(name = "action"))
            )
            val result = sut(
                constructSellRamSignTransactionRequest(),
                "",
                "",
                BlockchainType.Eos
            )

            assertTrue(result.isFailure)
        }

    @Test
    fun `Given sell RAM request transaction returns sell ram transaction with different authorization, when request transaction, then return result failure`() =
        runTest {
            mockValidateResultWithFee(
                listOf(
                    modifiedSellRamTransaction.copy(
                        authorization = listOf(
                            Transaction.Authorization(
                                "different",
                                "different"
                            )
                        )
                    )
                )
            )
            val result = sut(
                constructSellRamSignTransactionRequest(),
                "",
                "",
                BlockchainType.Eos
            )

            assertTrue(result.isFailure)
        }

    @Test
    fun `Given sell RAM request transaction returns transaction without signature, when request transaction, then return result failure`() =
        runTest {
            mockValidateResultWithFee(
                signatures = emptyList()
            )
            val result = sut(
                constructSellRamSignTransactionRequest(),
                "",
                "",
                BlockchainType.Eos
            )

            assertTrue(result.isFailure)
        }

    @Test
    fun `Given sell RAM request transaction returns transaction with rogue action, when request transaction, then return result failure`() =
        runTest {
            mockValidateResultWithFee(
                actions = listOf(
                    modifiedTransferTransaction,
                    modifiedSellRamTransaction,
                    Transaction.Action(
                        "rogue",
                        "action",
                        listOf(),
                        "data"
                    )
                )
            )
            val result = sut(
                constructSellRamSignTransactionRequest(),
                "",
                "",
                BlockchainType.Eos
            )

            assertTrue(result.isFailure)
        }

    @Test
    fun `Given sell RAM request transaction returns resource not required, when request transaction, then return result success`() =
        runTest {
            mockValidateResultNoFeeRequired()
            val result = sut(
                constructSellRamSignTransactionRequest(),
                "",
                "",
                BlockchainType.Eos
            )

            assertTrue(result.isSuccess)
        }


    private fun mockValidateResultWithFee(
        actions: List<Transaction.Action> = listOf(
            modifiedTransferTransaction,
            modifiedSellRamTransaction
        ),
        signatures: List<String> = listOf("abcdef")
    ) {
        coEvery {
            antelopeRepository.requestTransaction(
                BlockchainType.Eos,
                any(),
                "",
                ""
            )
        } returns Result.success(
            ResourceProviderResponse.FeeRequired(
                FeeBreakdown("0.0000 EOS", "0.0000 EOS", "0.0097 EOS"),
                "0.0097 EOS",
                Transaction(
                    expiration = "",
                    refBlockNum = 0L,
                    refBlockPrefix = 0L,
                    maxNetUsageWords = 0L,
                    maxCpuUsageMs = 0L,
                    delaySecs = 0L,
                    actions = actions,
                    signatures = signatures
                )
            )
        )
    }

    private fun mockValidateResultNoFeeRequired() {
        coEvery {
            antelopeRepository.requestTransaction(
                BlockchainType.Eos,
                any(),
                "",
                ""
            )
        } returns Result.success(ResourceProviderResponse.ResourceNotRequired)
    }

    private fun constructSellRamSignTransactionRequest() = SignTransactionRequest(
        chainId = "73e4385a2708e6d7048834fbc1079f2fabb17b3c125b146af438971e90716c4d",
        expiryTimestamp = 1721891831961,
        headBlockId = "091284cfbc7850af42e6c9b6d1e57f83ef66d19d58a1686f3d48307a4eda8845",
        authorization = emptyList(),
        actions = listOf(
            SignTransactionRequest.Action.SellRam(
                authorization = listOf(),
                sellAccount = "eidkcaz31234",
                bytes = 1000
            )
        ),
        signTransactionType = SignTransactionType.SELL_RAM
    )

    private val modifiedTransferTransaction = Transaction.Action(
        "eosio.token",
        "transfer",
        listOf(),
        "408608e31b049353000000403210955e610000000000000004454f5300000000126b6c646a6b696f7565726f7775656f697277" // Transfer with quantity "0.0097 EOS"
    )

    private val modifiedSellRamTransaction = Transaction.Action(
        "eosio",
        "sellram",
        listOf(),
        "408608e31b049353e803000000000000" // SellRam with bytes 1000
    )
}