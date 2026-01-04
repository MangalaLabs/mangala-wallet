package com.mangala.wallet.local.securestorage

import com.microsoft.credentialstorage.StorageProvider
import com.microsoft.credentialstorage.model.StoredToken
import com.microsoft.credentialstorage.model.StoredTokenType

actual class SecureStorageWrapperImpl : SecureStorageWrapper {
    private val credentialStorage = StorageProvider.getTokenStorage(true, StorageProvider.SecureOption.REQUIRED)

    actual override fun saveValue(key: String, value: String) {
        credentialStorage.add(key, StoredToken(value.toCharArray(), StoredTokenType.PERSONAL))
    }

    actual override fun getValue(key: String): String? {
        val storedToken: StoredToken? = credentialStorage.get(key)
        return storedToken?.value?.let { String(it) }
    }

    actual override fun containsKey(key: String): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun remove(key: String) {
        credentialStorage.delete(key)
    }
}
