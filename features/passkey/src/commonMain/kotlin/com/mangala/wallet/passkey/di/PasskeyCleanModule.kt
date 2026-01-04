package com.mangala.wallet.passkey.di

import com.mangala.wallet.passkey.PasskeyManager
import com.mangala.wallet.passkey.PasskeyManagerFactory
import com.mangala.wallet.passkey.data.config.PasskeyConfig
import com.mangala.wallet.passkey.data.repository.PasskeyDomainRepositoryImpl
import com.mangala.wallet.passkey.domain.model.PasskeyConfiguration
import com.mangala.wallet.passkey.domain.repository.PasskeyDomainRepository
import com.mangala.wallet.passkey.domain.usecase.*
import com.mangala.wallet.passkey.repository.PasskeyRepository
import com.mangala.wallet.passkey.repository.PasskeyRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Clean architecture Koin module for passkey feature
 */
fun passkeyCleanModule(
    baseUrl: String = PasskeyConfig.getBaseUrl(),
    useMockRepository: Boolean = false
): Module = module {
    
    // Configuration
    single<PasskeyConfiguration> {
        PasskeyConfiguration(
            rpName = PasskeyConfig.DEFAULT_RP_NAME,
            rpId = PasskeyConfig.getRpId(baseUrl),
            baseUrl = baseUrl,
            timeout = PasskeyConfig.DEFAULT_TIMEOUT
        )
    }
    
    // Data layer - Platform specific
    single<PasskeyManager> { PasskeyManagerFactory.create() }
    
    // Data layer - Repository
    single<PasskeyRepository> { 
        if (useMockRepository) {
            // For testing
            com.mangala.wallet.passkey.repository.MockPasskeyRepository()
        } else {
            PasskeyRepositoryImpl(
                httpClient = get(),
                baseUrl = baseUrl
            )
        }
    }
    
    // Domain layer - Repository
    single<PasskeyDomainRepository> {
        PasskeyDomainRepositoryImpl(
            passkeyManager = get(),
            passkeyRepository = get(),
            config = get()
        )
    }
    
    // Domain layer - Use cases
    factory { RegisterPasskeyUseCase(get()) }
    factory { AuthenticateWithPasskeyUseCase(get()) }
    factory { CheckPasskeySupportUseCase(get()) }
    factory { DeleteCredentialUseCase(get()) }
}

expect fun platformPasskeyModule(): Module