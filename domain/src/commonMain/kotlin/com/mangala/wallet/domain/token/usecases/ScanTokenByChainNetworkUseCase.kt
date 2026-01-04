package com.mangala.wallet.domain.token.usecases

import com.mangala.wallet.domain.token.repository.TokenRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

class ScanTokenByChainNetworkUseCase(
    private val tokenBalanceRepository: TokenRepository
) {

    suspend operator fun invoke(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String
    ): Map<String, TokenBalanceEntity> {
        return tokenBalanceRepository.getTokenBalanceByAccountIdAndBlockchainUid(
            forceReload = forceReload,
            address = address,
            blockchainType = blockchainType,
            accountId = accountId
        )
    }

    fun invokeFlow(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String
    ): Flow<Map<String, TokenBalanceEntity>> {
        return tokenBalanceRepository.getTokenBalanceByAccountIdAndBlockchainUidFlow(
            forceReload = forceReload,
            address = address,
            blockchainType = blockchainType,
            accountId = accountId
        )
    }

    fun invokeFlowResource(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String
    ): Flow<Resource<Map<String, TokenBalanceEntity>>> {
        return tokenBalanceRepository.getTokenBalanceByAccountIdAndBlockchainUidResource(
            forceReload = forceReload,
            address = address,
            blockchainType = blockchainType,
            accountId = accountId
        )
    }
}