package com.mangala.wallet.features.nft_base.data.repository

import com.mangala.wallet.features.nft_base.data.local.NftCollectionLocalDataSource
import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.features.nft_base.data.repository.mapper.toNft
import com.mangala.wallet.features.nft_base.data.repository.mapper.toNftCollectionEntity
import com.mangala.wallet.features.nft_base.data.repository.mapper.toNftEntityList
import com.mangala.wallet.features.nft_base.data.repository.mapper.toNfts
import com.mangala.wallet.features.nft_base.domain.repository.NftRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.provider.BaseBlockExplorerRemoteDataSource
import com.mangala.wallet.remote.utils.networkBoundResource
import com.mangala.wallet.utils.Constants.NFT_CACHE_TIMEOUT_MILLIS
import com.mangala.wallet.utils.currentTimeInMillis
import com.mangala.wallet.utils.ext.orZero
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class NftRepositoryImpl(
    private val getBlockExplorerRemoteDataSource: (BlockchainType) -> BaseBlockExplorerRemoteDataSource,
    private val local: NftCollectionLocalDataSource,
    private val parsingJson: Json
) : NftRepository {

    override suspend fun insertNft(accountId: String, blockchainUid: String, nftCollection: NftCollection) {
        val collection = nftCollection.toNftCollectionEntity(accountId, blockchainUid)
        val nfts = nftCollection.nft.toNftEntityList(
            blockchainUid,
            accountId,
            nftCollection.contractAddress
        )
        local.insertNftCollectionAndNfts(collection to nfts)
    }

    override suspend fun getNftsForAddress(
        accountId: String,
        blockchainType: BlockchainType,
        walletAddress: String
    ): Resource<List<NftCollection>> {
        val network = getBlockExplorerRemoteDataSource(blockchainType)
        network.getNftsForAddress(blockchainType, walletAddress).let {
            val result = if (it is ApiResponse.Success) {
                Resource.Success(it.body.toNfts())
            } else {
                Resource.Error(Exception())
            }

            if (result is Resource.Success) {
                cacheNfts(result, accountId, blockchainType)
            }

            return result
        }
    }

    override suspend fun getNftsForAddressFlow(
        accountId: String,
        blockchainType: BlockchainType,
        walletAddress: String,
        forceRefresh: Boolean
    ): Flow<Resource<List<NftCollection>>> {
        val network = getBlockExplorerRemoteDataSource(blockchainType)
        return networkBoundResource(
            query = {
                local.getNftCollectionsByAccountIdAndBlockchainUidJoinedFlow(
                    accountId,
                    blockchainType.uid
                ).map {
                    val result = it.map {
                        it.first.toNft(it.second.toNfts())
                    }
                    result
                }
            },
            fetch = {
                val result = network.getNftsForAddress(blockchainType, walletAddress)
                if (result is ApiResponse.Success) {
                    Resource.Success(result.body.toNfts())
                } else {
                    Resource.Error(Exception())
                }
            },
            saveFetchResult = {
                cacheNfts(it, accountId, blockchainType)
            },
            shouldFetch = {
                if (forceRefresh) return@networkBoundResource true
                val timeNow = currentTimeInMillis()
                val lastSynchedTimestamp = local.getCacheLastSynchedTimestampByAccountIdAndBlockchain(
                    accountId,
                    blockchainType.uid
                ).orZero()
                timeNow - lastSynchedTimestamp > NFT_CACHE_TIMEOUT_MILLIS || it.isEmpty()
            },
        )
    }

    private suspend fun cacheNfts(
        it: Resource<List<NftCollection>>,
        accountId: String,
        blockchainType: BlockchainType
    ) {
        local.deleteNftsCollectionsByAccountIdAndBlockchain(
            accountId,
            blockchainType.uid
        )

        val currentTime = currentTimeInMillis()
        it.data?.let {
            local.insertNftCollectionsAndNfts(
                it.map {
                    it.toNftCollectionEntity(
                        accountId,
                        blockchainType.uid
                    ) to it.nft.toNftEntityList(
                        blockchainType.uid,
                        accountId,
                        it.contractAddress
                    )
                }
            )
            local.insertOrReplaceNftCacheMetadata(
                accountId,
                blockchainType.uid,
                currentTime
            )
        }
    }

    override suspend fun getNftByTokenId(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String
    ): NftCollection? {
        val collection = local.getNftCollection(accountId, blockchainUid, collectionContractAddress)
            ?: return null
        val nft = local.getNft(
            blockchainUid = blockchainUid,
            accountId = accountId,
            collectionContractAddress = collectionContractAddress,
            tokenId = tokenId
        ) ?: return null

        return collection.toNft(listOf(nft.toNft()))
    }

    override fun getNftFavoriteStatus(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String
    ): Flow<Boolean?> {
        return local.getNftFavoriteStatus(
            blockchainUid = blockchainUid,
            accountId = accountId,
            collectionContractAddress = collectionContractAddress,
            tokenId = tokenId
        )
    }

    override suspend fun updateNftFavoriteStatus(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String,
        isFavorite: Boolean
    ) {
        local.updateNftFavoriteStatus(
            blockchainUid = blockchainUid,
            accountId = accountId,
            collectionContractAddress = collectionContractAddress,
            tokenId = tokenId,
            isFavorite = isFavorite
        )
    }

    override suspend fun deleteNftById(
        accountId: String,
        blockchainUid: String,
        collectionContractAddress: String,
        tokenId: String
    ) {
        local.deleteNftById(
            accountId = accountId,
            blockchainUid = blockchainUid,
            collectionContractAddress = collectionContractAddress,
            tokenId = tokenId
        )
    }
    
    override suspend fun clearAllNftData() =
        local.clearAllNftData()
}