package com.mangala.wallet.twofactorauth.data.model

/**
 * Represents a transaction that may require 2FA verification
 */
data class Transaction(
    val id: String,
    val amount: Double,
    val toAddress: String,
    val metadata: Map<String, String> = emptyMap()
)