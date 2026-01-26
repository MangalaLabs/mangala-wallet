package com.mangala.wallet.pin.di

import com.mangala.wallet.pin.data.storage.AttemptStorageImpl
import com.mangala.wallet.pin.data.storage.PINRepositoryStorageImpl
import com.mangala.wallet.pin.data.storage.RateLimitStorageImpl
import com.mangala.wallet.pin.domain.PINManager
import com.mangala.wallet.pin.domain.attempt.AttemptTracker
import com.mangala.wallet.pin.domain.ratelimit.RateLimiter
import com.mangala.wallet.pin.domain.repository.PINRepository
import kotlinx.coroutines.runBlocking
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Common DI module for new PIN system
 */
val newPinModule = module {
    // Storage implementations
    single {
        PINRepositoryStorageImpl(
            secureStorage = get()
        )
    }

    single {
        RateLimitStorageImpl(
            secureStorage = get()
        )
    }

    single {
        AttemptStorageImpl(
            secureStorage = get()
        )
    }

    // Domain layer
    single {
        PINRepository(
            storage = get<PINRepositoryStorageImpl>()
        )
    }

    single {
        val deviceIdProvider = get<com.mangala.wallet.pin.data.DeviceIdProvider>()
        val deviceId = runBlocking { deviceIdProvider.getDeviceId() }

        AttemptTracker(
            storage = get<AttemptStorageImpl>(),
            deviceId = deviceId
        )
    }

    single {
        RateLimiter(
            storage = get<RateLimitStorageImpl>()
        )
    }

    single {
        PINManager(
            repository = get(),
            attemptTracker = get(),
            rateLimiter = get()
        )
    }
}

/**
 * Platform-specific DI module
 */
expect fun platformNewPinModule(): Module
