package com.mangala.wallet.cryptography

import kotlin.test.Test
import kotlin.test.assertEquals

class PBKDF2SHA512Test {

    @Test
    fun `Given valid input When call pbkdf2sha512 then returns correct derived key`() {
        val result = pbkdf2sha512(
            password = "oxygen twenty wage beyond waste gift business thing fire shop trust bag pencil stick guard",
            salt = "mnemonic",
            rounds = 2048,
            derivedKeyLength = 64
        )

        assertEquals(byteArrayOf(-32, -69, 47, -45, 81, 93, 5, -113, -29, 33, 63, 85, -111, -61, -126, 10, -107, 87, -14, 97, 57, -97, -109, -49, -61, 90, -100, -37, -15, 124, 55, 85, -125, 53, -43, -19, -121, 5, 12, 31, -84, -67, -18, -60, -124, 77, -116, -117, 90, -86, -123, 54, 51, -19, 78, 53, -7, 5, 68, -26, 97, -115, -31, 105).toList(), result.toList())
        assertEquals(64, result.toList().size)
    }
}