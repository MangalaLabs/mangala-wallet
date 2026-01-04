package com.mangala.wallet.features.nft_base.domain.usecases

import com.mangala.wallet.features.nft_base.domain.repository.NftRepository

class GetNftFavoriteStatusUseCase(
    private val nftRepository: NftRepository
) {

    operator fun invoke(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String
    ) = nftRepository.getNftFavoriteStatus(
        blockchainUid = blockchainUid,
        accountId = accountId,
        collectionContractAddress = collectionContractAddress,
        tokenId = tokenId
    )
}