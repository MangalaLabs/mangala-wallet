package com.mangala.wallet.qrcode.domain.qrcheckers

import com.mangala.wallet.qrcode.domain.model.QrCodeData

interface QrCodeTypeChecker {
    fun canHandle(rawData: String): Boolean
    fun parse(rawData: String): Result<QrCodeData>
}