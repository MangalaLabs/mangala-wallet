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

package com.memtrip.eos.chain.actions.transaction.decoder

import com.memtrip.eos.abi.reader.bytereader.DefaultByteReader
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter
import com.memtrip.eos.chain.actions.transaction.transfer.actions.TransferArgs
import junit.framework.TestCase.assertEquals
import org.junit.Test

class AbiBinaryTransactionReaderTest {

    @Test
    fun testReadTransferArgs() {
        val hex =
            "408608e31b049353000000403210955e610000000000000004454f5300000000126b6c646a6b696f7565726f7775656f697277"

        val result = AbiBinaryTransactionReader(hex).readTransferArgs()

        assertEquals("eidkcaz31234", result.from)
        assertEquals("fuel.gm", result.to)
        assertEquals("0.0097 EOS", result.quantity)
        assertEquals("kldjkiouerowueoirw", result.memo)
    }

    @Test
    fun testReadTransaction() {
        val hex =
            "c397b3665798bdfad39d00000000010000000000ea3055000000004873bd3e011098c32e472d113200000000a8ed3232201098c32e472d11321098c32e472d1132a08601000000000004454f530000000000"

        val result = AbiBinaryTransactionReader(hex).readTransactionAbi()

        val actionData = result.actions[0].data

        val actionDecode = AbiBinaryTransactionReader(actionData!!).readBuyRamAbi()

//        println(result)

        println(actionDecode)
    }
}