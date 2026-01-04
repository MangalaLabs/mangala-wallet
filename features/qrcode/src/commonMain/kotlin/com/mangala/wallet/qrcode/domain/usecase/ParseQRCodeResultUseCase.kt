package com.mangala.wallet.qrcode.domain.usecase

import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.qrcheckers.QrCodeTypeRegistry

class ParseQRCodeResultUseCase(
    private val qrCodeTypeRegistry: QrCodeTypeRegistry
) {

    operator fun invoke(input: String): QrCodeData? {
        val result = qrCodeTypeRegistry.parseQRCode(input)

        return result.getOrNull()
    }
}