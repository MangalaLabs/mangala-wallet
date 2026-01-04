package com.mangala.wallet.features.conversationui.domain.service

import com.mangala.wallet.features.conversationui.presentation.WalletContext
import com.mangala.wallet.features.conversationui.presentation.TokenInfo
import com.mangala.wallet.features.conversationui.presentation.WalletAccount
import com.mangala.wallet.features.conversationui.presentation.NetworkInfo
import com.mangala.wallet.features.conversationui.presentation.Transaction
import com.mangala.wallet.features.addressbook.data.model.ContactModel

/**
 * Interface for providing wallet context and related data for transaction flows
 */
interface WalletContextProvider {
    /**
     * Get the complete wallet context including current network, tokens, accounts, and contacts
     */
    suspend fun getCurrentContext(): WalletContext
    
    /**
     * Get available tokens for the current network
     */
    suspend fun getAvailableTokens(networkId: String? = null): List<TokenInfo>
    
    /**
     * Get wallets available on a specific network
     */
    suspend fun getWalletsOnNetwork(networkId: String): List<WalletAccount>
    
    /**
     * Get all contacts from the address book
     */
    suspend fun getContacts(): List<ContactModel>
    
    /**
     * Get recent transactions for the current user
     */
    suspend fun getRecentTransactions(limit: Int = 10): List<Transaction>
    
    /**
     * Get current network information
     */
    suspend fun getCurrentNetwork(): NetworkInfo
    
    /**
     * Get available networks
     */
    suspend fun getAvailableNetworks(): List<NetworkInfo>
    
    /**
     * Get token information by symbol
     */
    suspend fun getTokenBySymbol(symbol: String, networkId: String? = null): TokenInfo?
    
    /**
     * Get wallet balance for a specific token
     */
    suspend fun getWalletBalance(walletId: String, tokenSymbol: String): String?
    
    /**
     * Check if a wallet has sufficient balance for a transaction
     */
    suspend fun hasSufficientBalance(
        walletId: String,
        tokenSymbol: String,
        amount: String,
        includeNetworkFees: Boolean = true
    ): Boolean
}