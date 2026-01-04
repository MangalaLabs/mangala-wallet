@file:OptIn(ExperimentalForeignApi::class)

package com.mangala.wallet.local.securestorage

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFAutorelease
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanFalse
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSMutableData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.appendBytes
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.darwin.OSStatus

actual class SecureStorageWrapperImpl: SecureStorageWrapper {

    actual override fun containsKey(key: String): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun remove(key: String) {
        delete(key)
    }

    actual override fun saveValue(key: String, value: String) {
        set(key, value)
    }

    actual override fun getValue(key: String): String? {
        return string(key).orEmpty()
    }

    private val serviceName = "mangala_secure_storage_server"
    /**
     * Saves a string value in the Keychain.
     * @param key The key to store
     * @param stringValue The value to store
     * @return True or false, depending on whether the value has been stored in the Keychain
     */
    fun set(key: String, stringValue: String): Boolean = addOrUpdate(key, stringValue.toNSData())

    /**
     * Returns the string value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    fun string(forKey: String): String? = value(forKey)?.string()

    @OptIn(ExperimentalForeignApi::class)
    private fun existsObject(forKey: String): Boolean = context(forKey) { (account) ->
        val query = query(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to account,
            kSecReturnData to kCFBooleanFalse
        )

        SecItemCopyMatching(query, null)
            .validate()
    }

    private fun addOrUpdate(key: String, value: NSData?): Boolean {
        return if (existsObject(key)) {
            update(key, value)
        } else {
            add(key, value)
        }
    }

    private fun add(key: String, value: NSData?): Boolean = context(key, value) { (account, data) ->
        val query = query(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to account,
            kSecValueData to data
        )

        SecItemAdd(query, null)
            .validate()
    }

    private fun update(key: String, value: Any?): Boolean = context(key, value) { (account, data) ->
        val query = query(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to account,
            kSecReturnData to kCFBooleanFalse
        )

        val updateQuery = query(
            kSecValueData to data
        )

        SecItemUpdate(query, updateQuery)
            .validate()
    }

    private fun value(forKey: String): NSData? = context(forKey) { (account) ->
        val query = query(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to account,
            kSecReturnData to kCFBooleanTrue,
            kSecMatchLimit to kSecMatchLimitOne
        )

        memScoped {
            val result = alloc<CFTypeRefVar>()
            SecItemCopyMatching(query, result.ptr)
            CFBridgingRelease(result.value) as? NSData
        }
    }

    private fun delete(key: String): Boolean = context(key) { (account) ->
        val query = query(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to account
        )

        SecItemDelete(query)
            .validate()
    }

    private class Context(val refs: Map<CFStringRef?, CFTypeRef?>) {
        fun query(vararg pairs: Pair<CFStringRef?, CFTypeRef?>): CFDictionaryRef? {
            val map = mapOf(*pairs).plus(refs.filter { it.value != null })
            return CFDictionaryCreateMutable(
                null, map.size.convert(), null, null
            ).apply {
                map.entries.forEach { CFDictionaryAddValue(this, it.key, it.value) }
            }.apply {
                CFAutorelease(this)
            }
        }
    }

    private fun <T> context(vararg values: Any?, block: Context.(List<CFTypeRef?>) -> T): T {
        val standard = mapOf(
            kSecAttrService to CFBridgingRetain(serviceName)
        )
        val custom = arrayOf(*values).map { CFBridgingRetain(it) }
        return block.invoke(Context(standard), custom).apply {
            standard.values.plus(custom).forEach { CFBridgingRelease(it) }
        }
    }

    private fun OSStatus.validate(): Boolean = toUInt() == platform.darwin.noErr
}

internal fun NSData.string(): String? =
    NSString.create(data = this, encoding = NSUTF8StringEncoding)?.toString()

internal fun String.toNSData(): NSData? =
    NSString.create(string = this).dataUsingEncoding(NSUTF8StringEncoding)

//private fun String.toNSData(): NSData = this.encodeToByteArray().toNSData()

private fun ByteArray.toNSData(): NSData = NSMutableData().apply {
    this@toNSData.usePinned { pinned ->
        appendBytes(pinned.addressOf(0), (sizeOf<ByteVar>() * size).toULong())
    }
}