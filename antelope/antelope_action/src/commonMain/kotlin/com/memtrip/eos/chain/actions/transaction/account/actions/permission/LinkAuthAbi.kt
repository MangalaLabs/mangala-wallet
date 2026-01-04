/*
 * Copyright 2013-present memtrip LTD.
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

// ------------------------------------------------------------------
// MODIFICATION NOTICE:
// Modified by Mangala Wallet
// Description: Adapted for Kotlin Multiplatform compatibility.
// ------------------------------------------------------------------

package com.memtrip.eos.chain.actions.transaction.account.actions.permission

import com.memtrip.eos.abi.writer.Abi
import com.memtrip.eos.abi.writer.AccountNameCompress

@Abi
class LinkAuthAbi(
    val account: String,
    val code: String,
    val type: String,
    val requirement: String
) {
    val getAccount: String
        @AccountNameCompress get() = account

    val getCode: String
        @AccountNameCompress get() = code

    val getType: String
        @AccountNameCompress get() = type

    val getRequirement: String
        @AccountNameCompress get() = requirement

    override fun toString(): String {
        return "AuthLinkAbi(account='$account', code='$code', type='$type', requirement='$requirement')"
    }
}