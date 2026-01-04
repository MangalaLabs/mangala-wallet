package com.mangala.wallet.features.addressbook.data.local.contact

import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress

interface WalletAddressLocalDataSource {
    suspend fun getWalletAddressesForContact(contactId: String, limit: Int = 5, offset: Int = 0): List<WalletAddressWithNetworkModel>
    suspend fun countWalletAddressesForContact(contactId: String): Int
    suspend fun getDefaultWalletAddress(contactId: String, blockchainNetworkId: String): WalletAddressEntity?
    suspend fun setWalletAddressAsDefault(id: String, contactId: String, blockchainNetworkId: String): Boolean
    suspend fun getWalletAddressesByNetwork(networkId: String, limit: Int = 5, offset: Int = 0): List<WalletAddressWithNetworkModel>
    suspend fun insertWalletAddress(walletAddress: WalletAddressEntity): String
    suspend fun updateWalletAddress(walletAddress: WalletAddressEntity): Boolean
    suspend fun deleteWalletAddress(id: String): Boolean
    suspend fun deleteWalletAddressesByContactId(contactId: String): Boolean
    suspend fun insertWalletAddressesBatch(walletAddresses: List<WalletAddressEntity>): Map<WalletAddressEntity, String>
    suspend fun getWalletAddressesById(walletId: String): WalletAddressWithNetworkModel
    suspend fun getWalletAddressByAddress(address: String): WalletAddressEntity?
    suspend fun getContactsWithAddressesByBlockchainType(blockchainId: String): List<ContactWithAddress>
    suspend fun getGroupWalletsByWalletIds(walletIds: List<String>): List<GroupWallet>
    
    /**
     * Get group wallets filtered by blockchain type and search query on wallet alias
     * This optimized query directly returns GroupWallet objects without mapping from ContactWithAddress
     * @param blockchainId blockchain type ID
     * @param limit maximum number of items to return
     * @param offset starting position for pagination
     * @param searchQuery optional search query to filter by wallet alias, address, or contact name
     * @return List of GroupWallet objects matching the criteria
     */
    suspend fun getGroupWalletsByBlockchainAndAlias(
        blockchainId: String,
        limit: Int,
        offset: Int,
        searchQuery: String = ""
    ): List<GroupWallet>
    
    /**
     * Count the total number of group wallets matching blockchain type and search query
     * Used for pagination
     * @param blockchainId blockchain type ID
     * @param searchQuery optional search query to filter by wallet alias, address, or contact name
     * @return total count of matching wallet addresses
     */
    suspend fun countGroupWalletsByBlockchainAndAlias(
        blockchainId: String,
        searchQuery: String = ""
    ): Int
}