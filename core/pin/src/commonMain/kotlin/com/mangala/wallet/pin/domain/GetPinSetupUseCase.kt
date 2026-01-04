package com.mangala.wallet.pin.domain

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperConstants

class GetIsPinSetupUseCase(private val secureStorageWrapper: SecureStorageWrapper) {
    operator fun invoke(): Boolean {
        val state = secureStorageWrapper.getValue(SecureStorageWrapperConstants.PIN_KEY) ?: ""

        return state.isBlank().not()
    }
}