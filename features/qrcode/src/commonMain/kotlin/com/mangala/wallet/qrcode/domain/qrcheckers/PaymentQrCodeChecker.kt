package com.mangala.wallet.qrcode.domain.qrcheckers

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.qrcode.domain.model.QrCodeData

class PaymentQrCodeChecker: QrCodeTypeChecker {
    override fun canHandle(rawData: String): Boolean {
        return extractBlockchainType(rawData) != null
    }

    override fun parse(rawData: String): Result<QrCodeData> {
        val address = extractAddress(rawData) ?: return Result.failure(IllegalArgumentException("Invalid address"))
        val amount = extractAmount(rawData)
        val blockchainType = extractBlockchainType(rawData)
        return Result.success(QrCodeData.Payment(address, blockchainType, amount))
    }

    private fun extractBlockchainType(content: String): BlockchainType? {
        return when(val prefix = content.split(":").firstOrNull()) {
            "smartchain" -> BlockchainType.BinanceSmartChain
            null -> null
            else -> {
                val blockchainType = BlockchainType.fromUid(prefix)
                if (blockchainType is BlockchainType.Unsupported) null else blockchainType
            }
        }
    }

    private fun extractAddress(content: String): String? {
        return content.split(":").getOrNull(1)?.split("?")?.getOrNull(0) // TODO: Validate address
    }

    private fun extractAmount(content: String): String? {
        val regex = "amount=([^&]*)".toRegex()
        val matchResult = regex.find(content) ?: return null
        return matchResult.destructured.component1()
    }
}