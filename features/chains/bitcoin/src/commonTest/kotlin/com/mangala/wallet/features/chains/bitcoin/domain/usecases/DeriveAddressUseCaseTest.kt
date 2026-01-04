package com.mangala.wallet.features.chains.bitcoin.domain.usecases

import com.mangala.wallet.core.address.domain.usecases.DeriveBitcoinAddressUseCase
import com.mangala.wallet.model.blockchain.AddressType
import kotlin.test.Test
import kotlin.test.assertEquals

class DeriveAddressUseCaseTest {

    @Test
    fun `Given public key_When generate Bitcoin legacy address P2PKH_Then receive valid Bitcoin address`() {
        val useCase = initUseCase()
        // public key from mnemonic: waste smoke pepper liberty slow hollow symbol utility spatial city uniform music
        // from derivation path m/44'/0'/0'/0/0
        val publicKey = byteArrayOf(4, -48, 52, -115, -120, -120, 113, 96, 75, -120, -50, 50, 94, 127, -3, -59, -11, -85, 100, 18, -70, 108, 53, 110, 83, 82, 21, 68, -40, 89, -1, 15, 59, 48, -42, 95, 96, 19, 112, -38, -119, 28, 118, 35, 37, 35, 116, -84, -13, 47, 46, -25, 42, -86, 100, 97, -44, -5, -102, -39, 73, -38, 51, -26, -54)
        val address = useCase(publicKey, AddressType.Bip44, isTestnet = false)

        assertEquals("1MjD9mpNaYc7S8fpvgxZ1gMoXhdPvkg16q", address)
    }

    @Test
    fun `Given public key 2_When generate Bitcoin legacy address P2PKH_Then receive valid Bitcoin address`() {
        val useCase = initUseCase()
        // public key from mnemonic: waste smoke pepper liberty slow hollow symbol utility spatial city uniform music
        // from derivation path m/44'/0'/0'/0/0
        val publicKey = byteArrayOf(4, -64, -38, -35, -118, 66, -102, -48, -29, -10, -78, 92, -89, 69, -28, 8, 36, -93, -57, 0, 86, 101, -83, 10, 57, -27, 11, 52, -82, -30, 95, -64, -42, -70, 79, -128, -120, 113, -64, 100, -39, 6, 4, 30, 121, 54, -119, 78, -113, 81, 99, -24, 113, 120, 63, 0, 93, -52, -59, 13, -96, 70, -105, 51, -119)
        val address = useCase(publicKey, AddressType.Bip44, isTestnet = false)

        assertEquals("1MPJcN9U9RwvmQKfQWzD68xDxrGTXep3bn", address)
    }

    @Test
    fun `Given public key 3_When generate Bitcoin legacy address P2PKH_Then receive valid Bitcoin address`() {
        val useCase = initUseCase()
        // public key from mnemonic: confirm crater erode funny truck mobile galaxy shed never brave budget spell detail bright need
        // from derivation path m/44'/0'/0'/0/0
        val publicKey = byteArrayOf(4, -79, 68, -29, 103, -12, 110, 84, -14, -48, 126, 127, -109, -32, 59, 52, 18, -15, -72, -114, 16, -128, -62, 118, 120, -67, -70, 6, 59, 115, 90, 114, -8, 42, -56, -13, -120, -88, -51, 115, -25, -108, -61, 4, -36, 48, -82, -58, 104, 92, -104, 87, 14, 127, 102, 25, -8, 42, 12, -53, -55, 62, -94, -75, 96)
        val address = useCase(publicKey, AddressType.Bip44, isTestnet = false)

        assertEquals("13dQKKMgBv2mTCQEobJL7mkawCQRNoc9c7", address)
    }

    @Test
    fun `Given public key_When generate Bitcoin BIP49 P2SH-P2WPKH_Then receive valid Bitcoin address`() {
        val useCase = initUseCase()
        // public key from mnemonic: waste smoke pepper liberty slow hollow symbol utility spatial city uniform music
        // from derivation path m/49'/0'/0'/0/0
        val publicKey = byteArrayOf(4, 123, 102, -111, 5, -82, 81, -35, -83, 2, 87, 52, -8, 43, 45, -98, -81, -64, 55, 64, 55, 66, -34, -10, -88, -91, 63, -26, -49, 81, -81, 105, 23, 81, -43, -87, 98, -105, -32, 80, 107, -64, -17, -76, 55, 24, 37, -3, -57, -35, -42, 0, 85, 21, 20, -45, -66, -89, -127, -16, 31, 41, 75, -33, 68)
        val address = useCase(publicKey, AddressType.Bip49, isTestnet = false)

        assertEquals("3EsuskW7URDUZVdK6Ac8dqT8bFwRZJDj1o", address)
    }

