package com.mangala.wallet.features.nft_base.domain.usecases

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.features.nft_base.domain.repository.NftRepository

class GetNftByTokenIdUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val nftRepository: NftRepository
) {
    /**
        @return NFT alongside the NftCollection that contains it
     */
    suspend operator fun invoke(
        accountId: String,
        collectionContractAddress: String,
        tokenId: String
    ): NftCollection? {
        val network = getSelectedNetworkUseCase.invoke()

        return nftRepository.getNftByTokenId(
            blockchainUid = network.blockChainUid,
            accountId = accountId,
            collectionContractAddress = collectionContractAddress,
            tokenId = tokenId
        )
    }
}