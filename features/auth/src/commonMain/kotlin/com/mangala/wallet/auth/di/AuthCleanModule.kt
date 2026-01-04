package com.mangala.wallet.auth.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.auth.AuthenticationFlowManager
import com.mangala.wallet.auth.AuthenticationFlowManagerClean
import com.mangala.wallet.auth.SessionManagerImpl
import com.mangala.wallet.auth.domain.ClearPasskeyAndSessionUseCaseImpl
import com.mangala.wallet.auth.navigation.AuthNavigationHandler
import com.mangala.wallet.auth.navigation.DefaultAuthNavigationHandler
import com.mangala.wallet.auth.presentation.AuthDemoScreen
import com.mangala.wallet.auth.presentation.AuthDemoScreenModel
import com.mangala.wallet.auth.presentation.RegisterScreenModel
import com.mangala.wallet.auth.presentation.signin.SignInScreen
import com.mangala.wallet.auth.presentation.signin.SignInScreenModel
import com.mangala.wallet.auth.repository.AuthRepository
import com.mangala.wallet.auth.repository.AuthRepositoryImpl
import com.mangala.wallet.auth.storage.CompletedPasskeyStorage
import com.mangala.wallet.core.auth.SessionManager
import com.mangala.wallet.domain.reset.usecases.ClearPasskeyAndSessionUseCase
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Clean architecture Koin module for auth feature
 */
val authCleanModule = module {
    // Repository
    single<AuthRepository> { AuthRepositoryImpl() }

    factoryOf(::ClearPasskeyAndSessionUseCaseImpl) bind ClearPasskeyAndSessionUseCase::class

    // Session Manager
    single<SessionManager> { SessionManagerImpl(get<SecureStorageWrapper>()) }

    // Completed Passkey Storage
    single { CompletedPasskeyStorage(get<SecureStorageWrapper>()) }
    
    // Authentication Flow Manager with clean architecture
    single {
        AuthenticationFlowManagerClean(
            registerPasskeyUseCase = get(),
            authenticateWithPasskeyUseCase = get(),
            checkPasskeySupportUseCase = get(),
            biometryAuthenticator = get(),
            authRepository = get(),
            sessionManager = get()
        )
    }
    
    // Provide the original AuthenticationFlowManager for compatibility with existing screens
    single {
        AuthenticationFlowManager(
            passkeyManager = get(),
            passkeyRepository = get(),
            biometryAuthenticator = get(),
            authRepository = get(),
            sessionManager = get(),
            completedPasskeyStorage = get()
        )
    }
    
    // Navigation handler - can be overridden by app module
    single<AuthNavigationHandler> { DefaultAuthNavigationHandler() }
    
    // Screen models
    factory {
        AuthDemoScreenModel(
            authFlowManager = get(),
            sessionManager = get(),
            authRepository = get(),
            navigationHandler = get()
        )
    }
    
    factory {
        SignInScreenModel(
            authFlowManager = get(),
            navigationHandler = get(),
            completeOnboardingUseCase = get(),
            createPortfolioUseCase = get()
        )
    }
    
    factory {
        RegisterScreenModel(
            authFlowManager = get(),
            navigationHandler = get(),
            completeOnboardingUseCase = get()
        )
    }
}

val authScreenModule = screenModule {
    register<SharedScreen.SignInScreen> { screen ->
        SignInScreen(showTokenExpiredMessage = screen.showTokenExpiredMessage)
    }
    register<SharedScreen.AuthDemoScreen> { AuthDemoScreen() }
}