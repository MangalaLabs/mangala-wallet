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

package com.mangala.antelope.base.domain.model.rpcerror

import com.mangala.antelope.base.api.model.ChainError

sealed class NotEnoughResourceException(open val chainError: ChainError) : Exception() {
    data class NotEnoughCpuException(
        val billedCpuMicroseconds: Long?,
        val maximumBillableCpuMicroseconds: Long?,
        val accountCpuLimit: Long?,
        override val chainError: ChainError
    ) : NotEnoughResourceException(chainError)

    data class NotEnoughRamException(
        val neededBytes: Long,
        val hasBytes: Long,
        override val chainError: ChainError
    ) : NotEnoughResourceException(chainError) {
        val extraBytesNeeded = neededBytes - hasBytes
    }

    data class NotEnoughNetException(
        val microsecondsNeeded: Long,
        val microsecondsAvailable: Long,
        override val chainError: ChainError
    ) : NotEnoughResourceException(chainError) {
        val extraMicrosecondsNeeded = microsecondsNeeded - microsecondsAvailable
    }
}