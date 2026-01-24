package com.mangala.wallet.pin.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.pin.domain.PINManager
import com.mangala.wallet.pin.presentation.base.PinSetupCallbacks
import com.mangala.wallet.pin.presentation.base.PinUnlockCallbacks
import com.mangala.wallet.pin.presentation.confirm.ConfirmPinScreen
import com.mangala.wallet.pin.presentation.confirm.ConfirmPinScreenModel
import com.mangala.wallet.pin.presentation.forgot.ForgotPinScreenV2
import com.mangala.wallet.pin.presentation.lock.LockScreenModel
import com.mangala.wallet.pin.presentation.lock.LockScreenV2
import com.mangala.wallet.pin.presentation.setup.SetupPinScreenModel
import com.mangala.wallet.pin.presentation.setup.SetupPinScreenV2
import com.mangala.wallet.pin.presentation.unlock.UnlockPinScreenModel
import com.mangala.wallet.pin.presentation.unlock.UnlockPinScreenV2
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

fun pinKoinModule() = module {
    // Old V1 screens - keep for backward compatibility
    factory { (pinCase: SharedScreen.SetupPinScreen.SetupPinScreenCase) ->
        SetupPinScreenModel(pinCase)
    }

    factory { (pin: String, pinCase: SharedScreen.SetupPinScreen.SetupPinScreenCase) ->
        ConfirmPinScreenModel(
            pin = pin,
            pinCase = pinCase,
            callbacks = null
        )
    }

    factory { (unlockPinCase: Int) ->
        UnlockPinScreenModel(
            unlockPinCase = unlockPinCase,
            callbacks = null,
            showForgotPinOption = true
        )
    }

    // New V2 screens - callback-based
    factory { (pin: String, callbacks: PinSetupCallbacks) ->
        ConfirmPinScreenModel(
            pin = pin,
            pinCase = null,
            callbacks = callbacks,
        )
    }
    factory { (callbacks: PinUnlockCallbacks, showForgotPin: Boolean) ->
        UnlockPinScreenModel(
            unlockPinCase = null,
            callbacks = callbacks,
            showForgotPinOption = showForgotPin
        )
    }

    factory { LockScreenModel(get()) }
}

expect fun platformPinModule(): org.koin.core.module.Module

val pinModule = module {
    factory { GetIsPinSetupUseCase(get<PINManager>()) }

    includes(platformPinModule())
    includes(newPinModule)
    includes(platformNewPinModule())
}


val pinScreenModule = screenModule {

    register<SharedScreen.UnlockPinScreen> { provider ->
        UnlockPinScreenV2(provider.unlockPinCase, provider.antelopeAccountName, provider.onUnlockSuccess, provider.unlockPinCallback)
    }
    register<SharedScreen.LockScreen> {
        LockScreenV2()
    }
    register<SharedScreen.SetupPinScreen> { provider ->
        SetupPinScreenV2(
            blockchainUid = provider.blockchainUid,
            antelopeAccountName = provider.antelopeAccountName,
            listString = provider.listString,
            name = provider.name,
            onPinSetupSuccess = provider.onPinSetupSuccess,
            onPinSetupCancel = provider.onPinSetupCancel,
            pinCase = provider.pinCase
        )
    }
    register<SharedScreen.ConfirmPinScreen> { provider ->
        ConfirmPinScreen(
            pin = provider.pin,
            blockchainUid = provider.blockchainUid,
            antelopeAccountName = provider.antelopeAccountName,
            listString = provider.listString,
            name = provider.name,
            onPinSetupSuccess = provider.onPinSetupSuccess,
            pinCase = provider.pinCase
        )
    }
    register<SharedScreen.ForgotPinScreen> {
        ForgotPinScreenV2()
    }
//    register<SharedScreen.BiometryScreen> {
//        BiometryScreen()
//    }
}