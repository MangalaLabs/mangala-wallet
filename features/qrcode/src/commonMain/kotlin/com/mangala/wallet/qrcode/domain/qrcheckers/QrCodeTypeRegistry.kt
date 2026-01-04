package com.mangala.wallet.qrcode.domain.qrcheckers

import com.mangala.wallet.qrcode.domain.model.QrCodeData

class QrCodeTypeRegistry(
    private val typeCheckers: List<QrCodeTypeChecker>
) {
    fun parseQRCode(rawData: String): Result<QrCodeData> {
        println("QrCodeTypeRegistry: checkers size = ${typeCheckers.size}")
        return typeCheckers
            .firstOrNull { it.canHandle(rawData) }
            ?.parse(rawData) ?: Result.failure(IllegalArgumentException("Unknown QR code type"))
    }
}