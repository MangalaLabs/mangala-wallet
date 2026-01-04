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
package com.mangala.wallet.antelope_balance

import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.DecimalFormatRoundingMode
import com.mangala.wallet.utils.EosSymbolFormatter

object BalanceFormatter {

    fun deserialize(balance: String): Balance {
        val parts = balance.split(" ")
        val precision = if (balance.contains('.')) {
            parts[0].substringAfter('.').length
        } else {
            0
        }
        return Balance(parts[0].toDouble(), parts[1], precision = precision)
    }

    fun deserializeOrNull(balance: String): Balance? {
        return try {
            deserialize(balance)
        } catch (e: Exception) {
            null
        }
    }

    fun create(balance: String, symbol: String): Balance {
        return if (balance.isEmpty()) {
            create(0.0, symbol)
        } else {
            create(balance.toDouble(), symbol)
        }
    }

    fun create(balance: Double, symbol: String): Balance {
        return Balance(balance, symbol)
    }

    fun formatEosBalance(balance: Double, symbol: String, ignoreLocale: Boolean): String {
        return formatEosBalance(balance.toString(), symbol, ignoreLocale)
    }

    fun formatEosBalance(balance: String, symbol: String, ignoreLocale: Boolean): String {
        return formatEosBalance(create(balance, symbol), ignoreLocale)
    }

    fun formatEosBalance(balance: Balance, ignoreLocale: Boolean = false): String {
        val value = formatBalanceDigits(
            balance.amount,
            precision = balance.precision,
            ignoreLocale = ignoreLocale
        )
        return "$value ${balance.symbol}"
    }

    fun formatEosBalance(balance: Balance, blockchainType: Any?, ignoreLocale: Boolean = false): String {
        val value = formatBalanceDigits(
            balance.amount,
            precision = balance.precision,
            ignoreLocale = ignoreLocale
        )
        val symbol = EosSymbolFormatter.formatSymbol(balance.symbol, blockchainType)
        return "$value $symbol"
    }

    private fun formatBalanceDigits(amount: Double, precision: Int, ignoreLocale: Boolean): String {
        val pattern = if (precision > 0) {
            "0." + "0".repeat(precision)
        } else {
            "0"
        }

        val decimalFormat = DecimalFormat(pattern, DecimalFormatRoundingMode.CEILING, ignoreLocale)

        return decimalFormat.format(amount)
    }
}