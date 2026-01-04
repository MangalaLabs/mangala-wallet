package com.mangala.wallet.features.addressbook.domain.usecase.pin

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperConstants.PIN_KEY

/**
 * Use case to check if PIN has been created/exists
 * This is used to validate security level selection in contacts
 */
class CheckPinExistsUseCase(
    private val secureStorageWrapper: SecureStorageWrapper
) {
    operator fun invoke(): Boolean {
        val pin = secureStorageWrapper.getValue(PIN_KEY)
        return !pin.isNullOrEmpty()
    }
}