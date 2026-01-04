package com.mangala.wallet.features.chains.bitcoin.data.remote.electrum

data class ElectrumServerConfig(
    val host: String,
    val port: Int = 50002,
    val useTLS: Boolean = true,
    val certificateInfo: CertificateInfo? = null
) {
    data class CertificateInfo(
        val pinnedPublicKey: String? = null,
        val trustAll: Boolean = false
    )
}