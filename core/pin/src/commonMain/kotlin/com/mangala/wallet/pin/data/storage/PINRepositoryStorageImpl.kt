package com.mangala.wallet.pin.data.storage

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.pin.domain.repository.PINRepositoryStorage
import com.mangala.wallet.pin.domain.repository.StoredPIN
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Implementation of PINRepositoryStorage using SecureStorageWrapper
 */
class PINRepositoryStorageImpl(
    private val secureStorage: SecureStorageWrapper,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : PINRepositoryStorage {

    companion object {
        private const val KEY_PIN_DATA = "pin_stored_data_v2"
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun save(pin: StoredPIN) {
        val serializable = StoredPINSerializable(
            hash = Base64.encode(pin.hash),
            salt = Base64.encode(pin.salt),
            iterations = pin.iterations
        )
        val jsonString = json.encodeToString(serializable)
        secureStorage.saveValue(KEY_PIN_DATA, jsonString)
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun load(): StoredPIN? {
        val jsonString = secureStorage.getValue(KEY_PIN_DATA) ?: return null

        return try {
            val serializable = json.decodeFromString<StoredPINSerializable>(jsonString)
            StoredPIN(
                hash = Base64.decode(serializable.hash),
                salt = Base64.decode(serializable.salt),
                iterations = serializable.iterations
            )
        } catch (e: Exception) {
            null
        }
    }

    override fun clear() {
        secureStorage.remove(KEY_PIN_DATA)
    }

    @Serializable
    private data class StoredPINSerializable(
        val hash: String,
        val salt: String,
        val iterations: Int
    )
}
