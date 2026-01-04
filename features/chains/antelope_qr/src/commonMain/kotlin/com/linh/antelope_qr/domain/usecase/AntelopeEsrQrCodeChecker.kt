package com.linh.antelope_qr.domain.usecase

import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.qrcheckers.QrCodeTypeChecker

class AntelopeEsrQrCodeChecker(private val decodeEsrUseCase: DecodeEsrUseCase) : QrCodeTypeChecker {
    override fun canHandle(rawData: String): Boolean {
        val hasEsrScheme = rawData.startsWith("esr:") || rawData.startsWith("web+esr:")

        if (!hasEsrScheme) return false

        try {
            // Test parse to make sure it's a valid ESR
            val esrSigningRequestArgs = decodeEsrUseCase.canDecode(rawData)
            return true
        } catch (e: Exception) {
            println("ParseQRCodeResultUseCase failed to parse Esr $e")
            return false
        }
    }

    override fun parse(rawData: String): Result<QrCodeData> {
        return try {
            Result.success(QrCodeData.Esr(rawData))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}