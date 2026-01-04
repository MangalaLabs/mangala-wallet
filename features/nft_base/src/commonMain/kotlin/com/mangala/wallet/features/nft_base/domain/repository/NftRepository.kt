package com.mangala.wallet.features.nft_base.domain.repository

import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface NftRepository {

    suspend fun insertNft(accountId: String, blockchainUid: String, nftCollection: NftCollection)

    suspend fun getNftsForAddress(
        accountId: String,
        blockchainType: BlockchainType,
        walletAddress: String,
    ): Resource<List<NftCollection>>

    suspend fun getNftsForAddressFlow(
        accountId: String,
        blockchainType: BlockchainType,
        walletAddress: String,
        forceRefresh: Boolean
    ): Flow<Resource<List<NftCollection>>>

    suspend fun getNftByTokenId(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String
    ): NftCollection?

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

    suspend fun deleteNftById(
        accountId: String,
        blockchainUid: String,
        collectionContractAddress: String,
        tokenId: String
    )
    
    suspend fun clearAllNftData()
}