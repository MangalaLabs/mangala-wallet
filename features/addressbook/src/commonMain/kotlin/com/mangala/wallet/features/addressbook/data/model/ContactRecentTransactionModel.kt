package com.mangala.wallet.features.addressbook.data.model

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.utils.ext.formatCompact

class ContactRecentTransactionModel(
    val contactId: String,
    val contactName: String,
    val walletAddress: String,
    val walletAddressId: String,
    val walletAlias: String,
    val walletSensitive: Boolean?,
    val blockchainUid: String,
    val blockchainName: String,
    val blockchainSymbol: String,
    val blockchainIcon: String,
    val lastTransactionTime: Long,
    val lastTransactionAmount: String,
    val lastTokenSymbol: String,
    val transactionStatus: String,
    val isSender: Boolean,
    val isFavorite: Boolean,
    val transactionId: String,
    val avatar: String? = null, // Thêm trường avatar lưu đường dẫn hoặc giá trị của avatar
    val memo: String = "",
    val privacyDisplayMode: DisplayMode = DisplayMode.FULL, // ✅ FIX: Add privacy display mode
) {
    val formattedAmount: String by lazy {
        if (lastTransactionAmount.isNotEmpty()) {
            try {
                val amountBigDecimal = lastTransactionAmount.toBigDecimal()
                val formattedAmount = amountBigDecimal.formatCompact(decimalScale = 3, useScaleBasedCompactForSmallerThanOne = true)
                if (formattedAmount.isNotEmpty()) {
                    "$formattedAmount $lastTokenSymbol"
                } else {
                    "0 $lastTokenSymbol"
                }
            }catch (e: Exception) {
                "0 $lastTokenSymbol" // Fallback in case of parsing error
            }
        } else {
            "0 $lastTokenSymbol"
        }
    }
}