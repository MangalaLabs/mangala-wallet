package com.mangala.wallet.local.securestorage

interface SecureStorageWrapper {
    fun saveValue(key: String, value: String)
    fun getValue(key: String): String?
    fun containsKey(key: String): Boolean
    fun remove(key: String)
}

expect class SecureStorageWrapperImpl: SecureStorageWrapper {
    override fun saveValue(key: String, value: String)
    override fun getValue(key: String): String?
    override fun containsKey(key: String): Boolean
    override fun remove(key: String)
}
