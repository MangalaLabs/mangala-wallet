package com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.anchorlink

import com.mangala.wallet.antelope_key_manager.EosKeyManager
import com.mangala.wallet.features.chains.antelope_base.domain.model.esr.anchorlink.AnchorLinkSession
import com.mangala.wallet.utils.ext.toHexString
import com.mangala.wallet.utils.hexStringToByteArray
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlin.test.Test
import kotlin.test.assertEquals

class DecodeAnchorLinkSealedMessageUseCaseTest {

    @MockK
    lateinit var eosKeyManager: EosKeyManager

    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun `Given a sealed Anchor Link ESR request, when decode, then return decrypted ESR URI`() {
        val data =
            "0003ed00b1b71e196c1fc28a2bc5c1e5b9ef5d611742343039a42431605bcc993aad17b6ff79a0da7195d001b509d06591c7ca6895b6b074eb5dfeafbe40de31a1a05ec59dc74a541fac1a2c939418d64cd5a916c4ac53e8141d4a369195e95e04a9d7d543e7d6d9f557183b078eab343abc789bf375a28484360a160620b7f863a9cafcb904d2e8dc4dc6896cac3dc811c388b060aada79761cc62b7381910df69d96b0dbccddd8e404c0bd0932c4e676314c9d510f1bddaa4ed6829fe72c5a944cf05ff39a9d8bcb53819dd96a724de0a1b45cc4b8e9b13de3049656409e7f5cc98832df6f48e5f3fc5f71c2697abbd082ee42e1fbdb77fd76f65227f336b6"
        val sut = DecodeAnchorLinkSealedMessageUseCase(eosKeyManager)
        every { eosKeyManager.getPrivateKey(any()) } returns EosPrivateKey.fromString("5HxGAg2NHHYFVzFnjzr3qJ7qzSUR8UJkePp2WzvoruC1rsBC9mu")

        val session = AnchorLinkSession(
            "",
            "EOS8dcSqNjVX2qoCMPPhmBncV8j2z1ysySyTCZqVzAbUPxFYWHxux",
            "5HxGAg2NHHYFVzFnjzr3qJ7qzSUR8UJkePp2WzvoruC1rsBC9mu",
            "",
            ""
        )

        val result = sut(session, data.hexStringToByteArray())

        assertEquals(
            "esr:gmMsfmIRpc7x7DpLh8nvg-zz9VdvrLYRihbJ-mIxXW5CYY4vEwMKYASTrwxCQZRH8V47xgZTtuue033ugARWvDUyUoAJwGgBdYhWFlf_YDCDyTqjpKSg2EpfPzlJLzEvOSO_SC8nMy9b39Ik1SApxdRCN9nU1EzXJM3YRDfRKNFI19AwzTw10cDIwizJgJEFpJQlOP5aGgA",
            result
        )
    }
}