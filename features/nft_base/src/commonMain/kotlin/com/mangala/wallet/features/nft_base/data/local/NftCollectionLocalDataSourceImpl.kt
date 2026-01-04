package com.mangala.wallet.features.nft_base.data.local

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.ext.toLong
import commangalawalletdatabase.NftCollectionEntity
import commangalawalletdatabase.NftEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NftCollectionLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): NftCollectionLocalDataSource {
    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun getNftCollectionsByAccountIdAndBlockchainUid(
        accountId: String,
        blockchainUid: String
    ): List<NftCollectionEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getNftCollectionsByAccountIdAndBlockchainUid(
            accountId,
            blockchainUid
        )
            .executeAsList()
    }

    override fun getNftCollectionsByAccountIdAndBlockchainUidFlow(
        accountId: String,
        blockchainUid: String
    ): Flow<List<NftCollectionEntity>> {
        return dbQuery.getNftCollectionsByAccountIdAndBlockchainUid(accountId, blockchainUid)
            .asFlow()
            .map {
                it.executeAsList()
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun getNftCollection(
        accountId: String,
        blockchainUid: String,
        collectionContractAddress: String
    ): NftCollectionEntity? = withContext(ioDispatcher) {
        return@withContext dbQuery.getNftCollectionByAccountIdAndBlockchainUidAndContractAddress(
            accountId = accountId,
            blockchain_uid = blockchainUid,
            contractAddress = collectionContractAddress
        ).executeAsOneOrNull()
    }

    override fun getNftCollectionsByAccountIdAndBlockchainUidJoinedFlow(
        accountId: String,
        blockchainUid: String
    ): Flow<List<Pair<NftCollectionEntity, List<NftEntity>>>> {
        return dbQuery.getNftCollectionByAccountIdAndBlockchainUidAndContractAddressJoin(
            accountId = accountId,
            blockchain_uid = blockchainUid
        ).asFlow().map { queryResult ->
            queryResult.executeAsList().groupBy {
                NftCollectionEntity(
                    contractName = it.contractName,
                    type = it.type,
                    contractTickerSymbol = it.contractTickerSymbol,
                    contractAddress = it.contractAddress,
                    blockchain_uid = it.blockchain_uid,
                    accountId = it.accountId
                )
            }.map { (collection, nftAndCollectionList) ->
                val nftList = nftAndCollectionList.mapNotNull { nftAndCollection ->
                    if (nftAndCollection.tokenId != null) { // Assuming tokenId is a good indicator of a valid NftEntity
                        NftEntity(
                            tokenId = nftAndCollection.tokenId!!,
                            tokenUrl = nftAndCollection.tokenUrl.orEmpty(),
                            name = nftAndCollection.name.orEmpty(),
                            description = nftAndCollection.description.orEmpty(),
                            image = nftAndCollection.image.orEmpty(),
                            attributes = nftAndCollection.attributes.orEmpty(),
                            blockchain_uid = nftAndCollection.blockchain_uid,
                            accountId = nftAndCollection.accountId,
                            isFavorite = nftAndCollection.isFavorite.orZero(),
                            collectionContractAddress = nftAndCollection.collectionContractAddress.orEmpty()
                        )
                    } else null
                }
                Pair(collection, nftList)
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun getNft(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String
    ): NftEntity? = withContext(ioDispatcher) {
        return@withContext dbQuery.getNft(
            tokenId = tokenId,
            blockchain_uid = blockchainUid,
            accountId = accountId,
            collectionContractAddress = collectionContractAddress
        ).executeAsOneOrNull()
    }

    override suspend fun getNfts(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String
    ): List<NftEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getNfts(blockchainUid, accountId, collectionContractAddress).executeAsList()
    }

    override fun getNftFavoriteStatus(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String
    ): Flow<Boolean?> {
        return dbQuery.getNftFavoriteStatus(
            tokenId = tokenId,
            blockchain_uid = blockchainUid,
            accountId = accountId,
            collectionContractAddress = collectionContractAddress
        ).asFlow().map {
            it.executeAsOneOrNull() == 1L
        }.flowOn(ioDispatcher)
    }

    override suspend fun updateNftFavoriteStatus(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String,
        isFavorite: Boolean
    ) = withContext(ioDispatcher) {
        dbQuery.updateNftFavoriteStatus(
            isFavorite = isFavorite.toLong(),
            tokenId = tokenId,
            blockchain_uid = blockchainUid,
            accountId = accountId,
            collectionContractAddress = collectionContractAddress
        )
    }

    override suspend fun insertNft(nft: NftEntity) = withContext(ioDispatcher) {
        upsertNftWithoutSuspend(nft)
    }

    override suspend fun insertNfts(nfts: List<NftEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            nfts.map {
                upsertNftWithoutSuspend(it)
            }
        }
    }

    override suspend fun insertNftCollection(nft: NftCollectionEntity) = withContext(ioDispatcher) {
        insertNftCollectionNoSuspend(nft)
    }

    override suspend fun insertNftCollections(nfts: List<NftCollectionEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            nfts.map {
                insertNftCollectionNoSuspend(it)
            }
        }
    }

    override suspend fun insertNftCollectionAndNfts(nftCollectionAndNft: Pair<NftCollectionEntity, List<NftEntity>>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            insertNftCollectionNoSuspend(nftCollectionAndNft.first)
            nftCollectionAndNft.second.map {
                upsertNftWithoutSuspend(it)
            }
        }
    }

    override suspend fun insertNftCollectionsAndNfts(
        nftCollectionsAndNfts: List<Pair<NftCollectionEntity, List<NftEntity>>>
    ) = withContext(ioDispatcher) {
        dbQuery.transaction {
            nftCollectionsAndNfts.map { (nftCollection, nfts) ->
                insertNftCollectionNoSuspend(nftCollection)
                nfts.map {
                    upsertNftWithoutSuspend(it)
                }
            }
        }
    }

    override suspend fun deleteNftsCollectionsByAccountIdAndBlockchain(accountId: String, blockchainUid: String) = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteNftCollectionsByAccountIdAndBlockchainUid(
                accountId = accountId,
                blockchain_uid = blockchainUid
            )
            dbQuery.deleteNftsByAccountIdAndBlockchainUid(
                blockchain_uid = blockchainUid,
                accountId = accountId
            )
        }
    }

    override suspend fun deleteNftById(
        accountId: String,
        blockchainUid: String,
        collectionContractAddress: String,
        tokenId: String
    ) = withContext(ioDispatcher) {
        dbQuery.deleteNftById(
            accountId = accountId,
            blockchain_uid = blockchainUid,
            collectionContractAddress = collectionContractAddress,
            tokenId = tokenId
        )
    }

    override suspend fun getCacheLastSynchedTimestampByAccountIdAndBlockchain(
        accountId: String,
        blockchainUid: String
    ): Long? = withContext(ioDispatcher) {
        return@withContext dbQuery.getNftCacheLastSynchedTimestamp(accountId, blockchainUid).executeAsOneOrNull()
    }

    override suspend fun insertOrReplaceNftCacheMetadata(
        accountId: String,
        blockchainUid: String,
        timestamp: Long
    ) = withContext(ioDispatcher) {
        dbQuery.insertOrReplaceNftCacheMetadata(accountId, blockchainUid, timestamp)
    }
    
    override suspend fun clearAllNftData() = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.clearAllNftEntities()
            dbQuery.clearAllNftCollectionEntities()
            dbQuery.clearAllNftCacheMetadataEntities()
        }
    }

    private fun upsertNftWithoutSuspend(nft: NftEntity) {
        dbQuery.upsertNft(
            tokenId = nft.tokenId,
            tokenUrl = nft.tokenUrl,
            name = nft.name,
            description = nft.description,
            image = nft.image,
            blockchain_uid = nft.blockchain_uid,
            accountId = nft.accountId,
            attributes = nft.attributes,
            collectionContractAddress = nft.collectionContractAddress
        )
    }

    private fun insertNftCollectionNoSuspend(nft: NftCollectionEntity) {
        dbQuery.insertNftCollection(
            contractName = nft.contractName,
            type = nft.type,
            contractAddress = nft.contractAddress,
            contractTickerSymbol = nft.contractTickerSymbol,
            blockchain_uid = nft.blockchain_uid,
            accountId = nft.accountId,
        )
    }
}
