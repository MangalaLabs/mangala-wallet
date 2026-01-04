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
package com.memtrip.eos.chain.actions.transaction.account.actions

// https://docs.eosnetwork.com/docs/latest/smart-contracts/variables/
enum class AntelopePrimitiveDataTypes(val value: String, val isNumericKeyboardInputType: Boolean) {
    ASSET("asset", false),
    BLOCK_TIMESTAMP_TYPE("block_timestamp_type", false),
    BOOL("bool", false),
    BYTES("bytes", false),
    CHECKSUM160("checksum160", false),
    CHECKSUM256("checksum256", false),
    CHECKSUM512("checksum512", false),
    EXTENDED_ASSET("extended_asset", false), // https://developers.eos.io/manuals/eosio.cdt/v1.4/structeosio_1_1extended__asset
    EXTENDED_SYMBOL("extended_symbol", false), // https://developers.eos.io/manuals/eosio.cdt/v1.4/classeosio_1_1extended__symbol
    FLOAT128("float128", true),
    FLOAT32("float32", true),
    FLOAT64("float64", true),
    INT128("int128", true),
    INT16("int16", true),
    INT32("int32", true),
    VARINT32("varint32", true),
    INT64("int64", true),
    INT8("int8", true),
    UINT128("uint128", true),
    UINT16("uint16", true),
    UINT32("uint32", true),
    VARUINT32("varuint32", true),
    UINT64("uint64", true),
    UINT8("uint8", true),
    NAME("name", false),
    PUBLIC_KEY("public_key", false),
    SIGNATURE("signature", false),
    STRING("string", false),
    SYMBOL("symbol", false),
    SYMBOL_CODE("symbol_code", false),
    TIME_POINT("time_point", false),
    TIME_POINT_SEC("time_point_sec", false);

    companion object {
        const val EXTENDED_TYPE_DELIMITER = ";" // A custom delimiter used in our app only
        const val SYMBOL_TYPE_DELIMITER = ","
        const val ASSET_TYPE_DELIMITER = " "
        fun fromValue(value: String): AntelopePrimitiveDataTypes? {
            return entries.firstOrNull { it.value == value }
        }
    }
}