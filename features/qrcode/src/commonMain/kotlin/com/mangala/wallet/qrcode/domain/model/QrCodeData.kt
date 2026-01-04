package com.mangala.wallet.qrcode.domain.model

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.qr.SyncAccountRequest

sealed class QrCodeData {
    data class Payment(val address: String, val chain: BlockchainType? = null, val amount: String?) : QrCodeData()
    data object WalletConnect: QrCodeData()
    data object Login: QrCodeData()
    data class NotSignedTransaction(val signTransactionRequest: Any): QrCodeData()
    data class SignedTransaction(val signedTransactionResponse: Any): QrCodeData()
    data class SyncAccount(val syncAccountRequest: SyncAccountRequest): QrCodeData()
    data class AntelopeCreateAccountForFriend(val request: Any): QrCodeData()
    data class Esr(val esrUri: String) : QrCodeData()
    data class ImportAccount(val privateKey: String): QrCodeData()
    data class AntelopeKeyCert(val keyCert: String) : QrCodeData()
//    data class AntelopeSyncAccount(val syncAccountRequest: AntelopeSyncAccountRequest): QrCodeData()
}
