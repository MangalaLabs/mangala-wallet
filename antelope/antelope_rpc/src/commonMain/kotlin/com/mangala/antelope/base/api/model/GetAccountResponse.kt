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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAccountResponse(
    @SerialName("account_name") val accountName: String?,
    @SerialName("head_block_num") val headBlockNum: Long?,
    @SerialName("head_block_time") val headBlockTime: String?,
    @SerialName("privileged") val privileged: Boolean?,
    @SerialName("last_code_update") val lastCodeUpdate: String?,
    @SerialName("created") val created: String?,
    @SerialName("core_liquid_balance") val coreLiquidBalance: String? = null,
    @SerialName("ram_quota") val ramQuota: Long?,
    @SerialName("net_weight") val netWeight: Long?,
    @SerialName("cpu_weight") val cpuWeight: Long?,
    @SerialName("net_limit") val netLimit: ResourceLimit?,
    @SerialName("cpu_limit") val cpuLimit: ResourceLimit?,
    @SerialName("ram_usage") val ramUsage: Long?,
    @SerialName("permissions") val permissions: List<Permission>?,
    @SerialName("total_resources") val totalResources: TotalResources?,
    @SerialName("self_delegated_bandwidth") val selfDelegatedBandwidth: SelfDelegatedBandwidth?,
    @SerialName("refund_request") val refundRequest: RefundRequest?,
    @SerialName("voter_info") val voterInfo: VoterInfo?,
    @SerialName("rex_info") val rexInfo: RexInfo?,
    @SerialName("subjective_cpu_bill_limit") val subjectiveCpuBillLimit: ResourceLimit?,
    @SerialName("eosio_any_linked_actions") val eosioAnyLinkedActions: List<String?>?
)

@Serializable
data class ResourceLimit(
    @SerialName("used") val used: Long?,
    @SerialName("available") val available: Long?,
    @SerialName("max") val max: Long?,
    @SerialName("last_usage_update_time") val lastUsageUpdateTime: String?,
    @SerialName("current_used") val currentUsed: Long?
)

@Serializable
data class Permission(
    @SerialName("perm_name") val permName: String?,
    @SerialName("parent") val parent: String?,
    @SerialName("required_auth") val requiredAuth: RequiredAuth?,
    @SerialName("linked_actions") val linkedActions: List<LinkedAction>?
)

@Serializable
data class RequiredAuth(
    @SerialName("threshold") val threshold: Int?,
    @SerialName("keys") val keys: List<Key>?,
    @SerialName("accounts") val accounts: List<Account>?,
    @SerialName("waits") val waits: List<Wait>?
)

@Serializable
data class Key(
    @SerialName("key") val key: String?,
    @SerialName("weight") val weight: Int?
)

@Serializable
data class TotalResources(
    @SerialName("owner") val owner: String?,
    @SerialName("net_weight") val netWeight: String?,
    @SerialName("cpu_weight") val cpuWeight: String?,
    @SerialName("ram_bytes") val ramBytes: Long?
)

@Serializable
data class SelfDelegatedBandwidth(
    @SerialName("from") val from: String?,
    @SerialName("to") val to: String?,
    @SerialName("net_weight") val netWeight: String?,
    @SerialName("cpu_weight") val cpuWeight: String?
)

@Serializable
data class RefundRequest(
    @SerialName("owner") val owner: String?,
    @SerialName("request_time") val requestTime: String?,
    @SerialName("net_amount") val netAmount: String?,
    @SerialName("cpu_amount") val cpuAmount: String?
)

@Serializable
data class VoterInfo(
    val owner: String?,
    val proxy: String?,
    val producers: List<String?>?,
    val staked: Long?,
    @SerialName("last_vote_weight") val lastVoteWeight: String?,
    @SerialName("proxied_vote_weight") val proxiedVoteWeight: String?,
    @SerialName("is_proxy") val isProxy: Int?,
    val flags1: Int?,
    val reserved2: Int?,
    @SerialName("reserved3") val reservedThree: String?
)

@Serializable
data class RexInfo(
    val version: Int?,
    val owner: String?,
    @SerialName("vote_stake") val voteStake: String?,
    @SerialName("rex_balance") val rexBalance: String?,
    @SerialName("matured_rex") val maturedRex: Long?,
    @SerialName("rex_maturities") val rexMaturities: List<RexMaturity?>
)

@Serializable
data class RexMaturity(
    val first: String?,
    val second: Long?
)

@Serializable
data class LinkedAction(
    val account: String? = null,
    val action: String? = null
)

@Serializable
data class Wait(
    @SerialName("wait_sec") val waitSec: Long?,
    val weight: Long?
)

@Serializable
data class Account(
    val weight: Long?,
    val permission: PermissionAccount?
)

@Serializable
data class PermissionAccount(
    val actor: String?,
    val permission: String?
)