package com.mangala.wallet.qrcode.domain.qrcheckers

import com.mangala.wallet.qrcode.domain.model.QrCodeData

class AddressQrCodeChecker: QrCodeTypeChecker {
    override fun canHandle(rawData: String): Boolean {
        return rawData.startsWith("0x") // TODO: Handle other blockchains (e.g. Bitcoin)
    }

    override fun parse(rawData: String): Result<QrCodeData> {
        // TODO: Validate address
        return Result.success(QrCodeData.Payment(rawData, null, null))
    }
}