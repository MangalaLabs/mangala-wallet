package com.mangala.wallet.features.addressbook.domain.model.group

/**
 * Represents a wallet address within a group, with minimal necessary information for display
 */
data class GroupWallet(
    val walletId: String,
    val walletAlias: String?,
    val walletType: String?,
    val walletAddress: String,
    val contactId: String, // Add contactId field for database relationships
    val contactName: String,
    val blockchainTypeSymbol: String,
    val isSensitive: Boolean = false
)