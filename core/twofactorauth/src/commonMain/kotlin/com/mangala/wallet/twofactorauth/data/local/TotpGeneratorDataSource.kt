package com.mangala.wallet.twofactorauth.data.local

import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis

interface TotpGeneratorDataSource {
    /**
     * Generate a TOTP code from a secret at the specified time
     */
    suspend fun generateTOTP(
        secret: ByteArray,
        timeMillis: Long = localDateTimeToMillis(localDateTimeNow())
    ): String

    /**
     * Validate a provided TOTP code against a secret
     */
    suspend fun validateTOTP(
        secret: ByteArray,
        inputCode: String,
        timeMillis: Long = localDateTimeToMillis(localDateTimeNow())
    ): Boolean
}
