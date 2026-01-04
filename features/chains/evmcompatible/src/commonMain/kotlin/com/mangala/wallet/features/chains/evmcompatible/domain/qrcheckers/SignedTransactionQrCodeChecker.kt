package com.mangala.wallet.features.chains.evmcompatible.domain.qrcheckers

import com.mangala.wallet.qrcode.domain.model.QrCodeData
import kotlinx.serialization.json.Json.Default.decodeFromString
import com.mangala.wallet.features.chains.evmcompatible.model.SignedTransactionResponse
import com.mangala.wallet.qrcode.domain.qrcheckers.QrCodeTypeChecker

class SignedTransactionQrCodeChecker: QrCodeTypeChecker {
    override fun canHandle(rawData: String): Boolean {
        return try {
            val parseResult = parse(rawData).getOrNull()

            parseResult != null
        } catch (e: Exception) {
            false
        }
    }

    override fun parse(rawData: String): Result<QrCodeData> {
        val signedTransactionResponse: SignedTransactionResponse = decodeFromString(rawData)
        return Result.success(QrCodeData.SignedTransaction(signedTransactionResponse))
    }
}