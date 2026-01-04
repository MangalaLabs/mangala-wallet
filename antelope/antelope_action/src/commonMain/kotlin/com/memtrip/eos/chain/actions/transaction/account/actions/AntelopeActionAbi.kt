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

import com.memtrip.eos.chain.actions.transaction.abi.AbiPrimitiveDataType

data class AntelopeActionAbi(
    val actionName: String,
    val accountName: String,
    val fieldName: String = "",
    val fieldType: String = "",
    var value: String = "",
    var valueArr: List<AbiPrimitiveDataType> = emptyList(),
    var mapValue: Map<String, List<AbiPrimitiveDataType>> = emptyMap(),
    var level: Int = 0,
    var subFields: List<AntelopeActionAbi> = emptyList(),
    var arraySize: Int = 0,
    val subfieldCount: Int,
    val isOptionalValueSet: Boolean = false,
    val variantTypeIndex: Int? = null
) {
    val isArray = fieldType.contains("[]")
    val isOptional = fieldType.contains("?")
    val isExtension = fieldType.contains("$")

    val baseType: String
        get() {
            var result = fieldType

            if (isArray) {
                result = fieldType.replace("[]", "")
            }
            if (isOptional) {
                result = fieldType.replace("?", "")
            }
            if (isExtension) {
                result = fieldType.replace("$", "")
            }

            return result
        }
    val stringValue = if (isArray) "" else value
    val isPrimitive = AntelopePrimitiveDataTypes.entries.any { it.value == fieldType }
    val isArrayPrimitive = AntelopePrimitiveDataTypes.entries.any { it.value == baseType } && isArray

    val elementsCount = if (isPrimitive) {
        if (isArray) arraySize else 1
    } else {
        if (isArray) arraySize else if (subfieldCount != 0) arraySize / subfieldCount else 0
    }
}
