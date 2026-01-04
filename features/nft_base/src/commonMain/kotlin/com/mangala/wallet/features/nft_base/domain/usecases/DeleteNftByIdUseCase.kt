package com.mangala.wallet.features.nft_base.domain.usecases

import com.mangala.wallet.features.nft_base.domain.repository.NftRepository

class DeleteNftByIdUseCase(
    private val repository: NftRepository
) {

    suspend operator fun invoke(
        accountId: String,
        blockchainUid: String,
        collectionContractAddress: String,
        tokenId: String
    ) {
        repository.deleteNftById(accountId, blockchainUid, collectionContractAddress, tokenId)
    }
}