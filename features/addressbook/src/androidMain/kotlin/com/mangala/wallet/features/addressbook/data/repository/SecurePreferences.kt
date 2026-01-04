//package com.mangala.wallet.features.addressbook.data.repository
//
//import android.content.Context
//import android.content.SharedPreferences
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKeys
//
///**
// * Lớp wrapper cho SharedPreferences mã hóa.
// * Sử dụng EncryptedSharedPreferences từ AndroidX Security
// */
//class SecurePreferences(context: Context) {
//
//    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//
//    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
//        "secure_preferences",
//        masterKeyAlias,
//        context,
//        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//    )
//
//    fun getString(key: String, defaultValue: String?): String? {
//        return sharedPreferences.getString(key, defaultValue)
//    }
//
//    fun putString(key: String, value: String): Boolean {
//        return sharedPreferences.edit().putString(key, value).commit()
//    }
//
//    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
//        return sharedPreferences.getBoolean(key, defaultValue)
//    }
//
//    fun putBoolean(key: String, value: Boolean): Boolean {
//        return sharedPreferences.edit().putBoolean(key, value).commit()
//    }
//
//    fun remove(key: String): Boolean {
//        return sharedPreferences.edit().remove(key).commit()
//    }
//
//    fun contains(key: String): Boolean {
//        return sharedPreferences.contains(key)
//    }
//
//    fun clear(): Boolean {
//        return sharedPreferences.edit().clear().commit()
//    }
//}