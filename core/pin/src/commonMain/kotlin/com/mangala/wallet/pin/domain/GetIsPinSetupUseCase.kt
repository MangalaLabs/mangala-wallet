package com.mangala.wallet.pin.domain

class GetIsPinSetupUseCase(
    private val pinManager: PINManager
) {
    operator fun invoke(): Boolean {
        return pinManager.isPINSetup()
    }
}
