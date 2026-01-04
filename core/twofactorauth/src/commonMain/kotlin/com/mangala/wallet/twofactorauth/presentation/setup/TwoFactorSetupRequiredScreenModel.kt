package com.mangala.wallet.twofactorauth.presentation.setup

import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel

class TwoFactorSetupRequiredScreenModel(
    private val onCancel: () -> Unit,
    private val onFallbackToPin: () -> Unit
) : BaseScreenModel() {

    fun onContinueWithPINClicked() {
        onFallbackToPin()
    }

    fun onCancelClicked() {
        onCancel()
    }
}