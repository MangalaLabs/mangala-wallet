package com.mangala.wallet.passkey.di

import com.mangala.wallet.passkey.PasskeyManager
import com.mangala.wallet.passkey.PasskeyManagerFactory
import com.mangala.wallet.passkey.repository.MockPasskeyRepository
import com.mangala.wallet.passkey.repository.PasskeyRepository
import com.mangala.wallet.passkey.repository.PasskeyRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

fun passkeyModule(baseUrl: String, useMockRepository: Boolean = false): Module = module {
    single<PasskeyRepository> {
        if (useMockRepository) {
            MockPasskeyRepository()
        } else {
            PasskeyRepositoryImpl(baseUrl = baseUrl)
        }
    }
    
    single<PasskeyManager> {
        PasskeyManagerFactory.create()
    }
}