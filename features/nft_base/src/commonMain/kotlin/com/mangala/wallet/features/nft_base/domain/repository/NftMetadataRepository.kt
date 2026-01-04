package com.mangala.wallet.features.nft_base.domain.repository

import com.mangala.wallet.features.nft_base.domain.model.NftCollection

interface NftMetadataRepository {

    suspend fun getNftMetadata(tokenId: String, uri: String): NftCollection.Nft?
}