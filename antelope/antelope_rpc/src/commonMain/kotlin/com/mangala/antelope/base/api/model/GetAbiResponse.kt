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



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GetAbiResponse(
    @SerialName("account_name")
    val accountName: String? = "",
    @SerialName("abi")
    val abi: Abi? = null
)

@Serializable
data class Abi(
    val version: String? = "",
    val types: List<TypeDef>? = emptyList(),
    val variants: List<Variant>? = emptyList(),
    val structs: List<Struct>? = emptyList(),
    val actions: List<ActionAbi>? = emptyList(),
    val tables: List<Table>? = emptyList(),
    @SerialName("ricardian_clauses")
    val ricardianClauses: List<Clause>? = emptyList(),
    @SerialName("action_results")
    val actionResults: List<ActionResult>? = emptyList()
)

@Serializable
data class TypeDef(
    @SerialName("new_type_name")
    val newTypeName: String? = "",
    val type: String? = ""
)

@Serializable
data class Field(
    val name: String? = "",
    val type: String? = ""
)

@Serializable
data class Struct(
    val name: String? = "",
    val base: String? = "",
    val fields: List<Field>? = emptyList()
)

@Serializable
data class ActionAbi(
    val name: String? = "",
    val type: String? = "",
    @SerialName("ricardian_contract")
    val ricardianContract: String? = ""
)

@Serializable
data class Table(
    val name: String? = "",
    @SerialName("index_type")
    val indexType: String? = "",
    @SerialName("key_names")
    val keyNames: List<String>? = emptyList(),
    @SerialName("key_types")
    val keyTypes: List<String>? = emptyList(),
    val type: String? = ""
)

@Serializable
data class Clause(
    val id: String? = "",
    val body: String? = ""
)

@Serializable
data class Variant(
    val name: String? = "",
    val types: List<String>? = emptyList()
)

@Serializable
data class ActionResult(
    val name: String? = "",
    @SerialName("result_type")
    val resultType: String? = ""
)






