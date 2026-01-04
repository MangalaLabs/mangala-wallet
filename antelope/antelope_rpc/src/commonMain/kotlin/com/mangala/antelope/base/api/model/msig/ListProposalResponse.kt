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

package com.mangala.antelope.base.api.model.msig

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListProposalResponse(
    @SerialName("proposals")
    val proposals: List<Proposal>?
) {
    @Serializable
    data class Proposal(
        @SerialName("block_num")
        val blockNum: Int?,
        @SerialName("executed")
        val executed: Boolean?,
        @SerialName("primary_key")
        val primaryKey: String?,
        @SerialName("proposal_name")
        val proposalName: String?,
        @SerialName("proposer")
        val proposer: String?,
        @SerialName("provided_approvals")
        val providedApprovals: List<Approval>?,
        @SerialName("requested_approvals")
        val requestedApprovals: List<Approval>?
    ) {
        @Serializable
        data class Approval(
            @SerialName("actor")
            val actor: String?,
            @SerialName("permission")
            val permission: String?,
            @SerialName("time")
            val time: String?
        )
    }
}