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
package com.memtrip.eos.chain.actions.transaction.account.actions.ramtransfer

import com.memtrip.eos.abi.writer.Abi
import com.memtrip.eos.abi.writer.AccountNameCompress
import com.memtrip.eos.abi.writer.LongCompress
import com.memtrip.eos.abi.writer.StringCompress

@Abi
data class RamTransferArgs(
    val from: String,
    val memo: String = "",
    val to: String,
    val bytes: Long,
) {

    val getFrom: String
        @AccountNameCompress get() = from

    val getMemo: String
        @StringCompress get() = memo

    val getTo: String
        @AccountNameCompress get() = to

    val getBytes: Long
        @LongCompress get() = bytes
}