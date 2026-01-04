package com.linh.antelope_qr.domain.usecase

import com.linh.antelope_qr.domain.model.SyncPublicKeyPairsRequest
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EncodeSyncPublicKeyPairsRequestUseCase(
    private val encodeRequestToQrCodeUseCase: EncodeRequestToQrCodeUseCase
) {

    operator fun invoke(accountKeyPairs: AccountKeyPairs): String {
        val request = SyncPublicKeyPairsRequest(
            ownerPublicKey = accountKeyPairs.ownerKeyPair.publicKey.bytes,
            activePublicKey = accountKeyPairs.activeKeyPair.publicKey.bytes
        )

        return encodeRequestToQrCodeUseCase(request)
    }
}