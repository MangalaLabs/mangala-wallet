package com.mangala.wallet.features.addressbook.di

import com.mangala.wallet.features.addressbook.domain.usecase.facade.CommunicationUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.facade.ContactCoreUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.facade.WalletUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * DI module for refactored ContactEdit feature using facade pattern
 * Replaces complex dependency injection with simple facade-based approach
 */
val contactEditRefactoredModule = module {
    
    // Facade use cases - these encapsulate multiple domain use cases
    singleOf(::ContactCoreUseCase)
    singleOf(::CommunicationUseCase) 
    singleOf(::WalletUseCase)
}