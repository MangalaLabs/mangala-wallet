package com.mangala.wallet.pin.presentation.base

sealed class PinScreenFlow {
    data object ShowCurrentScreen : PinScreenFlow()
    data object ShowSetUpPinScreen : PinScreenFlow()
    data object ShowConfirmPinScreen : PinScreenFlow()
    data object ShowUnlockPinScreen : PinScreenFlow()
    data object ShowForgotPinScreen : PinScreenFlow()
    data object ShowHomeScreen : PinScreenFlow()
    data object ShowRecoveryPhraseScreen : PinScreenFlow()
    data object BackupAntelopeAccountScreen : PinScreenFlow()
    data object ShowCreateWalletScreen : PinScreenFlow()
    data object ShowLockScreen : PinScreenFlow()
    data object ShowAddAccountScreen : PinScreenFlow()
    data object ShowBitcoinAddAccountScreen : PinScreenFlow()
    data object ConfirmDappScreen : PinScreenFlow()
    data object ShowEnableBiometryScreen : PinScreenFlow()
    data object ShowVerifyAndSendScreen : PinScreenFlow()
    data object ShowBackLastScreen : PinScreenFlow()
    data object ShowPopFromSetupPinScreen : PinScreenFlow()
    data object ShowSetUpPinAndContinueScreen : PinScreenFlow()
}

