package com.mangala.wallet.features.addressbook.data.model.setting

import com.benasher44.uuid.uuid4
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho hàng đợi đồng bộ hóa offline
 * Tương ứng với bảng 'offline_queue' trong database
 */
data class OfflineQueueEntity(
    val id: String, // UUID
    val actionType: String, // CREATE, UPDATE, DELETE
    val entityType: String, // CONTACT, WALLET, TAG, GROUP, etc.
    val entityId: String,
    val data: String, // JSON string
    val attemptCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * Kiểm tra đã thử quá số lần quy định chưa
     * @param maxAttempts Số lần thử tối đa
     * @return true nếu đã vượt quá số lần thử
     */
    fun hasExceededMaxAttempts(maxAttempts: Int): Boolean {
        return attemptCount >= maxAttempts
    }

    /**
     * Tạo một bản sao với số lần thử tăng lên 1
     * @return Đối tượng OfflineQueueEntity mới với attemptCount+1
     */
    fun incrementAttempt(): OfflineQueueEntity {
        return copy(
            attemptCount = attemptCount + 1,
            updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        )
    }

    companion object {
        // Các loại hành động
        const val ACTION_CREATE = "CREATE"
        const val ACTION_UPDATE = "UPDATE"
        const val ACTION_DELETE = "DELETE"

        // Các loại entity
        const val ENTITY_CONTACT = "CONTACT"
        const val ENTITY_WALLET = "WALLET"
        const val ENTITY_TAG = "TAG"
        const val ENTITY_GROUP = "GROUP"
        const val ENTITY_TRANSACTION = "TRANSACTION"

        // Số lần thử tối đa mặc định
        const val DEFAULT_MAX_ATTEMPTS = 5

        /**
         * Tạo một đối tượng OfflineQueueEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            actionType: String,
            entityType: String,
            entityId: String,
            data: String
        ): OfflineQueueEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return OfflineQueueEntity(
                id = id,
                actionType = actionType,
                entityType = entityType,
                entityId = entityId,
                data = data,
                attemptCount = 0,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}