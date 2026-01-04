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
package com.memtrip.eos.chain.actions.powerup

import com.memtrip.eos.abi.writer.Abi
import com.memtrip.eos.abi.writer.AccountNameCompress
import com.memtrip.eos.abi.writer.AssetCompress
import com.memtrip.eos.abi.writer.IntCompress
import com.memtrip.eos.abi.writer.LongCompress

@Abi
data class PowerUpArgs(
    val payer: String,
    val receiver: String,
    val days: Int,
    val net_frac: Long,
    val cpu_frac: Long,
    val max_payment: String,
) {

    val getCreator: String
        @AccountNameCompress get() = payer

    val getName: String
        @AccountNameCompress get() = receiver

    val getDays: Int
        @IntCompress get() = days

    val getNetFrac: Long
        @LongCompress get() = net_frac

    val getCpuFrac: Long
        @LongCompress get() = cpu_frac

    val getMaxPayment: String
        @AssetCompress get() = max_payment
}