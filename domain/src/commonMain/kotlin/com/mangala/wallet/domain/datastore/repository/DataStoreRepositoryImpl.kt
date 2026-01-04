package com.mangala.wallet.domain.datastore.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mangala.wallet.domain.datastore.MangalaWalletDataStoreWrapper
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.model.language.Language
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DataStoreRepositoryImpl(
    private val mangalaWalletDataStoreWrapper: MangalaWalletDataStoreWrapper,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
) : DataStoreRepository {

    private val dataStore: DataStore<Preferences> = mangalaWalletDataStoreWrapper.instance

    private val initialDatabaseKey = booleanPreferencesKey("initial_database_key")
    private val balanceVisibleKey = booleanPreferencesKey("balance_visible_key")
    private val selectedNetworkKey = stringPreferencesKey("selected_network_key")
    private val selectedCurrencyCodeKey = stringPreferencesKey("selected_currency_code_key")
    private val selectedLanguageCodeKey = stringPreferencesKey("selected_language_code_key")
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed_key")

    override suspend fun getInitialDatabaseIfNeeded(): Flow<Boolean> {
        return dataStore.data.map {
            it[initialDatabaseKey] ?: false
        }.distinctUntilChanged()
    }

    override suspend fun saveInitialDatabaseIfNeeded(value: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit {
                it[initialDatabaseKey] = value
            }
        }
    }

    override suspend fun getBalanceVisibleStatus(): Flow<Boolean> {
        return dataStore.data.map {
            it[balanceVisibleKey] ?: true
        }.distinctUntilChanged()
    }

    override suspend fun saveBalanceVisibleStatus(value: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit {
                it[balanceVisibleKey] = value
            }
        }
    }

    override fun getSelectedNetworkFlow(): Flow<BlockchainNetworkData> {
        return dataStore.data.map { preferences ->
            getSelectedNetwork(preferences)
        }.distinctUntilChanged()
    }

    override suspend fun getSelectedNetwork(): BlockchainNetworkData = withContext(Dispatchers.IO) {
        val preferences = dataStore.data.first()
        return@withContext getSelectedNetwork(preferences)
    }

    private fun getSelectedNetwork(preferences: Preferences): BlockchainNetworkData {
        val isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()

        return BlockchainNetworkData.getAllBlockchainNetworkSupported(isDevelopmentEnvironment).find {
            it.name == preferences[selectedNetworkKey]
        } ?: BlockchainNetworkData.getDefaultNetwork(isDevelopmentEnvironment)
    }

    override suspend fun saveSelectedNetwork(value: BlockchainNetworkData) {
        withContext(Dispatchers.IO) {
            dataStore.edit {
                it[selectedNetworkKey] = value.name
            }
        }
    }

    override suspend fun getSelectedCurrencyCodeFlow(): Flow<String> {
        return dataStore.data.map {
            it[selectedCurrencyCodeKey] ?: Currency.DEFAULT_CURRENCY.code
        }.distinctUntilChanged()
    }

    override suspend fun getSelectedCurrencyCode(): String = withContext(Dispatchers.IO) {
        val preferences = dataStore.data.first()
        return@withContext preferences[selectedCurrencyCodeKey] ?: Currency.DEFAULT_CURRENCY.code
    }

    override suspend fun saveSelectedCurrencyCode(value: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit {
                it[selectedCurrencyCodeKey] = value
            }
        }
    }

    override suspend fun getSelectedLanguageCodeFlow(): Flow<String> {
        return dataStore.data.map {
            it[selectedLanguageCodeKey] ?: Language.DEFAULT_LANGUAGE.code
        }.distinctUntilChanged()
    }

    override suspend fun saveSelectedLanguageCode(value: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit {
                it[selectedLanguageCodeKey] = value
            }
        }
    }

    override fun getOnboardingCompletedFlow(): Flow<Boolean> {
        return dataStore.data.map {
            it[onboardingCompletedKey] ?: false
        }.distinctUntilChanged()
    }

    override suspend fun getOnboardingCompleted(): Boolean = withContext(Dispatchers.IO) {
        val preferences = dataStore.data.first()
        return@withContext preferences[onboardingCompletedKey] ?: false
    }

    override suspend fun saveOnboardingCompleted(completed: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit {
                it[onboardingCompletedKey] = completed
            }
        }
    }
}