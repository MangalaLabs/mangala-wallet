package com.mangala.wallet.features.chains.evmcompatible.domain.qrcheckers

import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EncodeSignTransactionRequestUseCase
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.qrcheckers.QrCodeTypeChecker

class SignTransactionRequestQrCodeChecker(private val encodeSignTransactionRequestUseCase: EncodeSignTransactionRequestUseCase):
    QrCodeTypeChecker {
    override fun canHandle(rawData: String): Boolean {
        return try {
            val parseResult = parse(rawData).getOrNull()

            parseResult != null
        } catch (e: Exception) {
            false
        }
    }

    override fun parse(rawData: String): Result<QrCodeData> {
        val signTransactionRequest = encodeSignTransactionRequestUseCase.decode(rawData)
        return Result.success(QrCodeData.NotSignedTransaction(signTransactionRequest))
    }
}