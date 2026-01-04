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
package com.memtrip.eos.chain.actions.transaction.account.actions.multisigs.createpropose

import com.memtrip.eos.abi.writer.Abi
import com.memtrip.eos.abi.writer.AccountNameCompress
import com.memtrip.eos.abi.writer.ChildCompress
import com.memtrip.eos.abi.writer.CollectionCompress
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi

@Abi
class CreateProposeAbi(
    val proposer: String,
    val proposalName: String,
    val requested: List<TransactionAuthorizationAbi>,
    val trx: TransactionAbi
) {
    val getProposer: String
        @AccountNameCompress get() = proposer

    val getProposalName: String
        @AccountNameCompress get() = proposalName

    val getRequested: List<TransactionAuthorizationAbi>
        @CollectionCompress get() = requested

    val getTrx: TransactionAbi
        @ChildCompress get() = trx

    override fun toString(): String {
        return "CreateProposeAbi(proposer='$proposer', proposalName='$proposalName', requested=$requested, trx=$trx)"
    }
}