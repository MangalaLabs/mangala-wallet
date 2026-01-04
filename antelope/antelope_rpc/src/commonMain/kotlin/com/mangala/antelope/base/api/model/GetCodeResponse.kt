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

package com.mangala.antelope.base.api.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
@Serializable
data class GetCodeResponse(
    val name: String,
    @SerialName("code_hash") val codeHash: String,
    val wast: String,
    val wasm: String,
    val abi: Abi
) {

    @Serializable
    data class Abi(
        val version: String,
        val types: List<Type>,
        val structs: List<Struct>,
        val actions: List<Action>,
        val tables: List<Table>,
        @SerialName("abi_extensions") val abiExtensions: List<List<Int>>,
        @SerialName("error_messages") val errorMessages: List<String>,
        @SerialName("ricardian_clauses") val ricardianClauses:
        List<String>,
        val variants: List<String>
    )

    @Serializable
    data class Type(
        @SerialName("new_type_name") val newTypeName: String,
        val type: String
    )

    @Serializable
    data class Struct(
        val name: String,
        val base: String,
        val fields: List<Field>
    )

    @Serializable
    data class Field(
        val name: String,
        val type: String
    )

    @Serializable
    data class Action(
        val name: String,
        val type: String,
        @SerialName("ricardian_contract") val ricardianContract: String
    )

    @Serializable
    data class Table(
        val name: String,
        @SerialName("index_type") val indexType: String,
        @SerialName("key_names") val keyNames: List<String>,
        @SerialName("key_types") val keyTypes: List<String>,
        val type: String
    )
}