package com.mangala.wallet.twofactorauth.data.model

/**
 * Represents the result of setting up 2FA
 */
data class TotpSetupResult(
    val secret: String,
    val qrCodeUri: String,
    val backupCodes: List<String>
) {
    override fun toString(): String {
        return "qrCode: $qrCodeUri"
    }
}