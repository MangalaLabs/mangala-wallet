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
package com.memtrip.eos.abi.reader.bytereader

import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.utils.ext.toBigDecimal
import com.mangala.wallet.utils.ext.weiToEth
import com.mangala.wallet.utils.toByteArray
import com.memtrip.eos.abi.reader.ByteReader
import okio.internal.commonToUtf8String

class AssetReader(
    private val currencySymbolReader: CurrencySymbolReader = CurrencySymbolReader()
) {
    fun get(buffer: ByteArrayReaderBuffer, reader: ByteReader): String {
        val amount = buffer.readLong()

        val (precision, symbol) = currencySymbolReader.get(buffer, reader)

        val amountWithPrecision = amount.toBigDecimal().weiToEth(precision)

        return BalanceFormatter.formatEosBalance(
            Balance(
                amountWithPrecision.doubleValue(false),
                symbol,
                precision
            ),
            ignoreLocale = true
        )
    }
}