package com.mangala.wallet.features.chains.bitcoin.data.repository.balance

import com.mangala.wallet.features.chains.bitcoin.data.local.balance.BitcoinBalanceLocalDataSource
import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.MempoolRemoteDataSource
import com.mangala.wallet.features.chains.bitcoin.data.repository.electrum.mapper.toBitcoinBalance
import com.mangala.wallet.features.chains.bitcoin.domain.model.balance.BitcoinBalance
import com.mangala.wallet.features.chains.bitcoin.domain.repository.balance.BitcoinBalanceRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.utils.networkBoundResource
import com.mangala.wallet.utils.ext.orZero
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class BitcoinBalanceRepositoryImpl(
    private val mempoolRemoteDataSource: MempoolRemoteDataSource,
    private val bitcoinBalanceLocalDataSource: BitcoinBalanceLocalDataSource
) : BitcoinBalanceRepository {

    override suspend fun getBalance(
        forceRefresh: Boolean,
        accountId: String,
        address: String,
        blockchainType: BlockchainType
    ): Flow<Resource<BitcoinBalance?>> = networkBoundResource(
        query = {
            bitcoinBalanceLocalDataSource.getBalance(accountId, blockchainType)
        },
        fetch = {
            mempoolRemoteDataSource.getBalance(blockchainType, address)
        },
        saveFetchResult = { balance ->
            bitcoinBalanceLocalDataSource.insertOrUpdateBalance(
                accountId = accountId,
                blockchainType = blockchainType,
                confirmedBalance = balance.chainStats?.fundedTxoSum.orZero() - balance.chainStats?.spentTxoSum.orZero(),
                unconfirmedBalance = balance.mempoolStats?.fundedTxoSum.orZero() - balance.mempoolStats?.spentTxoSum.orZero(),
                lastUpdated = Clock.System.now().toEpochMilliseconds()
            )
        },
        shouldFetch = {
            if (it == null || forceRefresh) return@networkBoundResource true

            val currentTime = Clock.System.now().toEpochMilliseconds()

            currentTime - it.last_updated > BALANCE_CACHE_EXPIRATION_TIME_MILLIS
        },
        entityToDomain = {
            it?.toBitcoinBalance()
        }
    )

    companion object {
        private const val BALANCE_CACHE_EXPIRATION_TIME_MILLIS = 3 * 60 * 1000 // 3 minutes
    }
}