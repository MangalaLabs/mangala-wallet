package com.mangala.wallet.features.conversationui.domain.qrscanner

/**
 * Wrapper interface for QR scanning functionality
 * This avoids direct dependency on the scanqr module from the domain layer
 */
interface QrScannerWrapper {
    fun scanQrCode(onResult: (String) -> Unit)
}