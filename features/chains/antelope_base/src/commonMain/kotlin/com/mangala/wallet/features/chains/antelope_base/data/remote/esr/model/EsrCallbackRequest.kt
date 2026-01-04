package com.mangala.wallet.features.chains.antelope_base.data.remote.esr.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EsrCallbackRequest(
    @SerialName("sig") val firstSignature: String,
    @SerialName("tx") val transactionId: String, // Transaction ID as HEX-encoded string.
    @SerialName("bn") val blockNumber: String?, // Block number hint (only present if transaction was broadcast).
    @SerialName("sa") val signerAccount: String, // Signer authority, aka account name.
    @SerialName("sp") val signerPermission: String, // Signer permission, e.g. "active".
    @SerialName("rbn") val referenceBlockNum: String, // Reference block num used when resolving request.
    @SerialName("rid") val referenceBlockId: String, // Reference block id used when resolving request.
    @SerialName("req") val request: String, // The originating signing request packed as a uri string.
    @SerialName("ex") val expirationTime: String, // Expiration time used when resolving request.
    @SerialName("cid") val resolvedChainId: String? = null, // The resolved chain id.
    @SerialName("link_ch") val linkChannel: String? = null,
    @SerialName("link_key") val linkKey: String? = null,
    @SerialName("link_name") val linkName: String? = null,
    val sig0: String? = null, // All signatures 0-indexed as `sig0`, `sig1`, etc.
    val sig1: String? = null,
    val sig2: String? = null,
    val sig3: String? = null,
    val sig4: String? = null,
    val sig5: String? = null
)