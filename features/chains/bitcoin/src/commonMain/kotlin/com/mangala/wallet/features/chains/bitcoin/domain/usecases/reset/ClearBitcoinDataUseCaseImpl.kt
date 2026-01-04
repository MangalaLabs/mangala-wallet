package com.mangala.wallet.features.chains.bitcoin.domain.usecases.reset

import com.mangala.wallet.domain.reset.usecases.ClearBitcoinDataUseCase
import com.mangala.wallet.features.chains.bitcoin.data.local.account.BitcoinAccountLocalDataSource
import com.mangala.wallet.features.chains.bitcoin.data.local.balance.BitcoinBalanceLocalDataSource
import com.mangala.wallet.features.chains.bitcoin.data.local.transaction.BitcoinTransactionLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearBitcoinDataUseCaseImpl(
    private val bitcoinTransactionLocalDataSource: BitcoinTransactionLocalDataSource,
    private val bitcoinAccountLocalDataSource: BitcoinAccountLocalDataSource,
    private val bitcoinBalanceLocalDataSource: BitcoinBalanceLocalDataSource
) : ClearBitcoinDataUseCase {

    override suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            bitcoinTransactionLocalDataSource.clearAllTransactions()
            bitcoinAccountLocalDataSource.clearAllBitcoinAccounts()
            bitcoinBalanceLocalDataSource.deleteAllBalances()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}