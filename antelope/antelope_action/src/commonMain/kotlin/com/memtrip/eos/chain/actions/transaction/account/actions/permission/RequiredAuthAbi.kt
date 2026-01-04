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
import com.memtrip.eos.abi.writer.ChildCompress
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.AccountKeyAbi

@Abi
data class RequiredAuthAbi(
    val threshold: Int,
    val keys: List<AccountKeyAbi>,
    val accounts: List<AccountRequiredAuthAccountAbi>,
    val waits: List<AccountRequiredAuthWaitAbi>
) {
    val getThreshold: Int
        @AccountNameCompress get() = threshold

    val getKeys: List<AccountKeyAbi>
        @ChildCompress get() = keys

    val getAccounts: List<AccountRequiredAuthAccountAbi>
        @ChildCompress get() = accounts

    val getWaits: List<AccountRequiredAuthWaitAbi>
        @ChildCompress get() = waits
}