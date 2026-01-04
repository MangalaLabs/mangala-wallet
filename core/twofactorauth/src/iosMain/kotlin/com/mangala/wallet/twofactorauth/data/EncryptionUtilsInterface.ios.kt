package com.mangala.wallet.twofactorauth.data

private object PlatformEncryptionUtilsImpl : EncryptionUtilsInterface {
    override suspend fun generateSecureRandomBytes(length: Int): ByteArray {
        TODO("Not yet implemented")
    }

    override suspend fun encryptWithAesGcm(
        data: ByteArray,
        key: ByteArray,
        iv: ByteArray?
    ): ByteArray {
        TODO("Not yet implemented")
    }

    override suspend fun decryptWithAesGcm(encryptedData: ByteArray, key: ByteArray): ByteArray {
        TODO("Not yet implemented")
    }

    override suspend fun deriveKeyFromPassword(
        password: String,
        salt: ByteArray,
        iterations: Int
    ): ByteArray {
        TODO("Not yet implemented")
    }

    override suspend fun hmacSha1(key: ByteArray, data: ByteArray): ByteArray {
        TODO("Not yet implemented")
    }
}

actual val PlatformEncryptionUtils: EncryptionUtilsInterface = PlatformEncryptionUtilsImpl