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

import com.memtrip.eos.abi.writer.bytewriter.DefaultByteWriter
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopeActionAbi
import io.mockk.InternalPlatformDsl.toStr
import org.junit.Assert.assertEquals
import org.junit.Test

class AntelopeActionAbiSquishableTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a bool value true in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "bool", value = "true"))

        val result = serialize(data)

        assertEquals("01", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a bool value false in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "bool", value = "false"))

        val result = serialize(data)

        assertEquals("00", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a string value false in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "string", value = "helloworld"))

        val result = serialize(data)

        assertEquals("0A68656C6C6F776F726C64", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a int8 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "int8", value = "127"))

        val result = serialize(data)

        assertEquals("7F", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a int8 item with min value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "int8", value = "-128"))

        val result = serialize(data)

        assertEquals("80", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a int16 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "int16", value = "32767"))

        val result = serialize(data)

        assertEquals("FF7F", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a int16 item with min value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "int16", value = "-32768"))

        val result = serialize(data)

        assertEquals("0080", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a int32 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "int32", value = "2147483647"))

        val result = serialize(data)

        assertEquals("FFFFFF7F", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a int32 item with min value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "int32", value = "-2147483648"))

        val result = serialize(data)

        assertEquals("00000080", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a int64 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "int64", value = "9223372036854775807"))

        val result = serialize(data)

        assertEquals("FFFFFFFFFFFFFF7F", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a int64 item with min value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "int64", value = "-9223372036854775808"))

        val result = serialize(data)

        assertEquals("0000000000000080", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a int128 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "int128", value = "170141183460469231731687303715884105727"))

        val result = serialize(data)

        assertEquals("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7F", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a int128 item with min value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "int128", value = "-170141183460469231731687303715884105728"))

        val result = serialize(data)

        assertEquals("00000000000000000000000000000080", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a varint32 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "varint32", value = "2147483647"))

        val result = serialize(data)

        assertEquals("FEFFFFFF0F", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a varint32 item with min value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "varint32", value = "-2147483648"))

        val result = serialize(data)

        assertEquals("FFFFFFFF0F", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a varint32 item with 0 value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "varint32", value = "0"))

        val result = serialize(data)

        assertEquals("00", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a varint32 item with 256 value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "varint32", value = "256"))

        val result = serialize(data)

        assertEquals("8004", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given an uint8 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "uint8", value = "255"))

        val result = serialize(data)

        assertEquals("FF", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given an uint16 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "uint16", value = "65535"))

        val result = serialize(data)

        assertEquals("FFFF", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given an uint32 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "uint32", value = "4294967295"))

        val result = serialize(data)

        assertEquals("FFFFFFFF", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given an uint64 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "uint64", value = "18446744073709551615"))

        val result = serialize(data)

        assertEquals("FFFFFFFFFFFFFFFF", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given an varuint32 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "varuint32", value = "4294967295"))

        val result = serialize(data)

        assertEquals("FFFFFFFF0F", result.toHexString().uppercase())
    }


    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given an varuint32 item with min value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "varuint32", value = "0"))

        val result = serialize(data)

        assertEquals("00", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given an float32 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "float32", value = Float.MIN_VALUE.toStr()))

        val result = serialize(data)

        assertEquals("01000000", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a float32 item with min value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "float32", value = Float.MAX_VALUE.toStr()))

        val result = serialize(data)

        assertEquals("FFFF7F7F", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given an float64 item with max value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "float64", value = Double.MIN_VALUE.toStr()))

        val result = serialize(data)

        assertEquals("0100000000000000", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a float64 item with min value in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "float64", value = Double.MAX_VALUE.toStr()))

        val result = serialize(data)

        assertEquals("FFFFFFFFFFFFEF7F", result.toHexString().uppercase())
    }

