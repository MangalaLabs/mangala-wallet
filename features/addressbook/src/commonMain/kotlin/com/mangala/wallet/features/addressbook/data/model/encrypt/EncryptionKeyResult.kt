package com.mangala.wallet.features.addressbook.data.model.encrypt


sealed class EncryptionKeyResult {
    data class Success(val key: EncryptionKey) : EncryptionKeyResult()
    data class Error(val exception: Exception) : EncryptionKeyResult()

    // Tiện ích hỗ trợ
    inline fun onSuccess(block: (EncryptionKey) -> Unit): EncryptionKeyResult {
        if (this is Success) block(key)
        return this
    }

    inline fun onError(block: (Exception) -> Unit): EncryptionKeyResult {
        if (this is Error) block(exception)
        return this
    }

    fun getOrNull(): EncryptionKey? = when (this) {
        is Success -> key
        is Error -> null
    }

    fun getOrThrow(): EncryptionKey = when (this) {
        is Success -> key
        is Error -> throw exception
    }
}