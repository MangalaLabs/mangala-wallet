package com.mangala.wallet.features.chains.bitcoin.domain.usecases.account

import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.model.account.BitcoinAccount
import com.mangala.wallet.features.chains.bitcoin.domain.model.account.BitcoinAccountWithBalance
import com.mangala.wallet.features.chains.bitcoin.domain.repository.account.BitcoinAccountRepository
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.toBigDecimalOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class GetAccountBalancesInBitcoinAccountUseCase(
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val bitcoinAccountRepository: BitcoinAccountRepository,
    private val getAccountTokenBalanceUseCase: GetAccountTokenBalanceUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        accountId: String,
        forceReload: Boolean,
        blockchainNetworkData: BlockchainNetworkData
    ): Flow<Resource<BitcoinAccountWithBalance>> = channelFlow {
        getAccountByIdUseCase.invokeFlow(accountId).flatMapLatest {
            val bitcoinAccount = bitcoinAccountRepository.getAccount(blockchainNetworkData.blockchainType, accountId) ?: run {
                val exception = Exception("Account not found")
                send(Resource.Error(exception))

                throw exception
            }

            send(
                Resource.Loading(
                    BitcoinAccountWithBalance(
                        bitcoinAccount,
                        Resource.Loading(null)
                    )
                )
            )

            return@flatMapLatest getAccountTokenBalanceUseCase(forceReload, blockchainNetworkData, bitcoinAccount)
        }.collectLatest {
            send(Resource.Success(it))
        }
    }
}