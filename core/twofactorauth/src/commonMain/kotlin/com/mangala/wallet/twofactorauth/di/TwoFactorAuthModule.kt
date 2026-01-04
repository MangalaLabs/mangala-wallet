package com.mangala.wallet.twofactorauth.di

import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.twofactorauth.data.BackupManager
import com.mangala.wallet.twofactorauth.data.RateLimiter
import com.mangala.wallet.twofactorauth.data.local.TotpGeneratorDataSource
import com.mangala.wallet.twofactorauth.data.local.TotpGeneratorDataSourceImpl
import com.mangala.wallet.twofactorauth.data.repository.TwoFactorRepositoryImpl
import com.mangala.wallet.twofactorauth.domain.repository.TwoFactorRepository
import com.mangala.wallet.twofactorauth.domain.usecase.AuthenticateTransactionUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.Disable2FAUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.ExportBackupUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.GetAuthStateUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.ImportBackupUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.Is2FAEnabledUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.Setup2FAUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.VerifyBackupCodeUseCase
import com.mangala.wallet.twofactorauth.domain.usecase.VerifyCodeUseCase
import com.mangala.wallet.twofactorauth.presentation.backuporrestore.BackupAndRestoreScreenModel
import com.mangala.wallet.twofactorauth.presentation.setting.Setting2FaScreen
import com.mangala.wallet.twofactorauth.presentation.setting.Setting2FaScreenModel
import com.mangala.wallet.twofactorauth.presentation.setup.TwoFactorSetupRequiredScreen
import com.mangala.wallet.twofactorauth.presentation.setup.TwoFactorSetupRequiredScreenModel
import com.mangala.wallet.twofactorauth.presentation.setup.TwoFactorAuthenticationSetupScreen
import com.mangala.wallet.twofactorauth.presentation.setup.TwoFactorAuthenticationSetupScreenModel
import com.mangala.wallet.twofactorauth.presentation.unlock.Unlock2FaScreen
import com.mangala.wallet.twofactorauth.presentation.unlock.Unlock2FaScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val twoFactorAuthModule = module {
    // Utilities
    single<TotpGeneratorDataSource> { TotpGeneratorDataSourceImpl() }
    single { BackupManager() }
    single { RateLimiter(get()) }

    // Repository
    factory<TwoFactorRepository> {
        TwoFactorRepositoryImpl(
            secureStorage = get(),
            totpGenerator = get(),
            backupManager = get(),
            rateLimiter = get()
        )
    }

    // Use Cases
    factory { Setup2FAUseCase(get()) }
    factory { VerifyCodeUseCase(get()) }
    factory { Disable2FAUseCase(get()) }
    factory { AuthenticateTransactionUseCase(get()) }
    factory { Is2FAEnabledUseCase(get()) }
    factory { GetAuthStateUseCase(get()) }
    factory { ExportBackupUseCase(get()) }
    factory { ImportBackupUseCase(get()) }
    factory { VerifyBackupCodeUseCase(get()) }

    factory {
        TwoFactorAuthenticationSetupScreenModel(
            setup2FAUseCase = get(),
            is2FAEnabledUseCase = get(),
            verifyCodeUseCase = get(),
            getAuthStateUseCase = get()
        )
    }

    factory { (onUnlockSuccess: () -> Unit) ->
        Unlock2FaScreenModel(
            onUnlockSuccess = onUnlockSuccess,
            verifyCodeUseCase = get(),
            getAuthStateUseCase = get()
        )
    }

    factory {
        Setting2FaScreenModel(
            is2FAEnabledUseCase = get(),
            disable2FAUseCase = get(),
            exportBackupUseCase = get(),
            importBackupUseCase = get()
        )
    }

    factory {
        BackupAndRestoreScreenModel(
            exportBackupUseCase = get(),
            is2FAEnabledUseCase = get(),
            verifyBackupCodeUseCase = get()
        )
    }

    factory { (onCancel: () -> Unit, onFallbackToPin: () -> Unit) ->
        TwoFactorSetupRequiredScreenModel(
            onCancel = onCancel,
            onFallbackToPin = onFallbackToPin
        )
    }
}

val twoFactorAuthScreenModule = screenModule {
    register<SharedScreen.TwoFactorAuthenticationSetupScreen> {
        TwoFactorAuthenticationSetupScreen(it.onSuccess)
    }

    register<SharedScreen.Unlock2FaScreen> {
        Unlock2FaScreen(
            onUnlockSuccess = it.onUnlockSuccess,
            onUnlockCancelled = it.onUnlockCancelled
        )
    }

    register<SharedScreen.Setting2FaScreen> {
        Setting2FaScreen()
    }

    register<SharedScreen.TwoFactorSetupRequiredScreen> {
        TwoFactorSetupRequiredScreen(
            onSetup2Fa = it.onSetup2Fa,
            onCancel = it.onCancel,
            onFallbackToPin = it.onFallbackToPin
        )
    }
}
