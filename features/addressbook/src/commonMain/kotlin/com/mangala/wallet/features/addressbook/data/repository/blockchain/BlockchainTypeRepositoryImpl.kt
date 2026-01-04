package com.mangala.wallet.features.addressbook.data.repository.blockchain

import com.mangala.wallet.features.addressbook.data.local.blockchain.BlockchainLocalDataSource
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithBlockchainModel
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.TokenInformationEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository
import kotlinx.coroutines.flow.Flow

class BlockchainTypeRepositoryImpl(
    private val localDataSource: BlockchainLocalDataSource
) : BlockchainRepository {
    override suspend fun getBlockchainTypeById(id: String): BlockchainTypeEntity? {
        return localDataSource.getBlockchainTypeById(id)
    }

    override suspend fun getAllBlockchainTypes(): List<BlockchainTypeEntity> {
        return localDataSource.getAllBlockchainTypes()
    }

    override suspend fun insertBlockchainType(blockchainType: BlockchainTypeEntity): String {
        return localDataSource.insertBlockchainType(blockchainType)
    }

    override suspend fun updateBlockchainType(blockchainType: BlockchainTypeEntity): Boolean {
        return localDataSource.updateBlockchainType(blockchainType)
    }

    override suspend fun deleteBlockchainType(id: String): Boolean {
        return localDataSource.deleteBlockchainType(id)
    }

    override suspend fun getWalletAddressesByContactId(contactId: String): List<WalletAddressEntity> {
        return localDataSource.getWalletAddressesByContactId(contactId)
    }

    override suspend fun getWalletAddressesWithBlockchainByContactId(contactId: String): List<WalletAddressWithBlockchainModel> {
        val walletAddresses = localDataSource.getWalletAddressesByContactId(contactId)
        return walletAddresses.mapNotNull { walletAddress ->
            val blockchainType = localDataSource.getBlockchainTypeById(walletAddress.blockchainTypeId)
            blockchainType?.let {
                WalletAddressWithBlockchainModel(
                    walletAddress = walletAddress,
                    blockchainType = it
                )
            }
        }
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

    override suspend fun markWalletAddressAsPrimary(walletAddressId: String): Boolean {
        return localDataSource.markWalletAddressAsPrimary(walletAddressId)
    }

    override suspend fun verifyWalletAddress(walletAddressId: String): Boolean {
        return localDataSource.verifyWalletAddress(walletAddressId)
    }

    override suspend fun findContactByWalletAddress(address: String): ContactEntity? {
        return localDataSource.findContactByWalletAddress(address)
    }

    override suspend fun getTokenInformationById(id: String): TokenInformationEntity? {
        return localDataSource.getTokenInformationById(id)
    }

    override suspend fun getTokenInformationByBlockchainType(blockchainTypeId: String): List<TokenInformationEntity> {
        return localDataSource.getTokenInformationByBlockchainType(blockchainTypeId)
    }

    override suspend fun getNativeTokenForBlockchain(blockchainTypeId: String): TokenInformationEntity? {
        return localDataSource.getNativeTokenForBlockchain(blockchainTypeId)
    }

    override suspend fun insertTokenInformation(tokenInformation: TokenInformationEntity): String {
        return localDataSource.insertTokenInformation(tokenInformation)
    }

    override suspend fun updateTokenInformation(tokenInformation: TokenInformationEntity): Boolean {
        return localDataSource.updateTokenInformation(tokenInformation)
    }

    override suspend fun deleteTokenInformation(id: String): Boolean {
        return localDataSource.deleteTokenInformation(id)
    }

    override suspend fun validateAddress(address: String, blockchainTypeId: String): Boolean {
        return localDataSource.validateAddress(address, blockchainTypeId)
    }

    override suspend fun getAllActiveBlockchainTypes(): List<BlockchainTypeEntity> {
        TODO("Not yet implemented")
    }

    override fun observeBlockchainTypes(): Flow<List<BlockchainTypeEntity>> {
        TODO("Not yet implemented")
    }
}