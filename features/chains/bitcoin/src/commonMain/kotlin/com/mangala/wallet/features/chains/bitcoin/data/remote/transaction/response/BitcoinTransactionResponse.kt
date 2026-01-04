package com.mangala.wallet.features.chains.bitcoin.data.remote.transaction.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BitcoinTransactionResponse(
    @SerialName("txid")
    val txid: String? = null,
    @SerialName("version")
    val version: Int? = null,
    @SerialName("locktime")
    val locktime: Int? = null,
    @SerialName("vin")
    val vin: List<Vin?>? = null,
    @SerialName("vout")
    val vout: List<Vout?>? = null,
    @SerialName("size")
    val size: Int? = null,
    @SerialName("weight")
    val weight: Int? = null,
    @SerialName("sigops")
    val sigops: Int? = null,
    @SerialName("fee")
    val fee: Int? = null,
    @SerialName("status")
    val status: Status? = null
) {
    @Serializable
    data class Vin(
        @SerialName("txid")
        val txid: String? = null,
        @SerialName("vout")
        val vout: Int? = null,
        @SerialName("prevout")
        val prevout: Prevout? = null,
        @SerialName("scriptsig")
        val scriptsig: String? = null,
        @SerialName("scriptsig_asm")
        val scriptsigAsm: String? = null,
        @SerialName("witness")
        val witness: List<String?>? = null,
        @SerialName("is_coinbase")
        val isCoinbase: Boolean? = null,
        @SerialName("sequence")
        val sequence: Long? = null
    ) {
        @Serializable
        data class Prevout(
            @SerialName("scriptpubkey")
            val scriptpubkey: String? = null,
            @SerialName("scriptpubkey_asm")
            val scriptpubkeyAsm: String? = null,
            @SerialName("scriptpubkey_type")
            val scriptpubkeyType: String? = null,
            @SerialName("scriptpubkey_address")
            val scriptpubkeyAddress: String? = null,
            @SerialName("value")
            val value: Long? = null
        )
    }

    @Serializable
    data class Vout(
        @SerialName("scriptpubkey")
        val scriptpubkey: String? = null,
        @SerialName("scriptpubkey_asm")
        val scriptpubkeyAsm: String? = null,
        @SerialName("scriptpubkey_type")
        val scriptpubkeyType: String? = null,
        @SerialName("scriptpubkey_address")
        val scriptpubkeyAddress: String? = null,
        @SerialName("value")
        val value: Long? = null
    )

    @Serializable
    data class Status(
        @SerialName("confirmed")
        val confirmed: Boolean? = null,
        @SerialName("block_height")
        val blockHeight: Int? = null,
        @SerialName("block_hash")
        val blockHash: String? = null,
        @SerialName("block_time")
        val blockTime: Int? = null
    )
}