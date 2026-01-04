package com.mangala.wallet.features.chains.antelope_base.domain.model.esr

data class EsrCallbackData(
    val signatures: List<String>,
    val transactionId: String, // Transaction ID as HEX-encoded string.
    val blockNumber: String?, // Block number hint (only present if transaction was broadcast).
    val signerAccount: String, // Signer authority, aka account name.
    val signerPermission: String, // Signer permission, e.g. "active".
    val referenceBlockNum: String, // Reference block num used when resolving request.
    val referenceBlockId: String, // Reference block id used when resolving request.
    val request: String, // The originating signing request packed as a uri string. (e.g esr://...)
    val expirationTime: String, // Expiration time used when resolving request.
    val resolvedChainId: String?, // The resolved chain id.
    val anchorLinkChannel: String?, // Anchor Link callback URL
    val anchorLinkReceivePublicKey: String?, // Anchor Link receive public key
    val anchorLinkName: String?, // Anchor Link session name
)