package com.mangala.wallet.features.nft_base.domain.usecases

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.features.nft_base.domain.repository.NftRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

class GetNftBalanceUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val repository: NftRepository
) {

    suspend operator fun invoke(
        accountId: String,
        walletAddress: String
    ): Resource<List<NftCollection>> {
        val chainNetwork = getSelectedNetworkUseCase()
        val blockchainType = BlockchainType.fromUid(chainNetwork.blockChainUid)

        return repository.getNftsForAddress(accountId, blockchainType, walletAddress)
    }

    suspend fun invokeFlow(
        accountId: String,
        walletAddress: String,
        forceRefresh: Boolean
    ): Flow<Resource<List<NftCollection>>> {
        val chainNetwork = getSelectedNetworkUseCase()
        val blockchainType = BlockchainType.fromUid(chainNetwork.blockChainUid)
        
        return repository.getNftsForAddressFlow(accountId, blockchainType, walletAddress, forceRefresh)
    }
}