package com.mangala.wallet.domain.datastore.repository

import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun getInitialDatabaseIfNeeded(): Flow<Boolean>
    suspend fun saveInitialDatabaseIfNeeded(value: Boolean)
    suspend fun getBalanceVisibleStatus(): Flow<Boolean>
    suspend fun saveBalanceVisibleStatus(value: Boolean)
    fun getSelectedNetworkFlow(): Flow<BlockchainNetworkData>
    suspend fun getSelectedNetwork(): BlockchainNetworkData
    suspend fun saveSelectedNetwork(value: BlockchainNetworkData)
    suspend fun getSelectedCurrencyCodeFlow(): Flow<String>
    suspend fun getSelectedCurrencyCode(): String
    suspend fun saveSelectedCurrencyCode(value: String)
    suspend fun getSelectedLanguageCodeFlow(): Flow<String>
    suspend fun saveSelectedLanguageCode(value: String)
    fun getOnboardingCompletedFlow(): Flow<Boolean>
    suspend fun getOnboardingCompleted(): Boolean
    suspend fun saveOnboardingCompleted(completed: Boolean)
    fun getPrePermissionDoneFlow(): Flow<Boolean>
    suspend fun getPrePermissionDone(): Boolean
    suspend fun savePrePermissionDone(done: Boolean)
}
