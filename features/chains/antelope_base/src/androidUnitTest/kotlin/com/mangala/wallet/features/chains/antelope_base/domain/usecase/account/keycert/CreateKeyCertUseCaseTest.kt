package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.keycert

import com.mangala.wallet.cryptography.generateSecureRandomBytes
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.keycert.SecurityLevel
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.hexStringToByteArray
import com.mangala.wallet.utils.toRawHexString
import com.memtrip.eos.chain.actions.keycert.EncryptedPrivateKeyArgs
import com.memtrip.eos.chain.actions.keycert.KeyCertArgs
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.KeyType
import io.mockk.every
import io.mockk.mockkStatic
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateKeyCertUseCaseTest {

    val sut = CreateKeyCertUseCase()

    @Test
    fun `Given valid data to create keycert, and a determined encryption words, when create key cert, then return determined key cert`() {
        val privateKey =
            EosPrivateKey.fromString("PVT_K1_rvnpx3sr1PNrMCMgRW91t6XtEyGm6bdTuZ6cvPYsTPhpNhSx3")
        val blockchainType = BlockchainType.EosJungleTestnet
        val accountName = "beautifultra"
        val permissionName = "active"
        val encryptionWords = listOf("expect", "cactus", "answer", "sound", "enjoy", "estate")

        val result = sut(
            privateKey,
            blockchainType,
            "beautifultra",
            "active",
            encryptionWords
        )

        assertEquals(BlockchainType.EosJungleTestnet.chainId, result.chainId)
        assertEquals(accountName, result.permissionLevel.actor)
        assertEquals(permissionName, result.permissionLevel.permission)
        assertEquals(encryptionWords, result.encryptionWords)
        assertEquals(KeyType.K1, result.encryptedPrivateKey.type)
        assertEquals(SecurityLevel.DEFAULT.value, result.encryptedPrivateKey.level)
        assertEquals("80b8a60e", result.encryptedPrivateKey.checksum.toRawHexString())
        assertEquals(
            "5931ab46159609ad19b65e246cba8eaef712f5a90fbe40e8fc927921bc695aac",
            result.encryptedPrivateKey.cipherText.toRawHexString()
        )
    }

    @Test
    fun `Given valid data to create keycert, when create key cert with random encryption words, then return key cert with words`() {
        val privateKey =
            EosPrivateKey.fromString("PVT_K1_rvnpx3sr1PNrMCMgRW91t6XtEyGm6bdTuZ6cvPYsTPhpNhSx3")
        val blockchainType = BlockchainType.EosJungleTestnet
        val accountName = "beautifultra"
        val permissionName = "active"
        mockkStatic("com.mangala.wallet.cryptography.SecureRandomKt")
        val secureRandomBytes = listOf(
            byteArrayOf(-71, -47, 59, -56),
            byteArrayOf(35, 113, -87, -42),
            byteArrayOf(2, -18, -46, -114),
            byteArrayOf(66, -108, -120, -26),
            byteArrayOf(-110, 111, 54, -98),
            byteArrayOf(-21, 27, -26, -128)
        )
        every { generateSecureRandomBytes(4) } returnsMany secureRandomBytes

        val result = sut(
            privateKey,
            blockchainType,
            accountName,
            permissionName
        )

        assertEquals(BlockchainType.EosJungleTestnet.chainId, result.chainId)
        assertEquals(accountName, result.permissionLevel.actor)
        assertEquals(permissionName, result.permissionLevel.permission)
        assertEquals(6, result.encryptionWords?.size)
        assertEquals(KeyType.K1, result.encryptedPrivateKey.type)
        assertEquals(SecurityLevel.DEFAULT.value, result.encryptedPrivateKey.level)
        assertEquals(
            listOf("similar", "sweet", "divert", "jelly", "assist", "live"),
            result.encryptionWords
        )
        assertEquals("80b8a60e", result.encryptedPrivateKey.checksum.toRawHexString())
        assertEquals(
            "a90b17a3efafad4787b3a2bfa66bc2fe355ad97d882f850d6ab83d878a61df87",
            result.encryptedPrivateKey.cipherText.toRawHexString()
        )
    }

    @Test
    fun `Given valid keycert, when get anchorcert string, then return valid anchorcert string`() {
        val keyCert = KeyCertArgs(
            chainId = BlockchainType.EosJungleTestnet.chainId,
            permissionLevel = TransactionAuthorizationAbi(
                actor = "beautifultra",
                permission = "active"
            ),
            encryptedPrivateKey = EncryptedPrivateKeyArgs(
                type = KeyType.K1,
                level = SecurityLevel.DEFAULT.value,
                checksum = "80b8a60e".hexStringToByteArray(),
                cipherText = "a90b17a3efafad4787b3a2bfa66bc2fe355ad97d882f850d6ab83d878a61df87".hexStringToByteArray()
            ),
            encryptionWords = listOf("similar", "sweet", "divert", "jelly", "assist", "live")
        )

        val result = sut.toAnchorCertString(keyCert)

        assertEquals(
            "anchorcert:c-Q4WicI5tcEiDT7wQefL6uxezwSWxRq9DiXHpBxbE1gbo56uayNOgAAAACo7TIyACSAuKYOqQsXo--vrUeHs6K_pmvC_jVa2X2IL4UNarg9h4ph34c",
            result
        )
    }
}