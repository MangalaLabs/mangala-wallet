package com.mangala.wallet.features.nft_base.domain.usecases

import com.mangala.wallet.features.nft_base.domain.repository.NftRepository

class UpdateNftFavoriteStatusUseCase(
    private val nftRepository: NftRepository
) {

    suspend operator fun invoke(
        blockchainUid: String,
        accountId: String,
        collectionContractAddress: String,
        tokenId: String,
        isFavorite: Boolean
    ) = nftRepository.updateNftFavoriteStatus(
        blockchainUid = blockchainUid,
        accountId = accountId,
        collectionContractAddress = collectionContractAddress,
        tokenId = tokenId,
        isFavorite = isFavorite
    )
}