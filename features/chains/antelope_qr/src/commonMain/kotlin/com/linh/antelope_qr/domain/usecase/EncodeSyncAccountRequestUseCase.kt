package com.linh.antelope_qr.domain.usecase

import com.linh.antelope_qr.domain.model.SyncAccountRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EncodeSyncAccountRequestUseCase(
    private val json: Json
) {

    operator fun invoke(ownerPublicKey: ByteArray, activePublicKey: ByteArray, accountName: String): String {
        val request = SyncAccountRequest(
            ownerPublicKey = ownerPublicKey,
            activePublicKey = activePublicKey,
            accountName = accountName
        )
        return json.encodeToString(request)
    }
}