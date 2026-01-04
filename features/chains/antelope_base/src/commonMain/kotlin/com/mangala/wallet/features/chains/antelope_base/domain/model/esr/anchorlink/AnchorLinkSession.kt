package com.mangala.wallet.features.chains.antelope_base.domain.model.esr.anchorlink

data class AnchorLinkSession(
    val accountName: String,
    val requestKey: String,
    val receiveKey: String,
    val url: String,
    val name: String
)