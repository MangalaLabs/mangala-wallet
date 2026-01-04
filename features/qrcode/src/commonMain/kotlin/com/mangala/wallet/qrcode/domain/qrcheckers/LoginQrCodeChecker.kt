package com.mangala.wallet.qrcode.domain.qrcheckers

import com.mangala.wallet.qrcode.domain.model.QrCodeData

class LoginQrCodeChecker: QrCodeTypeChecker {
    override fun canHandle(rawData: String): Boolean {
        return rawData.startsWith("login:")
    }

    override fun parse(rawData: String): Result<QrCodeData> {
        return Result.success(QrCodeData.Login)
    }
}