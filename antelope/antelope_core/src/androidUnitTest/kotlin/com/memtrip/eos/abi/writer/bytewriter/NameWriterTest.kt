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

package com.memtrip.eos.abi.writer.bytewriter

import org.junit.Assert.assertEquals
import org.junit.Test

class NameWriterTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun test() {
        val byteWriter = DefaultByteWriter()
        val sut = NameWriter()

        sut.put("zzzzzzzzzzzzj", byteWriter)
        sut.put("zzzzzzzzzzzzz", byteWriter)
        val result = byteWriter.toBytes().toHexString()

        assertEquals("ffffffffffffffff", result)
    }
}