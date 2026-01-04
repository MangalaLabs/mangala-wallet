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

package com.mangala.antelope.base.core.enums

/**
 * Enum of supported algorithms which are employed in eosio-java library
 */
enum class AlgorithmEmployed(val str: String) {
    /**
     * Supported SECP256r1 (prime256v1) algorithm curve
     */
    SECP256R1("secp256r1"),

    /**
     * Supported SECP256k1 algorithm curve
     */
    SECP256K1("secp256k1"),

    /**
     * Supported prime256v1 algorithm curve
     */
    PRIME256V1("prime256v1");

    fun getString(): String {
        return str
    }
}