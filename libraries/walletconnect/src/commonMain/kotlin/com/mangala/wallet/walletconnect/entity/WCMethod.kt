/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
enum class WCMethodType(val value: String) {
    @SerialName("wc_sessionRequest")
    SESSION_REQUEST("wc_sessionRequest"),

    @SerialName("wc_sessionUpdate")
    SESSION_UPDATE("wc_sessionUpdate"),

//    @SerialName("eth_sign")
//    ETH_SIGN,
//
//    @SerialName("personal_sign")
//    ETH_PERSONAL_SIGN,
//
//    @SerialName("eth_signTypedData")
//    ETH_SIGN_TYPE_DATA,
//
//    @SerialName("eth_signTransaction")
//    ETH_SIGN_TRANSACTION,
//
//    @SerialName("eth_sendTransaction")
//    ETH_SEND_TRANSACTION,
//
//    @SerialName("get_accounts")
//    GET_ACCOUNTS,
//
//    @SerialName("wallet_switchEthereumChain")
//    SWITCH_ETHEREUM_CHAIN,
//
//    @SerialName("wallet_addEthereumChain")
//    ADD_ETHEREUM_CHAIN,

}

sealed class WCMethod(val requestId: Long) {
    data class Request(
        val id: Long,
        val type: WCMethodType,
        val params: List<Params>,
    ) : WCMethod(id) {
        sealed class Params {
            @Serializable
            data class Request(
                val chainId: Long? = null,
                val peerId: String,
                val peerMeta: WCPeerMeta? = null
            ) : Params()

            @Serializable
            data class Update(
                val approved: Boolean,
                val chainId: Long? = null,
                val accounts: List<String>? = null,
            ) : Params()
        }
    }

    data class Response(
        val id: Long,
        val result: Any
    ) : WCMethod(requestId = id) {
        @Serializable
        data class RequestResponse(
            val peerId: String,
            val peerMeta: WCPeerMeta? = null,
            val approved: Boolean,
            val chainId: Long? = null,
            val accounts: List<String>? = null
        )

    }

    data class CustomRequest(
        val id: Long,
        val method: String,
        val params: JsonArray,
    ) : WCMethod(requestId = id)

    data class Error(
        val id: Long,
        val error: String,
        val code: Long,
    ) : WCMethod(requestId = id)
}
