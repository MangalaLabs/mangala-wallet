package com.mangala.wallet.features.nft_base.data.local

import commangalawalletdatabase.NftCollectionEntity
import commangalawalletdatabase.NftEntity
import kotlinx.coroutines.flow.Flow

interface NftCollectionLocalDataSource {


    suspend fun getNftCollectionsByAccountIdAndBlockchainUid(
        accountId: String,
        blockchainUid: String
    ): List<NftCollectionEntity>

    fun getNftCollectionsByAccountIdAndBlockchainUidFlow(
        accountId: String,
        blockchainUid: String
    ): Flow<List<NftCollectionEntity>>

    suspend fun getNftCollection(
        accountId: String,
        blockchainUid: String,
        collectionContractAddress: String
    ): NftCollectionEntity?

    fun getNftCollectionsByAccountIdAndBlockchainUidJoinedFlow(
        accountId: String,
        blockchainUid: String,
    ): Flow<List<Pair<NftCollectionEntity, List<NftEntity>>>>

    suspend fun getNft(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String
    ): NftEntity?

    suspend fun getNfts(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String
    ): List<NftEntity>

    fun getNftFavoriteStatus(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String
    ): Flow<Boolean?>

    suspend fun updateNftFavoriteStatus(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String,
        isFavorite: Boolean
    )

    suspend fun insertNft(nft: NftEntity)

    suspend fun insertNfts(nfts: List<NftEntity>)

    suspend fun insertNftCollection(nft: NftCollectionEntity)

    suspend fun insertNftCollections(nfts: List<NftCollectionEntity>)

    suspend fun insertNftCollectionsAndNfts(
        nftCollectionsAndNfts: List<Pair<NftCollectionEntity, List<NftEntity>>>
    )

    suspend fun insertNftCollectionAndNfts(
        nftCollectionAndNft: Pair<NftCollectionEntity, List<NftEntity>>
    )

    suspend fun deleteNftsCollectionsByAccountIdAndBlockchain(
        accountId: String,
        blockchainUid: String
    )

    suspend fun deleteNftById(
        accountId: String,
        blockchainUid: String,
        collectionContractAddress: String,
        tokenId: String
    )

    suspend fun getCacheLastSynchedTimestampByAccountIdAndBlockchain(
        accountId: String,
        blockchainUid: String
    ): Long?

    suspend fun insertOrReplaceNftCacheMetadata(
        accountId: String,
        blockchainUid: String,
        timestamp: Long
    )
    
    suspend fun clearAllNftData()
}