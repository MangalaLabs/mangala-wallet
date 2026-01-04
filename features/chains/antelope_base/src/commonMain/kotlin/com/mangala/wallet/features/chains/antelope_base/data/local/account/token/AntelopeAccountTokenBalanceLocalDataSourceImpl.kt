package com.mangala.wallet.features.chains.antelope_base.data.local.account.token

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountTokenBalanceEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class AntelopeAccountTokenBalanceLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AntelopeAccountTokenBalanceLocalDataSource {
    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun getAccountTokenBalance(accountName: String, blockchainUid: String): List<AntelopeAccountTokenBalanceEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.selectAccountTokenBalance(accountName, blockchainUid).executeAsList()
    }

    override fun getAccountTokenBalanceFlow(
        accountName: String,
        blockchainUid: String
    ): Flow<List<AntelopeAccountTokenBalanceEntity>> {
        return dbQuery.selectAccountTokenBalance(accountName, blockchainUid).asFlow()
            .map { it.executeAsList() }.flowOn(ioDispatcher)
    }

    override suspend fun insertAccountTokenBalance(tokenBalanceData: List<AntelopeAccountTokenBalanceEntity>) = withContext(ioDispatcher) {
        tokenBalanceData.forEach {
            insertAccountTokenBalance(it)
        }
    }

    private suspend fun insertAccountTokenBalance(tokenBalanceData: AntelopeAccountTokenBalanceEntity) = withContext(ioDispatcher) {
        dbQuery.insertAccountTokenBalance(
            account_name = tokenBalanceData.account_name,
            blockchain_uid = tokenBalanceData.blockchain_uid,
            key = tokenBalanceData.key,
            currency = tokenBalanceData.currency,
            amount = tokenBalanceData.amount,
            contract = tokenBalanceData.contract,
            decimals = tokenBalanceData.decimals,
            name = tokenBalanceData.name,
            website = tokenBalanceData.website,
            logo = tokenBalanceData.logo,
            token_created_at = tokenBalanceData.token_created_at,
            exchange_name = tokenBalanceData.exchange_name,
            exchange_price = tokenBalanceData.exchange_price,
            last_updated = tokenBalanceData.last_updated
        )
    }

    override suspend fun deleteAccountTokenBalanceByAccount(accountName: String, blockchainUid: String) = withContext(ioDispatcher) {
        dbQuery.deleteAccountTokenBalanceByAccount(accountName, blockchainUid)
    }
}