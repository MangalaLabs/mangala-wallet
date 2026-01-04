package com.mangala.wallet.features.chains.antelope_base.domain.usecase.proposal

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.TransactionProposalDecoded
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionAbiByContractAndActionNameUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.DecoderProposalTransactionUseCaseV2
import com.memtrip.eos.abi.writer.ByteWriter
import com.memtrip.eos.abi.writer.bytewriter.DefaultByteWriter
import com.memtrip.eos.core.crypto.EosPublicKey
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DecoderProposalTransactionUseCaseTest {
    private val getActionAbiByContractAndActionNameUseCase =
        mockk<GetActionAbiByContractAndActionNameUseCase>()
    private val useCase =
        DecoderProposalTransactionUseCaseV2(getActionAbiByContractAndActionNameUseCase)

    @Test
    fun `should decode transaction proposal successfully`() = runBlocking {
        // Arrange: Set up the mocked dependencies and input data
        val input = "validHexInput" // Use valid hexadecimal input for testing
        val mockActionsAbi = emptyList<AntelopeActionAbi>()
        val mockTransaction = mockk<TransactionProposalDecoded> {
            every { actions } returns emptyList()
        }

        coEvery {
            getActionAbiByContractAndActionNameUseCase(any(), any(), false)
        } returns Result.success(mockActionsAbi)

        // Act: Call the use case with the test input
        val result = useCase(input)

        // Assert: Validate the output
        assertNotNull(result)
        assertEquals(mockTransaction.expiration, result.expiration)
        assertEquals(mockTransaction.actions.size, result.actions.size)

        // Verify interactions
        coVerify { getActionAbiByContractAndActionNameUseCase(any(), any(), false) }
    }

    @Test
    fun testDecodeCreateAccTransaction() = runTest {
        val input =
            "25e13567f11ca9c0165500000000030000000000ea305500409e9a2264b89a01000000000000a68600000000a8ed323266000000000000a686000000004c0dc14f01000000010003cd991f11cf51450411418040d13273075bc9ed29fe2b8fa168a91a64ef5690d90100000001000000010003cd991f11cf51450411418040d13273075bc9ed29fe2b8fa168a91a64ef5690d9010000000000000000ea305500b0cafe4873bd3e01000000a01a04af4900000000a8ed323214000000a01a04af49000000004c0dc14fb80b00000000000000ea305500003f2a1ba6a24a01000000a01a04af4900000000a8ed323231000000a01a04af49000000004c0dc14fd00700000000000004454f5300000000d00700000000000004454f53000000000000"
        coEvery {
            getActionAbiByContractAndActionNameUseCase(
                "eosio",
                "newaccount",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("newaccount", "eosio", "creator", "name", isVariant = false),
                AntelopeActionAbi("newaccount", "eosio", "name", "name", isVariant = false),
                AntelopeActionAbi("newaccount", "eosio", "owner", "authority", isVariant = false),
                AntelopeActionAbi("newaccount", "eosio", "active", "authority", isVariant = false),
            )
        )
        coEvery {
            getActionAbiByContractAndActionNameUseCase(
                "eosio",
                "authority",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("authority", "eosio", "threshold", "uint32", isVariant = false),
                AntelopeActionAbi("authority", "eosio", "keys", "key_weight[]", isVariant = false),
                AntelopeActionAbi("authority", "eosio", "accounts", "permission_level_weight[]", isVariant = false),
                AntelopeActionAbi("authority", "eosio", "waits", "wait_weight[]", isVariant = false)
            )
        )
        coEvery {
            getActionAbiByContractAndActionNameUseCase(
                "eosio",
                "permission_level_weight",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("permission_level_weight", "eosio", "permission", "permission_level", isVariant = false),
                AntelopeActionAbi("permission_level_weight", "eosio", "weight", "uint16", isVariant = false),
            )
        )
        coEvery {
            getActionAbiByContractAndActionNameUseCase(
                "eosio",
                "permission_level",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("permission_level", "eosio", "actor", "name", isVariant = false),
                AntelopeActionAbi("permission_level", "eosio", "permission", "name", isVariant = false),
            )
        )
        coEvery {
            getActionAbiByContractAndActionNameUseCase(
                "eosio",
                "key_weight",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("key_weight", "eosio", "key", "public_key", isVariant = false),
                AntelopeActionAbi("key_weight", "eosio", "weight", "uint16", isVariant = false),
            )
        )
        coEvery {
            getActionAbiByContractAndActionNameUseCase(
                "eosio",
                "wait_weight",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("wait_weight", "eosio", "wait_sec", "uint32", isVariant = false),
                AntelopeActionAbi("wait_weight", "eosio", "weight", "uint16", isVariant = false),
            )
        )
        coEvery {
            getActionAbiByContractAndActionNameUseCase(
                "eosio",
                "buyrambytes",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("buyrambytes", "eosio", "payer", "name", isVariant = false),
                AntelopeActionAbi("buyrambytes", "eosio", "receiver", "name", isVariant = false),
                AntelopeActionAbi("buyrambytes", "eosio", "bytes", "uint32", isVariant = false),
            )
        )
        coEvery {
            getActionAbiByContractAndActionNameUseCase(
                "eosio",
                "delegatebw",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("delegatebw", "from", "name", "name", isVariant = false),
                AntelopeActionAbi("delegatebw", "receiver", "name", "name", isVariant = false),
                AntelopeActionAbi("delegatebw", "stake_net_quantity", "asset", "name", isVariant = false),
                AntelopeActionAbi("delegatebw", "stake_cpu_quantity", "asset", "name", isVariant = false),
                AntelopeActionAbi("delegatebw", "transfer", "bool", "uint32", isVariant = false),
            )
        )

        val result = useCase(input)

        println(result)
    }

    @Test
    fun testDecodeActionWithExtension() = runTest {
        val input = "e7eaa267a6a0cb1f6c1c00000000010000000000ea30550000002d6b03a78b0110999c6a4e0aaf6900000000a8ed32322810999c6a4e0aaf6910999c6a4e0aaf6910999c6a4e0aaf6910999c6a4e0aaf6910999c6a4e0aaf6900"
        coEvery {
            getActionAbiByContractAndActionNameUseCase(
                "eosio",
                "linkauth",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("linkauth", "eosio", "account", "name", isVariant = false),
                AntelopeActionAbi("linkauth", "eosio", "code", "name", isVariant = false),
                AntelopeActionAbi("linkauth", "eosio", "type", "name", isVariant = false),
                AntelopeActionAbi("linkauth", "eosio", "requirement", "name", isVariant = false),
                AntelopeActionAbi("linkauth", "eosio", "authorized_by", "name$", isVariant = false),
            )
        )

        val result = useCase(input)

        println(result)
    }

    @Test
    fun testDecodeAsset() = runTest {
        val input = "2218b767cb8af5810196000000000100a6823403ea3055000000572d3ccdcd0110999c6a4e0aaf6900000000a8ed32322110999c6a4e0aaf693037bdd544c3a6910d00000000000000014a554e474c45000000"
        coEvery {
            getActionAbiByContractAndActionNameUseCase(
                "eosio.token",
                "transfer",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("transfer", "eosio.token", "from", "name", isVariant = false),
                AntelopeActionAbi("transfer", "eosio.token", "to", "name", isVariant = false),
                AntelopeActionAbi("transfer", "eosio.token", "quantity", "asset", isVariant = false),
                AntelopeActionAbi("transfer", "eosio.token", "memo", "string", isVariant = false),
            )
        )

        val result = useCase(input)

        println(result)
    }

    @Test
    fun testDecodeAssetBuyRam() = runTest {
        val input = "8f9cb867ba931050957800000000010000000000ea3055000000004873bd3e0110999c6a4e0aaf6900000000a8ed32322000000000000000000000000000000000204e00000000000004454f530000000000"
        coEvery {
            getActionAbiByContractAndActionNameUseCase(
                "eosio",
                "buyram",
                false
            )
        } returns Result.success(
            listOf(
                AntelopeActionAbi("buyram", "eosio", "payer", "name", isVariant = false),
                AntelopeActionAbi("buyram", "eosio", "receiver", "name", isVariant = false),
                AntelopeActionAbi("buyram", "eosio", "quant", "asset", isVariant = false),
            )
        )

        val result = useCase(input)

        println(result)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testSerializePublicKey() {
        val byteWriter = DefaultByteWriter()
        byteWriter.putName("accmultisig1") // creator
        byteWriter.putName("fiercepionee") // name
        byteWriter.putUInt(1U) // owner threshold
        byteWriter.putVariableUInt(2L) // owner keys count
        byteWriter.putPublicKey(EosPublicKey("EOS79kiEd252CZSLdKo5YsgFX5VrddmagAeTdNRB6RSoMQB9zqdS3"))
        byteWriter.putUShort(1U) // owner key weight
        byteWriter.putPublicKey(EosPublicKey("EOS8dwuihbUfvRvGahkzo62v9o8xGV3WMgjt4nMtruCsrH4DU7byE"))
        byteWriter.putUShort(1U) // owner key weight
        val result = byteWriter.toBytes()

        println(result.toHexString().uppercase())
    }
}