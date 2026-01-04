/*
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mangala.antelope.base.api.model

import com.mangala.wallet.utils.ext.jsonArrayOrNull
import com.mangala.wallet.utils.ext.jsonObjectOrNull
import com.mangala.wallet.utils.ext.stringOrNull
import kotlinx.serialization.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

@Serializable
data class GetActionsResponse(
    @SerialName("query_time_ms") val queryTimeMs: Double? = null,
    @SerialName("cached") val cached: Boolean? = null,
    @SerialName("lib") val lib: Int? = null,
    @SerialName("last_indexed_block") val lastIndexedBlock: Long? = null,
    @SerialName("last_indexed_block_time") val lastIndexedBlockTime: String? = null,
    @SerialName("total") val total: Total? = null,
    @SerialName("actions") val actions: List<EosAction>? = null,
)

@Serializable
data class Total(
    @SerialName("value") val value: Int? = null,
    @SerialName("relation") val relation: String? = null,
)

@Serializable
data class EosAction(
//    @SerialName("@timestamp") val timestamp: String?,
    @SerialName("timestamp") val timestampSimple: String? = null,
    @SerialName("block_num") val blockNum: Long? = null,
    @SerialName("block_id") val blockId: String? = null,
    @SerialName("trx_id") val trxId: String? = null,
    @SerialName("act") val act: Act? = null,
    @SerialName("receipts") val receipts: List<Receipt>?,
    @SerialName("global_sequence") val globalSequence: Long? = null,
    @SerialName("producer") val producer: String? = null,
    @SerialName("action_ordinal") val actionOrdinal: Int? = null,
    @SerialName("creator_action_ordinal") val creatorActionOrdinal: Int? = null,
    @SerialName("cpu_usage_us") val cpuUsageUs: Int? = null,
    @SerialName("net_usage_words") val netUsageWords: Int? = null,
    @SerialName("signatures") val signatures: List<String>? = null,
    @SerialName("account_action_seq") val accountActionSeq: Long? = null,
    @SerialName("action_trace") val actionTrace: ActionTrace? = null,
    @SerialName("block_time") val blockTime: String? = null,
    @SerialName("global_action_seq") val globalActionSeq: Long? = null,
    @SerialName("irreversible") val irreversible: Boolean? = null,
)

@Serializable
data class Act(
    @SerialName("account") val account: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("authorization") val authorization: List<EosAuthorization>? = null,
    @SerialName("data") val data: JsonObject? = null,
) {
    val actionId = "$account:$name"

    val amount: JsonElement? by lazy { data?.get("amount") }
    val stakeCpuQuantity: String? by lazy { data?.get("stake_cpu_quantity")?.stringOrNull }
    val stakeNetQuantity: String? by lazy { data?.get("stake_net_quantity")?.stringOrNull }
    val from: String? by lazy { data?.get("from")?.stringOrNull }
    val to: String? by lazy { data?.get("to")?.stringOrNull }
    val symbol: String? by lazy { data?.get("symbol")?.stringOrNull }
    val receiver: String? by lazy { data?.get("receiver")?.stringOrNull }
    val transfer: Boolean? by lazy { data?.get("transfer")?.jsonPrimitive?.booleanOrNull }
    val payer: String? by lazy { data?.get("payer")?.stringOrNull }
    private val quant: JsonElement? by lazy { data?.get("quant") }
    val loanPayment: String? by lazy { data?.get("loan_payment")?.stringOrNull }
    val quantity: String? by lazy { data?.get("quantity")?.stringOrNull }
    val bytes: Long? by lazy { data?.get("bytes")?.jsonPrimitive?.longOrNull }
    val ramBytes: Long? by lazy { data?.get("ram_bytes")?.jsonPrimitive?.longOrNull }
    val owner: JsonElement? by lazy { data?.get("owner") }
    val cpuFrac: Long? by lazy { data?.get("cpu_frac")?.jsonPrimitive?.longOrNull }
    val days: Int? by lazy { data?.get("days")?.jsonPrimitive?.intOrNull }
    val maxPayment: String? by lazy { data?.get("max_payment")?.stringOrNull }
    val netFrac: Long? by lazy { data?.get("net_frac")?.jsonPrimitive?.longOrNull }
    val bid: String? by lazy { data?.get("bid")?.stringOrNull }
    val bidder: String? by lazy { data?.get("bidder")?.stringOrNull }
    val newAct: String? by lazy { data?.get("newact")?.stringOrNull }
    val code: String? by lazy { data?.get("code")?.stringOrNull }
    val type: String? by lazy { data?.get("type")?.stringOrNull }
    val requirement: String? by lazy { data?.get("requirement")?.stringOrNull }
    val permission: String? by lazy { data?.get("permission")?.stringOrNull }
    val message: String? by lazy { data?.get("message")?.stringOrNull }
    val user: String? by lazy { data?.get("user")?.stringOrNull }
    val reward: String? by lazy { data?.get("reward")?.stringOrNull }
    val stakedAmount: String? by lazy { data?.get("staked_amount")?.stringOrNull }
    val memo: String? by lazy { data?.get("memo")?.stringOrNull }
    val ramFee: Double? by lazy { data?.get("ram_fee")?.jsonPrimitive?.doubleOrNull }
    val fee: String? by lazy { data?.get("fee")?.stringOrNull }
    val proposer: String? by lazy { data?.get("proposer")?.stringOrNull }
    val proposalName: String? by lazy { data?.get("proposal_name")?.stringOrNull }
    val requested: JsonArray? by lazy { data?.get("requested").jsonArrayOrNull }
    val level: JsonObject? by lazy { data?.get("level").jsonObjectOrNull }
    val canceler: String? by lazy { data?.get("canceler")?.stringOrNull }
    val executer: String? by lazy { data?.get("executer")?.stringOrNull }
    private val trx: JsonObject? by lazy { data?.get("trx")?.jsonObjectOrNull }
    val trxExpiration: String? by lazy { trx?.get("expiration")?.stringOrNull }
    val trxActions: JsonArray? by lazy { trx?.get("actions")?.jsonArrayOrNull }
    val trxActionNames: List<String>? by lazy { trxActions?.mapNotNull { it.jsonObjectOrNull?.get("name")?.stringOrNull } }

    fun toDataQuantity(): Double {
        return quantity?.split(" ")?.getOrNull(0)?.toDouble() ?: 0.0
    }

    // Function to handle owner field - it could be a string or an object
    fun getDataOwnerAsString(): String? {
        return when (owner) {
            is JsonPrimitive -> if ((owner as JsonPrimitive).doubleOrNull == null) {
                (owner as JsonPrimitive).content // Return the owner as a string
            } else {
                null
            }

            is JsonObject -> owner.toString() // If owner is an object, return its string representation
            else -> null
        }
    }

    fun getDataAmountAsDouble(): Double {
        return try {
            amount?.jsonPrimitive?.doubleOrNull ?: run {
                val stringValue = amount?.jsonPrimitive?.contentOrNull
                stringValue?.toDoubleOrNull() ?: 0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }

    fun getDataAmountAsString(): String? = amount?.stringOrNull

    fun getQuantAsString(): String? {
        return quant?.stringOrNull
    }
}

@Serializable
data class EosAuthorization(
    @SerialName("actor") val actor: String? = null,
    @SerialName("permission") val permission: String? = null,
)

@Serializable
data class Receipt(
    @SerialName("receiver") val receiver: String? = null,
    @SerialName("global_sequence") val globalSequence: Long? = null,
    @SerialName("recv_sequence") val recvSequence: Long? = null,
    @SerialName("auth_sequence") val authSequence: List<AuthSequence>? = null,
)

@Serializable
data class AuthSequence(
    @SerialName("account") val account: String? = null,
    @SerialName("sequence") val sequence: Long? = null,
)