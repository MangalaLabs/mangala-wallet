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

package com.mangala.antelope.base.model

import kotlinx.serialization.Serializable

// Assuming NameType is a type alias or a class.
// You would need to define it appropriately in Kotlin.

interface TypeDef {
    val newTypeName: String
    val type: String
}

interface Field {
    val name: String
    val type: String
}

interface Struct {
    val name: String
    val base: String
    val fields: List<Field>
}

interface Action {
    val name: String
    val type: String
    val ricardianContract: String
}

interface Table {
    val name: String
    val indexType: String
    val keyNames: List<String>
    val keyTypes: List<String>
    val type: String
}

interface Clause {
    val id: String
    val body: String
}

interface Variant {
    val name: String
    val types: List<String>
}

interface Def {
    val version: String
    val types: List<TypeDef>
    val variants: List<Variant>
    val structs: List<Struct>
    val actions: List<Action>
    val tables: List<Table>
    val ricardianClauses: List<Clause>
    val actionResults: List<ActionResult>
}

interface ActionResult {
    val name: String
    val resultType: String
}
