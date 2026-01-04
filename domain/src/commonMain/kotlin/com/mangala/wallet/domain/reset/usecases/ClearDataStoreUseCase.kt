package com.mangala.wallet.domain.reset.usecases

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.model.language.Language
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearDataStoreUseCase(
    private val dataStoreRepository: DataStoreRepository,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Reset to default values rather than clearing completely
            // This maintains app functionality while clearing user data
            
            // Reset database initialization flag
            dataStoreRepository.saveInitialDatabaseIfNeeded(false)
            
            // Reset balance visibility to default (true)
            dataStoreRepository.saveBalanceVisibleStatus(true)
            
            // Reset selected network to default
            val defaultNetwork = BlockchainNetworkData.getDefaultNetwork(
                buildEnvironmentProvider.isDevelopmentEnvironment()
            )
            dataStoreRepository.saveSelectedNetwork(defaultNetwork)
            
            // Reset currency to default
            dataStoreRepository.saveSelectedCurrencyCode(Currency.DEFAULT_CURRENCY.code)
            
            // Reset language to default
            dataStoreRepository.saveSelectedLanguageCode(Language.DEFAULT_LANGUAGE.code)
            
            // Reset onboarding completion flag
            dataStoreRepository.saveOnboardingCompleted(false)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}