    @Test
    fun `Given public key 2_When generate Bitcoin BIP49 P2SH-P2WPKH_Then receive valid Bitcoin address`() {
        val useCase = initUseCase()
        // public key from mnemonic: waste smoke pepper liberty slow hollow symbol utility spatial city uniform music
        // from derivation path m/49'/0'/0'/0/1
        val publicKey = byteArrayOf(4, 6, 114, 117, 24, -112, -110, 26, 89, -75, -96, -10, 88, 77, -69, -60, 35, -119, 125, -53, 39, -32, 78, 4, -69, 10, -2, 123, 76, -125, 125, -103, 12, -9, 32, -47, 118, 16, -15, -66, -35, 81, -40, 78, 56, 72, -53, 41, 127, 114, 25, -109, -67, -111, -22, 99, 3, -38, -73, -111, 69, 7, -9, -9, 75)
        val address = useCase(publicKey, AddressType.Bip49, isTestnet = false)

        assertEquals("3F9ovyvXufL4Z1yBY4onHmnQUQSNdMA1DB", address)
    }

    @Test
    fun `Given public key 3_When generate Bitcoin BIP49 P2SH-P2WPKH_Then receive valid Bitcoin address`() {
        val useCase = initUseCase()
        // public key from mnemonic: confirm crater erode funny truck mobile galaxy shed never brave budget spell detail bright need
        // from derivation path m/49'/0'/0'/0/0
        val publicKey = byteArrayOf(4, 67, 70, -121, -31, 101, 3, -40, -38, 41, 123, -86, -119, -34, 111, 115, 51, 100, 107, -79, -119, -11, -74, 102, 46, -46, 125, -9, 116, -128, -66, -73, -36, 7, -77, -11, 54, 8, 56, -51, -12, 31, 89, 47, -86, 53, 89, -30, -80, 45, 75, -17, 28, -8, -29, 99, 108, -107, -114, 51, 125, -3, 80, -16, 118)
        val address = useCase(publicKey, AddressType.Bip49, isTestnet = false)

        assertEquals("3FnDPW7kAC1z7o23onjvn23StgtJCXUDHw", address)
    }

    @Test
    fun `Given public key_When generate Bitcoin BIP84 Address_Then receive valid Bitcoin address`() {
        val useCase = initUseCase()
        // public key from mnemonic: waste smoke pepper liberty slow hollow symbol utility spatial city uniform music
        // from derivation path m/84'/0'/0'/0/0
        val publicKey = byteArrayOf(4, 12, -15, -75, 61, 99, -33, -33, 19, 108, -83, -117, 87, 97, 70, 9, -89, 9, 110, -97, 31, -96, -113, -122, -110, -6, 59, 74, 32, -123, 39, 114, 70, -18, 19, -57, -125, 91, -12, -31, 122, -75, -18, -14, 106, 52, -92, 16, -2, 113, -6, -42, 37, -22, -69, -109, -53, 39, -119, 16, 11, -27, -36, 22, 110)
        val address = useCase(publicKey, AddressType.Bip84, isTestnet = false)

        assertEquals("bc1qhvklydpksfeqgcft0ck97ejexdj4rpc52y0469", address)
    }

    @Test
    fun `Given public key 2_When generate Bitcoin BIP84 Address_Then receive valid Bitcoin address`() {
        val useCase = initUseCase()
        // public key from mnemonic: waste smoke pepper liberty slow hollow symbol utility spatial city uniform music
        // from derivation path m/84'/0'/0'/0/1
        val publicKey = byteArrayOf(4, -39, 90, -101, -57, -61, -29, 44, -65, 78, 86, 57, -114, 28, 48, -55, 119, 14, -53, 102, 47, -74, 29, -32, 66, 126, -117, -25, 68, -105, 86, 83, 49, 52, -85, -93, 21, -98, 78, -46, -74, 107, 48, 108, -5, -5, -110, 66, -31, -46, 80, 31, -76, -82, 61, 123, 58, -70, -128, 37, -9, 87, -77, -66, 110)
        val address = useCase(publicKey, AddressType.Bip84, isTestnet = false)

        assertEquals("bc1qgc3wsz2m9qyndp26520f5n592yfr8qpzxzxhxk", address)
    }

    @Test
    fun `Given public key 3_When generate Bitcoin BIP84 Address_Then receive valid Bitcoin address`() {
        val useCase = initUseCase()
        // public key from mnemonic: confirm crater erode funny truck mobile galaxy shed never brave budget spell detail bright need
        // from derivation path m/84'/0'/0'/0/0
        val publicKey = byteArrayOf(4, 32, 36, 110, 106, -100, -29, 77, -44, -108, -52, -55, -26, 123, -9, -107, -24, 124, 47, 101, -96, 120, -94, -79, 79, -34, -76, -105, -69, -17, 54, -85, 20, 92, 13, 34, 26, 88, -40, 107, -60, -88, 51, 118, -115, 117, 100, 27, 108, 100, -58, 37, 81, -74, 36, 21, 29, -76, 125, -71, -43, -38, -66, 43, -68)
        val address = useCase(publicKey, AddressType.Bip84, isTestnet = false)

        assertEquals("bc1qyutf3t57dl9wmgfwpn0ecfupaw4qwq7l2kvw7d", address)
    }

    fun initUseCase() = DeriveBitcoinAddressUseCase()
}