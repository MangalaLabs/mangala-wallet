package com.mangala.wallet.qrcode.domain.qrcheckers

import com.mangala.wallet.qrcode.domain.model.QrCodeData

class AnchorKeycertQrCodeChecker: QrCodeTypeChecker {
    override fun canHandle(rawData: String): Boolean {
        return rawData.startsWith("anchorcert:", ignoreCase = true)
    }

    override fun parse(rawData: String): Result<QrCodeData> {
        return Result.success(QrCodeData.AntelopeKeyCert(rawData))
    }
}