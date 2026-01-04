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

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.Locale

class BalanceFormatterTest {

    @Test
    fun `Given token balance with precision 0, when format balance, then return formatted balance without any decimal places`() {
        val balance = Balance(100.0, "BRAM", precision = 0)

        val formattedBalance = BalanceFormatter.formatEosBalance(balance, ignoreLocale = true)

        assertEquals("100 BRAM", formattedBalance)
    }

    @Test
    fun `Given token balance with precision 4, when format balance, then return formatted balance with decimal places and padded zeroes`() {
        val balance = Balance(0.04, "EOS", precision = 4)

        val formattedBalance = BalanceFormatter.formatEosBalance(balance, ignoreLocale = true)

        assertEquals("0.0400 EOS", formattedBalance)
    }

    @Test
    fun `Given token balance with precision 4 and locale with comma as decimal place, when format balance ignore locale, then return formatted balance with decimal places and padded zeroes`() {
        val balance = Balance(0.04, "EOS", precision = 4)

        Locale.setDefault(Locale("vi", "vn"))
        val formattedBalance = BalanceFormatter.formatEosBalance(balance, ignoreLocale = true)

        assertEquals("0.0400 EOS", formattedBalance)
    }

    @Test
    fun `Given token balance with precision 4 and locale with comma as decimal place, when format balance, then return formatted balance with localized decimal places and padded zeroes`() {
        val balance = Balance(0.04, "EOS", precision = 4)

        Locale.setDefault(Locale("vi", "vn"))
        val formattedBalance = BalanceFormatter.formatEosBalance(balance, ignoreLocale = false)

        assertEquals("0,0400 EOS", formattedBalance)
    }
}