//    @OptIn(ExperimentalStdlibApi::class)
//    @Test
//    fun `Given a random float128 item in form of AntelopeActionAbi, when squishing the item, then the item is squished`() {
//        val data = listOf(createActionAbi(fieldType = "float128", value = "4B2B2B068F13D20C6345588607DCCAEF"))
//
//        val result = serialize(data)
//
//        assertEquals("4B2B2B068F13D20C6345588607DCCAEF", result.toHexString().uppercase())
//    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a name item AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "name", value = "accmultisig1"))

        val result = serialize(data)

        assertEquals("1098C32E472D1132", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a uint16 item AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val byteWriter = DefaultByteWriter()
        val data = listOf(createActionAbi(fieldType = "uint16", value = "1"))
        val sut = AntelopeActionAbiSquishable()

        sut.squish(data, byteWriter)
        val result = byteWriter.toBytes()

        assertEquals("0100", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a uint32 item AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "uint32", value = "1"))

        val result = serialize(data)

        assertEquals("01000000", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a checksum160 item AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "checksum160", value = "8a73c5438c28e79e696144fa869886f240cfaddb"))

        val result = serialize(data)

        assertEquals("8A73C5438C28E79E696144FA869886F240CFADDB", result.toHexString().uppercase())
    }


    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a checksum256 item AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "checksum256", value = "936a185caaa266bb9cbe981e9e05cb78cd732b0b3280eb944412bb6f8f8f07af"))

        val result = serialize(data)

        assertEquals("936A185CAAA266BB9CBE981E9E05CB78CD732B0B3280EB944412BB6F8F8F07AF", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a checksum512 item AntelopeActionAbi, when squishing the item, then the item is squished`() {
        val data = listOf(createActionAbi(fieldType = "checksum512", value = "1594244d52f2d8c12b142bb61f47bc2eaf503d6d9ca8480cae9fcf112f66e4967dc5e8fa98285e36db8af1b8ffa8b84cb15e0fbcf836c3deb803c13f37659a60"))

        val result = serialize(data)

        assertEquals("1594244D52F2D8C12B142BB61F47BC2EAF503D6D9CA8480CAE9FCF112F66E4967DC5E8FA98285E36DB8AF1B8FFA8B84CB15E0FBCF836C3DEB803C13F37659A60", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given an empty array of objects in form of item AntelopeActionAbi, when squishing the item, then the array size of 0 is squished`() {
        val data = listOf(
            createActionAbi(fieldType = "key_weight[]", value = "1", arraySize = 0),
        )

        val result = serialize(data)

        assertEquals("00", result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given an array of objects in form of item AntelopeActionAbi, when squishing the item, then the array size is squished along with its items`() {
        val data = listOf(
            createActionAbi(fieldType = "key_weight[]", value = "1", arraySize = 1),
            createActionAbi(fieldType = "public_key", value = "EOS6f2RYsQFgpcsjfe7gYrn7rcHvCUYu6jhpKnETigernBBcs3iN3"),
            createActionAbi(fieldType = "uint16", value = "1"),
        )

        val result = serialize(data)

        val keyWeightArraySizeSerialized = "01"
        val publicKeySerialized = "0002E8D1E14F90B4698773DC7A34CD4CC25AA229D0E8F6870222786FDEF5A7B3834C"
        val keyWeightSerialized = "0100"
        val expectedResult = keyWeightArraySizeSerialized + publicKeySerialized + keyWeightSerialized
        assertEquals(expectedResult, result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a byte array encoded in hex in form of item AntelopeActionAbi, when squishing the item, then the array size is squished along with its items`() {
        val data = listOf(createActionAbi(fieldType = "bytes", value = "aabbccddeeff00112233445566778899"))

        val result = serialize(data)

        val size = "10"
        val elements = "AABBCCDDEEFF00112233445566778899"
        val expectedResult = size + elements
        assertEquals(expectedResult, result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a currency symbol in form of item AntelopeActionAbi, when squishing the item, then the array size is squished along with its items`() {
        val data = listOf(createActionAbi(fieldType = "symbol_code", value = "JUNGLE"))

        val result = serialize(data)

        val expectedResult = "4A554E474C450000"
        assertEquals(expectedResult, result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a currency symbol single character in form of item AntelopeActionAbi, when squishing the item, then the array size is squished along with its items`() {
        val data = listOf(createActionAbi(fieldType = "symbol_code", value = "A"))

        val result = serialize(data)

        val expectedResult = "4100000000000000"
        assertEquals(expectedResult, result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a symbol in form of item AntelopeActionAbi, when squishing the item, then the array size is squished along with its items`() {
        val data = listOf(createActionAbi(fieldType = "symbol", value = "4,JUNGLE"))

        val result = serialize(data)

        val expectedResult = "044A554E474C4500"
        assertEquals(expectedResult, result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a zero symbol in form of item AntelopeActionAbi, when squishing the item, then the array size is squished along with its items`() {
        val data = listOf(createActionAbi(fieldType = "symbol", value = "0,"))

        val result = serialize(data)

        val expectedResult = "0000000000000000"
        assertEquals(expectedResult, result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a valid K1 signature in form of item AntelopeActionAbi, when squishing the item, signature is squished successfully`() {
        val data = listOf(createActionAbi(fieldType = "signature", value = "SIG_K1_KmE7FEeodKeM2hV1rAAt9wwzWmMSqHuPRoLPob6TDKJ1BLsMw4oL2jq7rcTVNUZNRLywcLdmqCDipz24oLudYNr1zDGFpy"))

        val result = serialize(data)

        val expectedResult = "00207DF5C297A7F73E9B1E840B182C356A6E9566270DDA86AD0AB45A54679F4B0CBF672A9B19CD3E8798AB04AAC17AFEB80E0B928CE431A264555AA95F17FB313594"
        assertEquals(expectedResult, result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given a valid float128 value in form of item AntelopeActionAbi, when squishing value, then value is squished correctly`() {
        val data = listOf(createActionAbi(fieldType = "float128", value = "c9b2d4e0cbc619eae8f30a5aea333058"))

        val result = serialize(data)

        val expectedResult = "C9B2D4E0CBC619EAE8F30A5AEA333058"
        assertEquals(expectedResult, result.toHexString().uppercase())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Given an actionData with variants in form of AntelopeActionAbi list, when squishing value, then value is squished correctly`() {
        val data = listOf(
            createActionAbi(fieldType = "name", value = "harkonnenmgl"),
            createActionAbi(fieldType = "VariantType", value = "1", variantTypeIndex = 1),
            createActionAbi(fieldType = "string", value = "helloworld"),
        )

        val result = serialize(data)

        val expectedResult = "10999C6A4E0AAF69010A68656C6C6F776F726C64"
        assertEquals(expectedResult, result.toHexString().uppercase())
    }

    private fun serialize(data: List<AntelopeActionAbi>): ByteArray {
        val byteWriter = DefaultByteWriter()
        val sut = AntelopeActionAbiSquishable()
        sut.squish(data, byteWriter)
        return byteWriter.toBytes()
    }

    private fun createActionAbi(
        fieldType: String,
        value: String,
        arraySize: Int = 0,
        variantTypeIndex: Int? = null
    ): AntelopeActionAbi {
        return AntelopeActionAbi(
            actionName = "actionName",
            accountName = "accountName",
            fieldName = "fieldName",
            fieldType = fieldType,
            value = value,
            valueArr = emptyList(),
            mapValue = emptyMap(),
            level = 0,
            subFields = emptyList(),
            arraySize = arraySize,
            subfieldCount = 0,
            variantTypeIndex = variantTypeIndex
        )
    }
}