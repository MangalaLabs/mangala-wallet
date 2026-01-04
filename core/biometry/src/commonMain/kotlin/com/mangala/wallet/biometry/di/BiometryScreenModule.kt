package com.mangala.wallet.biometry.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.biometry.presentation.BiometryScreen
import com.mangala.wallet.ui.SharedScreen

val biometryScreenModule = screenModule {
    register<SharedScreen.BiometryScreen> {
        BiometryScreen(
            it.blockchainUid,
            it.antelopeAccountName,
            it.pinCase,
            it.listString,
            it.name,
            it.onBiometryCallback,
            it.onCancel
        )
    }
}