package com.mangala.wallet.features.nft_base.data.repository

import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.features.nft_base.domain.repository.NftMetadataRepository
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.provider.ipfs.IpfsRemoteDataSource

class NftMetadataRepositoryImpl(private val remote: IpfsRemoteDataSource): NftMetadataRepository {

    override suspend fun getNftMetadata(tokenId: String, uri: String): NftCollection.Nft? {
        if (remote.isIpfsUri(uri)) {
            return when (val response = remote.getNftMetadata(uri)) {
                is ApiResponse.Success -> {
                    val imageUrl = if (remote.isIpfsUri(response.body.imageUrl.orEmpty())) {
                        remote.resolveIpfsUriToHttpUrl(response.body.imageUrl.orEmpty())
                    } else {
                        response.body.imageUrl.orEmpty()
                    }

                    NftCollection.Nft(
                        tokenId = tokenId,
                        tokenUrl = uri,
                        name = response.body.name.orEmpty(),
                        description = "",
                        image = imageUrl,
                        attributes = response.body.properties?.map {
                            it?.let {
                                NftCollection.Nft.Attribute(
                                    traitType = it.traitType.orEmpty(),
                                    value = it.value?.toString().orEmpty()
                                )
                            }
                        }?.mapNotNull { it } ?: emptyList()
                    )
                }
                is ApiResponse.Error -> {
                    null
                }
            }
        }

        return null
    }
}