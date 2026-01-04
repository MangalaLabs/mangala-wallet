package com.mangala.wallet.qrcode.domain.qrcheckers

import com.mangala.wallet.qrcode.domain.model.QrCodeData

class AntelopeImportAccountQrCodeChecker: QrCodeTypeChecker {
    override fun canHandle(rawData: String): Boolean {
        return rawData.startsWith("5") || rawData.startsWith("PVT_K1_") || rawData.startsWith("PVT_K1_") || rawData.startsWith("PVT_WA_")
    }

    override fun parse(rawData: String): Result<QrCodeData> {
        return Result.success(QrCodeData.ImportAccount(rawData))
    }
}