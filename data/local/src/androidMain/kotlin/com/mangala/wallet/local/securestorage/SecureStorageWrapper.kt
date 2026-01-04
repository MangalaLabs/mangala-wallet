package com.mangala.wallet.local.securestorage

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.StrongBoxUnavailableException
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

actual class SecureStorageWrapperImpl(private val context: Context): SecureStorageWrapper {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore")

    init {
        keyStore.load(null)
    }

    actual override fun saveValue(key: String, value: String) {
        generateKeyStoreKeyIfNotExist(key)

        val cipher = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")
        cipher.init(Cipher.ENCRYPT_MODE, getKeystoreKey(key))
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val iv = cipher.iv
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit()
            .putString(key, Base64.getEncoder().encodeToString(encrypted))
            .putString("${key}_iv", Base64.getEncoder().encodeToString(iv))
            .commit()
    }

    actual override fun getValue(key: String): String? {
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val encrypted = preferences.getString(key, null)?.let {
            Base64.getDecoder().decode(it)
        }
        val iv = preferences.getString("${key}_iv", null)?.let {
            Base64.getDecoder().decode(it)
        }

        if (encrypted != null && iv != null) {
            val ivSpec = IvParameterSpec(iv)
            val cipher = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")
            cipher.init(Cipher.DECRYPT_MODE, getKeystoreKey(key), ivSpec)
            val decrypted = cipher.doFinal(encrypted)
            return String(decrypted, Charsets.UTF_8)
        }
        return null
    }

    actual override fun containsKey(key: String): Boolean {
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.contains(key)
    }

    actual override fun remove(key: String) {
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        // Remove the encrypted value and initialization vector
        preferences.edit()
            .remove(key)
            .remove("${key}_iv")
            .commit()

        // Remove the key from the KeyStore if it exists
        if (keyStore.containsAlias(key)) {
            keyStore.deleteEntry(key)
        }
    }

    private fun generateKeyStoreKeyIfNotExist(key: String) {
        if (!keyStore.containsAlias(key)) {
            try {
                generateKeyStoreKey(key)
            } catch (e: Exception) {
                if (Build.VERSION.SDK_INT >= STRONGBOX_ENABLED_API_LEVEL && e is StrongBoxUnavailableException) {
                    generateKeyStoreKey(key, strongBoxEnabled = false)
                } else throw e
            }
        }
    }

    private fun generateKeyStoreKey(key: String, strongBoxEnabled: Boolean = true) {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

        val builder = KeyGenParameterSpec.Builder(
            key,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
//            .setUserAuthenticationRequired(true) // TODO: Implement logic to check user authentication

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            builder.setIsStrongBoxBacked(strongBoxEnabled)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setUserAuthenticationParameters(
                AUTH_DURATION_SECS,
                KeyProperties.AUTH_BIOMETRIC_STRONG
            )
        } else {
            @Suppress("DEPRECATION")
            builder.setUserAuthenticationValidityDurationSeconds(AUTH_DURATION_SECS)
        }

        keyGenerator.init(builder.build())
        keyGenerator.generateKey()
    }

    private fun getKeystoreKey(key: String): SecretKey {
        return (keyStore.getEntry(key, null) as KeyStore.SecretKeyEntry).secretKey
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "secure_storage"
        private const val STRONGBOX_ENABLED_API_LEVEL = Build.VERSION_CODES.P
        private const val AUTH_DURATION_SECS = 86400 // 24 hours
    }
}