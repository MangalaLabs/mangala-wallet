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

package com.memtrip.eos.chain.actions.transaction.gen

import com.memtrip.eos.abi.writer.ByteWriter
import com.memtrip.eos.abi.writer.Squishable
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi

class TransactionAbiSquishable internal constructor(private val abiBinaryGen: AbiBinaryGenTransactionWriter) :
    Squishable<TransactionAbi> {
    override fun squish(transactionabi: TransactionAbi, byteWriter: ByteWriter) {
        byteWriter.putTimestampMs(transactionabi.getExpiration)
        byteWriter.putBlockNum(transactionabi.getRefBlockNum)
        byteWriter.putBlockPrefix(transactionabi.getRefBlockPrefix)
        byteWriter.putVariableUInt(transactionabi.getMaxNetUsageWords)
        byteWriter.putVariableUInt(transactionabi.getMaxCpuUsageMs)
        byteWriter.putVariableUInt(transactionabi.getDelaySec)
        abiBinaryGen.squishCollectionActionAbi(transactionabi.getContextFreeActions, byteWriter)
        abiBinaryGen.squishCollectionActionAbi(transactionabi.getActions, byteWriter)
        byteWriter.putStringCollection(transactionabi.getTransactionExtensions)
    }
}
