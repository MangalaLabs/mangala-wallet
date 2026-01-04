package com.mangala.wallet.features.addressbook.data.repository.contact

import com.mangala.wallet.features.addressbook.data.local.contact.WalletAddressLocalDataSource
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress

class WalletAddressRepositoryImpl(
    private val localDataSource: WalletAddressLocalDataSource
) : WalletAddressRepository {

    override suspend fun getWalletAddressesForContact(contactId: String, limit: Int, offset: Int): List<WalletAddressWithNetworkModel> {
        return localDataSource.getWalletAddressesForContact(contactId, limit, offset)
    }

    override suspend fun countWalletAddressesForContact(contactId: String): Int {
        return localDataSource.countWalletAddressesForContact(contactId)
    }

    override suspend fun getDefaultWalletAddress(contactId: String, blockchainNetworkId: String): WalletAddressEntity? {
        return localDataSource.getDefaultWalletAddress(contactId, blockchainNetworkId)
    }

    override suspend fun setWalletAddressAsDefault(id: String, contactId: String, blockchainNetworkId: String): Boolean {
        return localDataSource.setWalletAddressAsDefault(id, contactId, blockchainNetworkId)
    }

    override suspend fun getWalletAddressesByNetwork(networkId: String, limit: Int, offset: Int): List<WalletAddressWithNetworkModel> {
        return localDataSource.getWalletAddressesByNetwork(networkId, limit, offset)
    }

    override suspend fun insertWalletAddress(walletAddress: WalletAddressEntity): String {
        return localDataSource.insertWalletAddress(walletAddress)
    }

    override suspend fun updateWalletAddress(walletAddress: WalletAddressEntity): Boolean {
        return localDataSource.updateWalletAddress(walletAddress)
    }

    override suspend fun deleteWalletAddress(id: String): Boolean {
        return localDataSource.deleteWalletAddress(id)
    }

    override suspend fun deleteWalletAddressesByContactId(contactId: String): Boolean {
        return localDataSource.deleteWalletAddressesByContactId(contactId)
    }

    override suspend fun insertWalletAddressesBatch(walletAddresses: List<WalletAddressEntity>): Map<WalletAddressEntity, String> {
        return localDataSource.insertWalletAddressesBatch(walletAddresses)
    }

    override suspend fun getWalletAddressById(walletId: String): WalletAddressWithNetworkModel {
        return localDataSource.getWalletAddressesById(walletId)
    }

    override suspend fun getWalletAddressByAddress(address: String): WalletAddressEntity? {
        return localDataSource.getWalletAddressByAddress(address)
    }

    override suspend fun getContactsWithAddressesByBlockchainType(blockchainId: String): List<ContactWithAddress> {
        return localDataSource.getContactsWithAddressesByBlockchainType(blockchainId)
    }

    override suspend fun getGroupWalletsByWalletIds(walletIds: List<String>): List<GroupWallet> {
        return localDataSource.getGroupWalletsByWalletIds(walletIds)
    }
    
    override suspend fun getGroupWalletsByBlockchainAndAlias(
        blockchainId: String,
        limit: Int,
        offset: Int,
        searchQuery: String
    ): List<GroupWallet> {
        return localDataSource.getGroupWalletsByBlockchainAndAlias(
            blockchainId = blockchainId,
            limit = limit,
            offset = offset,
            searchQuery = searchQuery
        )
    }
}