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

package com.memtrip.eos.core.hash

import com.mangala.wallet.utils.hexStringToByteArray
import junit.framework.TestCase.assertEquals
import org.junit.Test

@OptIn(ExperimentalStdlibApi::class)
class HMacTest {

    @Test
    fun test1() {
        val key = "41210c3ac519349ab6531b6d82611e7c5e13c7220e772470632cdafbbe7bf8b7"
        val message = "0101010101010101010101010101010101010101010101010101010101010101001c12d8f0118f5f1a093565ae6eeecfae363aae77d9a62ed9d47f8d00a124fe38f5e9593d47d7a585cee01d5f0789259bf8eb3a96b11f2b41d733f7f95570b43d"
        val expected = "293daaa06a445bd66283e0546d75eace717cb41820049c05c846e3cd696c11da"

        val actual = HMac.hash(key.hexStringToByteArray(), message.hexStringToByteArray())

        assertEquals(expected, actual.toHexString())
    }

    @Test
    fun test2() {
        val key = "34313231306333616335313933343961623635333162366438323631316537633565313363373232306537373234373036333263646166626265376266386237"
        val message = "3031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130313031303130303163313264386630313138663566316130393335363561653665656563666165333633616165373764396136326564396434376638643030613132346665333866356539353933643437643761353835636565303164356630373839323539626638656233613936623131663262343164373333663766393535373062343364"
        val expected = "c52eebcf3e1facc63a4a7b4181c873b245cb5e5078b64af8901c23904a06077a"

        val actual = HMac.hash(key.hexStringToByteArray(), message.hexStringToByteArray())

        assertEquals(expected, actual.toHexString())
    }

    @Test
    fun test3() {
        val key = "0000000000000000000000000000000000000000000000000000000000000000"
        val message = "0101010101010101010101010101010101010101010101010101010101010101001c12d8f0118f5f1a093565ae6eeecfae363aae77d9a62ed9d47f8d00a124fe388280b8121a01ff1f2a4da3558c9cb1f620f9aeacfe7ffb2b63f2f1a5c026d7bc"
        val expected = "8afd9586822b8882729c6bb69627648a01ee6a48b18e56db689cc17232b5714b"

        val actual = HMac.hash(key.hexStringToByteArray(), message.hexStringToByteArray())

        assertEquals(expected, actual.toHexString())
    }

    @Test
    fun test4() {
        val key = "1bbe23defb3c90117bde82c3e2c3a23a1d2de802df0401d837925ebb59de98d8"
        val message = "0101010101010101010101010101010101010101010101010101010101010101"
        val expected = "e9d0c532b387255bc1cfb42c0fc2dff1db49b7aacdeb96bdca1cfd821c378825"

        val actual = HMac.hash(key.hexStringToByteArray(), message.hexStringToByteArray())

        assertEquals(expected, actual.toHexString())
    }

    @Test
    fun test5() {
        val key = "f800b20bea8bc5c60a61ca27dbf98e029f9e754c091ae975358c67df46b6bf5e"
        val message = "0101010101010101010101010101010101010101010101010101010101010101"
        val expected = "f795f3534021d16e1ca25bf0782cca2b8035656adb4929c4be4d4fafb29d5792"

        val actual = HMac.hash(key.hexStringToByteArray(), message.hexStringToByteArray())

        assertEquals(expected, actual.toHexString())
    }

    @Test
    fun test6() {
        val key = "f800b20bea8bc5c60a61ca27dbf98e029f9e754c091ae975358c67df46b6bf5e"
        val message = "f795f3534021d16e1ca25bf0782cca2b8035656adb4929c4be4d4fafb29d5792011c12d8f0118f5f1a093565ae6eeecfae363aae77d9a62ed9d47f8d00a124fe38148704d24d0713ebf245cf56ba219981d129305448842535093e9690ae208074"
        val expected = "4a122b81f72d9ebe09cc5f61f48ec41ddb649206e0b0be9233726bbe454bf050"

        val actual = HMac.hash(key.hexStringToByteArray(), message.hexStringToByteArray())

        assertEquals(expected, actual.toHexString())
    }

    @Test
    fun test7() {
        val key = "4a122b81f72d9ebe09cc5f61f48ec41ddb649206e0b0be9233726bbe454bf050"
        val message = "f795f3534021d16e1ca25bf0782cca2b8035656adb4929c4be4d4fafb29d5792"
        val expected = "7df7bbd8a0ac2b8b6510f454b1d08b3005ab06686b14d53d768dd61b2c8319d2"

        val actual = HMac.hash(key.hexStringToByteArray(), message.hexStringToByteArray())

        assertEquals(expected, actual.toHexString())
    }

    @Test
    fun test8() {
        val key = "4a122b81f72d9ebe09cc5f61f48ec41ddb649206e0b0be9233726bbe454bf050"
        val message = "7df7bbd8a0ac2b8b6510f454b1d08b3005ab06686b14d53d768dd61b2c8319d2"
        val expected = "eda419a81e008624464950d12a29a709bed6e625273750d916ad74d12dbe82e6"

        val actual = HMac.hash(key.hexStringToByteArray(), message.hexStringToByteArray())

        assertEquals(expected, actual.toHexString())
    }
}