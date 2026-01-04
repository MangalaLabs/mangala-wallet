package com.mangala.wallet.core.hdwallet.domain

import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlin.test.Test
import kotlin.test.assertEquals

class GenerateHDKeyUseCaseTest {

    private val seedPhrase = "waste smoke pepper liberty slow hollow symbol utility spatial city uniform music".split(" ")

    @Test
    fun `Given root key When generate hardened HD key for purpose Then return HDKey with correct private key`() {
        val result = initUseCase().derivePath(seedPhrase, "", "m/44'")

        assertEquals(byteArrayOf(-77, -25, -78, -20, 63, -61, -14, -66, 22, -58, 7, 103, -25, 21, 1, -124, -123, 122, -25, 108, 59, 12, 25, 88, -17, 97, 34, 0, -3, -52, 15, 33).toList(), result.privateKey.toList())
        assertEquals(44, result.accountIndex)
    }

    @Test
    fun `Given root key When generate hardened HD key for purpose Then return HDKey with correct chain code`() {
        val result = initUseCase().derivePath(seedPhrase, "", "m/44'")

        assertEquals(byteArrayOf(-33, -49, 38, 18, -92, -83, 64, 113, 62, 70, -77, 100, 53, 32, -59, 107, 7, -101, -95, 115, -105, 13, -37, -112, -94, 126, 105, 8, -83, 63, 118, 75).toList(), result.chainCode.toList())
        assertEquals(44, result.accountIndex)
    }

    @Test
    fun `Given root key When generate hardened HD key for coin Then return HDKey with correct private key`() {
        val result = initUseCase().derivePath(seedPhrase, "", "m/44'/60'")

        assertEquals(byteArrayOf(-26, 73, 17, 104, -43, -65, 40, 111, 94, -124, 5, -50, 100, 6, 14, -49, 108, 77, -36, -18, -97, -98, -28, -40, 60, 100, 13, 8, -106, -19, -102, -21).toList(), result.privateKey.toList())
        assertEquals(60, result.accountIndex)
    }

    @Test
    fun `Given root key When generate hardened HD key for coin Then return HDKey with correct chain code`() {
        val result = initUseCase().derivePath(seedPhrase, "", "m/44'/60'")

        assertEquals(byteArrayOf(-37, 97, -123, 116, -71, -9, -66, 25, 74, 4, -108, -73, -99, 21, -101, -25, 22, 94, -42, 92, -54, 44, -118, -38, -73, -120, -90, -111, -92, -32, -89, -94).toList(), result.chainCode.toList())
        assertEquals(60, result.accountIndex)
    }

    @Test
    fun `Given root key When generate hardened HD key for account Then return HDKey with correct private key`() {
        val result = initUseCase().derivePath(seedPhrase, "",  "m/44'/60'/0'")

        assertEquals(byteArrayOf(44, -113, 32, 114, 18, 64, 51, 51, -15, -1, 92, 10, 72, -111, -46, 80, -56, -124, 89, 44, 96, -41, -85, 2, 84, -123, -76, -76, 37, 28, -51, -94).toList(), result.privateKey.toList())
        assertEquals(0, result.accountIndex)
    }

    @Test
    fun `Given root key When generate hardened HD key for account Then return HDKey with correct chain code`() {
        val result = initUseCase().derivePath(seedPhrase, "",  "m/44'/60'/0'")

        assertEquals(byteArrayOf(11, 48, -122, -44, -23, -107, 118, -120, 105, 2, 103, 36, -66, -93, -85, -45, 7, -79, -83, 69, -32, 67, -104, 118, -5, 91, 94, 81, -103, -21, 82, 11).toList(), result.chainCode.toList())
        assertEquals(0, result.accountIndex)
    }

    @Test
    fun `Given root key When generate hardened HD key for external Then return HDKey with correct private key`() {
        val result = initUseCase().derivePath(seedPhrase, "",  "m/44'/60'/0'/0")

        assertEquals(byteArrayOf(-58, -6, -15, -76, 99, -114, 34, -97, -74, 76, 48, 88, -1, 58, -71, 35, 111, -67, -45, 83, -67, 20, 89, 25, 55, -109, 8, -75, -112, -86, 34, -14).toList(), result.privateKey.toList())
        assertEquals(0, result.accountIndex)
    }

    @Test
    fun `Given root key When generate hardened HD key for external Then return HDKey with correct chain code`() {
        val result = initUseCase().derivePath(seedPhrase, "",  "m/44'/60'/0'/0")

        assertEquals(byteArrayOf(40, -44, -100, 104, 95, -114, -74, 22, -49, 35, -82, 28, 105, 111, -34, -101, -125, -92, -102, -116, 3, 37, -95, -105, 14, 72, -90, -124, 96, -8, -10, -79).toList(), result.chainCode.toList())
        assertEquals(0, result.accountIndex)
    }

    @Test
    fun `Given root key When generate hardened HD key for index Then return HDKey with correct private key`() {
        val result = initUseCase().derivePath(seedPhrase, "",  "m/44'/60'/0'/0/0")

        assertEquals(byteArrayOf(77, 126, -50, -25, -18, -86, 15, -98, 71, -1, 65, -78, -117, -78, -29, -101, 5, 106, 68, 68, -73, -107, -17, -40, -11, 106, 95, -93, -45, 17, -25, 78).toList(), result.privateKey.toList())
        assertEquals(0, result.accountIndex)
    }

    @Test
    fun `Given root key When generate hardened HD key for index Then return HDKey with correct chain code`() {
        val result = initUseCase().derivePath(seedPhrase, "",  "m/44'/60'/0'/0/0")

        assertEquals(byteArrayOf(-18, 99, -97, 80, 111, -113, 41, -69, -38, 39, 127, 56, 91, -121, -120, -117, 28, -27, -10, -53, -21, -50, -24, -84, 84, 11, -77, -126, -95, -86, -86, 50).toList(), result.chainCode.toList())
        assertEquals(0, result.accountIndex)
    }

    @Test
    fun `Given seed phrase that can cause algo to generate private key not 32 bytes in length When derive path Then return valid private key`() {
        val words = "pioneer edit top review stem rough velvet network album conduct steel talk".split(", ")
        val result = initUseCase().derivePath(words, "",  "m/44'/60'/0'/0/0")

        assertEquals(byteArrayOf(97, 95, 20, -127, 9, -120, 86, 44, 95, -80, -89, 99, -29, 85, -3, -115, -82, 112, 51, -49, 76, -81, 96, -24, 104, 43, 91, -1, -128, 117, 55, 49).toList(), result.privateKey.toList())
        assertEquals(0, result.accountIndex)
    }

    @Test
    fun `Given seed phrase_When generate Polygon HD key_Then return valid Polygon HD key`() {
        val words = "pioneer edit top review stem rough velvet network album conduct steel talk".split(", ")
        val result = initUseCase().invoke(words, "", Blockchain(BlockchainType.Polygon, "Polygon", ""), AddressType.Bip44)

        assertEquals(byteArrayOf(97, 95, 20, -127, 9, -120, 86, 44, 95, -80, -89, 99, -29, 85, -3, -115, -82, 112, 51, -49, 76, -81, 96, -24, 104, 43, 91, -1, -128, 117, 55, 49).toList(), result.privateKey.toList())
        assertEquals(0, result.accountIndex)
    }

    private fun initUseCase() = GenerateHDKeyUseCase()
}