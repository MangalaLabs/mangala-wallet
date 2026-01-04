package com.mangala.wallet.qrcode.domain.qrcheckers

import com.mangala.wallet.model.qr.SyncAccountRequest
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import kotlinx.serialization.json.Json.Default.decodeFromString

class SyncAccountRequestQrCodeChecker: QrCodeTypeChecker {
    override fun canHandle(rawData: String): Boolean {
        return try {
            val result = parse(rawData).getOrNull()

            return result != null
        } catch (e: Exception) {
            false
        }
    }

    override fun parse(rawData: String): Result<QrCodeData> {
        val syncAccountRequest: SyncAccountRequest = decodeFromString(rawData)
        println("ParseQRCodeResultUseCase SyncAccountRequest $syncAccountRequest")
        return Result.success(QrCodeData.SyncAccount(syncAccountRequest))
    }
}