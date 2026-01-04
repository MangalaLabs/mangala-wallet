package com.mangala.wallet.pin.data

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

// Implementation of PINStorage for Android
actual class PINStorage(private val context: Context) : IPINStorage {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore")
    private val keyAlias = "mangala_wallet_pin_key"

    init {
        keyStore.load(null)
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

            val builder = KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                builder.setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG)
            } else {
                builder.setUserAuthenticationValidityDurationSeconds(600) // TODO: Require user to reauthenticate in case timeout
            }

            keyGenerator.init(builder.build())
            keyGenerator.generateKey()
        }
    }

    actual override fun storePIN(pin: PIN) {
        val cipher = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val encrypted = cipher.doFinal(pin.toString().toByteArray(Charsets.UTF_8))
        val preferences = context.getSharedPreferences("pin_storage", Context.MODE_PRIVATE)
        preferences.edit().putString("pin", Base64.getEncoder().encodeToString(encrypted)).apply()
    }

    actual override fun retrievePIN(): PIN? {
        val preferences = context.getSharedPreferences("pin_storage", Context.MODE_PRIVATE)
        val encrypted = preferences.getString("pin", null)?.let {
            Base64.getDecoder().decode(it)
        }
        if (encrypted != null) {
            val cipher = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")
            cipher.init(Cipher.DECRYPT_MODE, getKey(), cipher.parameters)
            val decrypted = cipher.doFinal(encrypted)
            return PIN(String(decrypted, Charsets.UTF_8))
        }
        return null
    }

    private fun getKey(): SecretKey {
        return (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
    }
}