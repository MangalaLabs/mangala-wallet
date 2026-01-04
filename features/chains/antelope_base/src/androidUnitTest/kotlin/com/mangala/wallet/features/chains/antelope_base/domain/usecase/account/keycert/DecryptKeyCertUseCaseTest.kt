package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.keycert

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.toRawHexString
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.core.crypto.KeyType
import kotlin.test.Test
import kotlin.test.assertEquals

class DecryptKeyCertUseCaseTest {

    val sut = DecryptKeyCertUseCase()

    @Test
    fun `Given a valid anchorcert-prefixed keycert string, when the keycert is decrypted, then the keycert is returned`() {
        val keycert =
            "anchorcert:c-Q4WicI5tcEiDT7wQefL6uxezwSWxRq9DiXHpBxbE3QqRt7zM2OswAAAACo7TIyACR7vU4h6d0--tjQ_tluIYLtDpdZJSqNb-wpoQ19HN_UbSUgCc0"

        val result = sut(keycert)

        assertEquals("qibgvn3v3iox", result.permissionLevel.actor)
        assertEquals("active", result.permissionLevel.permission)

        println(result)
    }

    @Test
    fun `Given a valid anchorcert-prefixed keycert string 2, when the keycert is decrypted, then the keycert is returned`() {
        val keycert =
            "anchorcert:c-Q4WicI5tcEiDT7wQefL6uxezwSWxRq9DiXHpBxbE1gbo56uayNOgAAAACo7TIyACSAuKYOWTGrRhWWCa0Ztl4kbLqOrvcS9akPvkDo_JJ5IbxpWqw"

        val result = sut(keycert)

        assertEquals("beautifultra", result.permissionLevel.actor)
        assertEquals("active", result.permissionLevel.permission)

        println(result)
    }

    @Test
    fun `Given a valid anchorcert-prefixed keycert string, when get the encrypted mnemonic, then the correct mnemonic is returned`() {
        val keycert =
            "anchorcert:c-Q4WicI5tcEiDT7wQefL6uxezwSWxRq9DiXHpBxbE3QqRt7zM2OswAAAACo7TIyACR7vU4h6d0--tjQ_tluIYLtDpdZJSqNb-wpoQ19HN_UbSUgCc0"
        val expected = listOf(
            "abandon",
            "banana",
            "upon",
            "prefer",
            "capable",
            "exclude",
            "exile",
            "volcano",
            "minimum",
            "legal",
            "grant",
            "service",
            "load",
            "half",
            "place",
            "rather",
            "famous",
            "effort",
            "sausage",
            "lunch",
            "patient",
            "hip",
            "elbow",
            "sausage",
            "egg",
            "myth",
            "lens",
            "defy"
        )

        val result = sut(keycert)
        val actualMnemonic = sut.getEncryptedPrivateKeyMnemonic(result)

        assertEquals(expected, actualMnemonic)
    }

    @Test
    fun `Given a valid mnemonic, when get keycert, then the correct keycert is returned`() {
        val blockchainType = BlockchainType.EosJungleTestnet
        val permission = TransactionAuthorizationAbi("beautifultra", "active")
        val mnemonic = listOf(
            "abandon", "banner", "actual",
            "below", "atom", "sister",
            "hello", "method", "floor",
            "age", "refuse", "cycle",
            "funny", "casino", "concert",
            "bubble", "urge", "maximum",
            "stove", "capable", "salad",
            "also", "dish", "ceiling",
            "must", "taste", "pistol",
            "few"
        )

        val result = sut(blockchainType, permission, mnemonic)

        assertEquals("beautifultra", result.permissionLevel.actor)
        assertEquals("active", result.permissionLevel.permission)
        assertEquals(KeyType.K1, result.encryptedPrivateKey.type)
        assertEquals(36, result.encryptedPrivateKey.level)
        assertEquals("80b8a60e", result.encryptedPrivateKey.checksum.toRawHexString())
        assertEquals(
            "5931ab46159609ad19b65e246cba8eaef712f5a90fbe40e8fc927921bc695aac",
            result.encryptedPrivateKey.cipherText.toRawHexString()
        )
    }


    @Test
    fun `Given a valid anchorcert-prefixed keycert string 2, when get the encrypted mnemonic, then the correct mnemonic is returned`() {
        val keycert =
            "anchorcert:c-Q4WicI5tcEiDT7wQefL6uxezwSWxRq9DiXHpBxbE1gbo56uayNOgAAAACo7TIyACSAuKYOWTGrRhWWCa0Ztl4kbLqOrvcS9akPvkDo_JJ5IbxpWqw"
        val expected = listOf(
            "abandon", "banner", "actual",
            "below", "atom", "sister",
            "hello", "method", "floor",
            "age", "refuse", "cycle",
            "funny", "casino", "concert",
            "bubble", "urge", "maximum",
            "stove", "capable", "salad",
            "also", "dish", "ceiling",
            "must", "taste", "pistol",
            "few"
        )

        val result = sut(keycert)
        val actualMnemonic = sut.getEncryptedPrivateKeyMnemonic(result)
        println(result.encryptedPrivateKey)
        println(result.encryptedPrivateKey.cipherText.toRawHexString())
        println(result.encryptedPrivateKey.checksum.toRawHexString())

        assertEquals(expected, actualMnemonic)
    }

    @Test
    fun `Given a valid anchorcert-prefixed keycert string, when the keycert is decrypted, then the correct private key is returned`() {
        val keycert =
            "anchorcert:c-Q4WicI5tcEiDT7wQefL6uxezwSWxRq9DiXHpBxbE3QqRt7zM2OswAAAACo7TIyACR7vU4h6d0--tjQ_tluIYLtDpdZJSqNb-wpoQ19HN_UbSUgCc0"
        val expected = "PVT_K1_2FcoMJCs9DfxPVkk9fop5kB5vuKMZe9vrhmXUuxZxJRQsivHr3"
        val encryptionKeys = listOf("sadness", "improve", "blast", "erupt", "blue", "magnet")

        val result = sut(keycert)
        val decrypted = sut.decryptKeyCert(result, encryptionKeys)

        assertEquals(expected, decrypted.toString())
    }

    @Test
    fun `Given a valid anchorcert-prefixed keycert string 2, when the keycert is decrypted, then the correct private key is returned`() {
        val keycert =
            "anchorcert:c-Q4WicI5tcEiDT7wQefL6uxezwSWxRq9DiXHpBxbE1gbo56uayNOgAAAACo7TIyACSAuKYOWTGrRhWWCa0Ztl4kbLqOrvcS9akPvkDo_JJ5IbxpWqw"
        val expected = "PVT_K1_rvnpx3sr1PNrMCMgRW91t6XtEyGm6bdTuZ6cvPYsTPhpNhSx3"
        val encryptionKeys = listOf("expect", "cactus", "answer", "sound", "enjoy", "estate")

        val result = sut(keycert)
        val decrypted = sut.decryptKeyCert(result, encryptionKeys)

        assertEquals(expected, decrypted.toString())
    }
}