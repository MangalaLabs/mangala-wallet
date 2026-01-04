package com.mangala.wallet.domain.reset.usecases

import com.mangala.wallet.domain.reset.constants.SecureStorageKeys
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearSecureStorageUseCase(
    private val secureStorageWrapper: SecureStorageWrapper
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Clear PIN
            secureStorageWrapper.remove(SecureStorageWrapperConstants.PIN_KEY)
            
            // Clear PIN unlock state
            secureStorageWrapper.remove(SecureStorageKeys.UNLOCK_PIN_STATE)
            secureStorageWrapper.remove(SecureStorageKeys.INCORRECT_ATTEMPTS_PIN)
            
            // Clear biometric settings
            secureStorageWrapper.remove(SecureStorageKeys.ENABLE_BIOMETRIC_ANDROID)
            secureStorageWrapper.remove(SecureStorageKeys.ENABLE_BIOMETRIC_IOS)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}