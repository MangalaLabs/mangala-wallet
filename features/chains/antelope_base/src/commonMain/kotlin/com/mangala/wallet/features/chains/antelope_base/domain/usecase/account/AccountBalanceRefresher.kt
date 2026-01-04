package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AccountBalanceRefresher(
    private val refreshAccountBalanceUseCase: RefreshAccountBalanceUseCase
) {
    private var refreshJob: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh(accountName: String, blockchainType: BlockchainType, refreshTokenBalance: Boolean) {
        refreshJob?.cancel()
        refreshJob = GlobalScope.launch {
            val refreshInterval = Chain.fromBlockchainType(blockchainType).blockIntervalMillis * 2

            delay(refreshInterval)
            refreshAccountBalanceUseCase(
                accountName,
                blockchainType,
                refreshTokenBalance = refreshTokenBalance
            )
        }
    }
}