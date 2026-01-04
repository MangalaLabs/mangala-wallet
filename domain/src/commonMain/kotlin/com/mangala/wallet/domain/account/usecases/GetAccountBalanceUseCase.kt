package com.mangala.wallet.domain.account.usecases

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.token.usecases.ScanTokenByChainNetworkUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

// use GetBalanceFromCovalenthqUseCase and GetTokenBalanceByTokenIdUseCase. First time, check if the token is already in the database. If not, call GetBalanceFromCovalenthqUseCase and save the token in the database. Then, call GetTokenBalanceByTokenIdUseCase to get the token balance from api then insert it in the database. Finally, return the token balance from the database.

class GetAccountBalanceUseCase(
    private val scanTokenByChainNetworkUseCase: ScanTokenByChainNetworkUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase
) {

    suspend operator fun invoke(forceReload: Boolean, address: String, accountId: String, sparkline: Boolean): List<TokenBalanceModel> {
        val selectedNetwork = getSelectedNetworkUseCase()
        val blockchainType = BlockchainType.fromUid(selectedNetwork.blockChainUid)
        return this(forceReload, address, blockchainType, accountId, sparkline)
    }

    suspend fun invokeFlow(forceReload: Boolean, address: String, accountId: String, sparkline: Boolean): Flow<List<TokenBalanceModel>> {
        val selectedNetwork = getSelectedNetworkUseCase()
        val blockchainType = BlockchainType.fromUid(selectedNetwork.blockChainUid)
        return invokeFlow(forceReload, address, blockchainType, accountId, sparkline)
    }

    suspend operator fun invoke(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String,
        sparkline: Boolean
    ): List<TokenBalanceModel> {
        val tokensScan = scanTokenByChainNetworkUseCase.invoke(forceReload, address, blockchainType, accountId)
        return fetchTokenPriceUseCase.invoke(forceReload, tokensScan, sparkline)
    }

    suspend fun invokeFlow(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String,
        sparkline: Boolean
    ): Flow<List<TokenBalanceModel>> {
        return scanTokenByChainNetworkUseCase.invokeFlow(forceReload, address, blockchainType, accountId).map {
            fetchTokenPriceUseCase.invoke(forceReload, it, sparkline)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun invokeFlowResource(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String
    ): Flow<Resource<List<TokenBalanceModel>>> {
        return scanTokenByChainNetworkUseCase.invokeFlowResource(
            forceReload,
            address,
            blockchainType,
            accountId
        ).flatMapMerge {
            fetchTokenPriceUseCase.getTokenPriceWithSparkline(
                forceReload = forceReload,
                tokenScan = it.data.orEmpty(),
            )
        }
    }
}