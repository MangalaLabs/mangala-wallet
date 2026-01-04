/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect

import com.mangala.wallet.walletconnect.entity.WCEncryptedPayload
import com.mangala.wallet.walletconnect.utils.JSON
import com.mangala.wallet.walletconnect.utils.WCCipher
import kotlin.test.Test
import kotlin.test.assertEquals

internal class WCCipherTest {
    private val payload = """
        {"data":"ec47bb1b08af7f22e05e81f5200852a92228d00eebe4905d163e25272aced8bf7b88b7935741e290d077a8c789dbb47b0a82a3695d9e27b835e079fc6c0da9a79a619ad49d7742d29c58885c1bd0612572c1dbefe55c51b95a862f2cfb333c6c7b57c3cce930f597a9e4d92d1cc2567e100c0f537214d5aed00831144d6b85ed479f4f235a163664cfee45c0677403baafbcbccacebada26271d8bf47516d46adc6f769392c11e328e36c3d5e0966a97032fe183caa1516703ac081b6bf0ec892bc8a4049767f667f3218f3817236828f424685240a5c19ca4c83aad4e4d2e628b0b9e163d4926ab2dd9dcb4efa0982d1612510d7c3d2242c8f589ce04711a38ac83465e3aa5053d9830d14e9e57133f041482256dd9ec34a00cdcf477300cfe4663487cb7b3d9060614d483aa5fec09ca4e40877048a8130ee319e7dae63cec","hmac":"0ae384630625426546d07c8a4c0be44f52c45d82c9dd63c36d488254ac49212f","iv":"b60a6fadfb776501095f33d9ac4e606b"}
    """.trimIndent()

    private val key = "ecb21cab633bc2e7ba71a7b4d6792788379b7219522ea2ec761cc08c701a8724"

    private val decrypted = """
        {"id":1662023151146552,"jsonrpc":"2.0","method":"wc_sessionRequest","params":[{"peerId":"8f434505-b7dc-4034-99e8-d35a4fb28830","peerMeta":{"description":"","url":"https://example.walletconnect.org","icons":["https://example.walletconnect.org/favicon.ico"],"name":"WalletConnect Example"},"chainId":null}]}
    """.trimIndent()
    @Test
    fun decrypt() {
       val decrypt =  WCCipher.decrypt(
            payload = payload,
            key = key
        )

        assertEquals(decrypted, decrypt)

    }

    @Test
    fun encrypet() {
        val encrypt = WCCipher.encrypt(
            payload = decrypted,
            key = key
        )

        val decrypt =  WCCipher.decrypt(
            payload = JSON.encodeToString(WCEncryptedPayload.serializer(), encrypt),
            key = key
        )

        assertEquals(
            decrypt,
            decrypted
        )
    }
}
