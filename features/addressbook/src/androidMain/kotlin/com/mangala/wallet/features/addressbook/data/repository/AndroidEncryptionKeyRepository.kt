//package com.mangala.wallet.features.addressbook.data.repository
//
//import android.content.Context
//import android.os.Build
//import android.security.keystore.KeyGenParameterSpec
//import android.security.keystore.KeyProperties
//import androidx.annotation.RequiresApi
//import com.mangala.wallet.features.addressbook.data.model.encrypt.EncryptionKey
//import com.mangala.wallet.features.addressbook.data.model.encrypt.EncryptionKeyResult
//import com.mangala.wallet.features.addressbook.data.model.enum.CryptoConstants
//import com.mangala.wallet.features.addressbook.domain.repository.encrypt.EncryptionKeyRepository
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import java.security.KeyStore
//import java.util.concurrent.atomic.AtomicLong
//import javax.crypto.KeyGenerator
//import javax.crypto.SecretKey
//import javax.crypto.spec.SecretKeySpec
//
///**
// * Triển khai Android-specific của EncryptionKeyRepository.
// * Sử dụng Android KeyStore để lưu trữ và quản lý khóa.
// */
//class AndroidEncryptionKeyRepository(
//    private val context: Context,
//    private val securePreferences: SecurePreferences
//) : EncryptionKeyRepository {
//
//    companion object {
//        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
//        private const val FALLBACK_KEY_PREF = "encryption_key_fallback"
//
//        // Cache khóa để tối ưu hiệu suất
//        private var cachedKey: EncryptionKey? = null
//        private val cacheTimestamp = AtomicLong(0)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun generateKey(keyAlias: String, userAuthRequired: Boolean): Flow<EncryptionKeyResult> = flow {
//        try {
//            val keyGenerator = KeyGenerator.getInstance(
//                KeyProperties.KEY_ALGORITHM_AES,
//                ANDROID_KEYSTORE
//            )
//
//            // Cấu hình khóa
//            val builder = KeyGenParameterSpec.Builder(
//                keyAlias,
//                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
//            )
//                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
//                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
//                .setKeySize(CryptoConstants.KEY_SIZE)
//                .setUserAuthenticationRequired(userAuthRequired)
//
//            // Android P trở lên, lưu khóa trong StrongBox nếu có
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                builder.setIsStrongBoxBacked(true)
//            }
//
//            keyGenerator.init(builder.build())
//            val secretKey = keyGenerator.generateKey()
//
//            // Tạo đối tượng EncryptionKey từ SecretKey
//            val encryptionKey = createEncryptionKeyFromSecretKey(secretKey)
//
//            // Cache khóa
//            updateKeyCache(encryptionKey)
//
//            emit(EncryptionKeyResult.Success(encryptionKey))
//        } catch (e: Exception) {
//            // Nếu Android KeyStore không khả dụng (thiết bị cũ), fallback về SharedPreferences
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                val fallbackKey = generateFallbackKey(keyAlias)
//                if (fallbackKey != null) {
//                    emit(EncryptionKeyResult.Success(fallbackKey))
//                } else {
//                    emit(EncryptionKeyResult.Error(Exception(CryptoConstants.ERROR_KEY_GENERATION, e)))
//                }
//            } else {
//                emit(EncryptionKeyResult.Error(Exception(CryptoConstants.ERROR_KEY_GENERATION, e)))
//            }
//        }
//    }
//
//    override fun getKey(keyAlias: String): Flow<EncryptionKeyResult> = flow {
//        // Kiểm tra cache trước
//        cachedKey?.let { key ->
//            if (!key.isExpired(CryptoConstants.KEY_CACHE_DURATION_MS)) {
//                emit(EncryptionKeyResult.Success(key))
//                return@flow
//            }
//        }
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                // Android M trở lên, lấy từ KeyStore
//                val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
//                keyStore.load(null)
//
//                if (keyStore.containsAlias(keyAlias)) {
//                    val entry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
//                    val secretKey = entry?.secretKey
//
//                    if (secretKey != null) {
//                        val encryptionKey = createEncryptionKeyFromSecretKey(secretKey)
//                        updateKeyCache(encryptionKey)
//                        emit(EncryptionKeyResult.Success(encryptionKey))
//                    } else {
//                        emit(EncryptionKeyResult.Error(Exception(CryptoConstants.ERROR_KEY_NOT_FOUND)))
//                    }
//                } else {
//                    emit(EncryptionKeyResult.Error(Exception(CryptoConstants.ERROR_KEY_NOT_FOUND)))
//                }
//            } else {
//                // Android dưới M, lấy từ SharedPreferences
//                val fallbackKey = getFallbackKey(keyAlias)
//                if (fallbackKey != null) {
//                    emit(EncryptionKeyResult.Success(fallbackKey))
//                } else {
//                    emit(EncryptionKeyResult.Error(Exception(CryptoConstants.ERROR_KEY_NOT_FOUND)))
//                }
//            }
//        } catch (e: Exception) {
//            emit(EncryptionKeyResult.Error(Exception("Failed to retrieve key: $keyAlias", e)))
//        }
//    }
//
//    override fun hasKey(keyAlias: String): Flow<Boolean> = flow {
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
//                keyStore.load(null)
//                emit(keyStore.containsAlias(keyAlias))
//            } else {
//                // Kiểm tra trong SharedPreferences
//                emit(securePreferences.contains("$FALLBACK_KEY_PREF.$keyAlias"))
//            }
//        } catch (e: Exception) {
//            emit(false)
//        }
//    }
//
//    override fun deleteKey(keyAlias: String): Flow<Boolean> = flow {
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
//                keyStore.load(null)
//
//                if (keyStore.containsAlias(keyAlias)) {
//                    keyStore.deleteEntry(keyAlias)
//                    // Xóa khỏi cache
//                    if (cachedKey != null) {
//                        cachedKey = null
//                        cacheTimestamp.set(0)
//                    }
//                    emit(true)
//                } else {
//                    emit(false)
//                }
//            } else {
//                // Xóa từ SharedPreferences
//                val removed = securePreferences.remove("$FALLBACK_KEY_PREF.$keyAlias")
//                // Xóa khỏi cache
//                if (cachedKey != null) {
//                    cachedKey = null
//                    cacheTimestamp.set(0)
//                }
//                emit(removed)
//            }
//        } catch (e: Exception) {
//            emit(false)
//        }
//    }
//
//    /**
//     * Tạo EncryptionKey từ SecretKey.
//     */
//    private fun createEncryptionKeyFromSecretKey(secretKey: SecretKey): EncryptionKey {
//        // Lưu ý: Không thể trực tiếp truy xuất byte của khóa từ Android KeyStore
//        // Chúng ta sẽ tạo một EncryptionKey với keyBytes trống
//        // Các thao tác mã hóa/giải mã sẽ sử dụng SecretKey này trực tiếp
//        return EncryptionKey(
//            keyBytes = ByteArray(0), // Rỗng vì không thể truy xuất byte từ khóa trong KeyStore
//            algorithm = secretKey.algorithm,
//            creationTimestamp = System.currentTimeMillis()
//        )
//    }
//
//    /**
//     * Tạo khóa fallback cho thiết bị không hỗ trợ KeyStore.
//     */
//    private fun generateFallbackKey(keyAlias: String): EncryptionKey? {
//        try {
//            val keyBytes = ByteArray(CryptoConstants.KEY_SIZE / 8) // Chuyển từ bit sang byte
//            java.security.SecureRandom().nextBytes(keyBytes)
//
//            // Lưu vào SharedPreferences dưới dạng chuỗi Base64
//            val keyBase64 = android.util.Base64.encodeToString(keyBytes, android.util.Base64.NO_WRAP)
//            securePreferences.putString("$FALLBACK_KEY_PREF.$keyAlias", keyBase64)
//
//            val encryptionKey = EncryptionKey(
//                keyBytes = keyBytes,
//                algorithm = CryptoConstants.AES_ALGORITHM,
//                creationTimestamp = System.currentTimeMillis()
//            )
//
//            // Cache khóa
//            updateKeyCache(encryptionKey)
//
//            return encryptionKey
//        } catch (e: Exception) {
//            return null
//        }
//    }
//
//    /**
//     * Lấy khóa fallback từ SharedPreferences.
//     */
//    private fun getFallbackKey(keyAlias: String): EncryptionKey? {
//        try {
//            val keyBase64 = securePreferences.getString("$FALLBACK_KEY_PREF.$keyAlias", null)
//                ?: return null
//
//            val keyBytes = android.util.Base64.decode(keyBase64, android.util.Base64.NO_WRAP)
//
//            val encryptionKey = EncryptionKey(
//                keyBytes = keyBytes,
//                algorithm = CryptoConstants.AES_ALGORITHM,
//                creationTimestamp = System.currentTimeMillis()
//            )
//
//            // Cache khóa
//            updateKeyCache(encryptionKey)
//
//            return encryptionKey
//        } catch (e: Exception) {
//            return null
//        }
//    }
//
//    /**
//     * Cập nhật cache khóa để tối ưu hiệu suất.
//     */
//    private fun updateKeyCache(key: EncryptionKey) {
//        cachedKey = key
//        cacheTimestamp.set(System.currentTimeMillis())
//    }
//}