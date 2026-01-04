package com.mangala.wallet.features.addressbook.presentation.security

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Trạng thái phiên xác thực
 */
data class AuthSessionState(
    val actionType: SecureActionType,
    val timestamp: Instant,
    val isValid: Boolean
)

/**
 * Interface quản lý phiên xác thực
 */
interface SecureAuthSessionManager {
    /**
     * Kiểm tra xem có phiên xác thực hợp lệ hiện tại cho loại hành động không
     *
     * @param actionType Loại hành động cần kiểm tra
     * @param actionId Optional: ID của action cụ thể để kiểm tra các case đặc biệt
     * @return true nếu có phiên xác thực hợp lệ
     */
    fun hasValidSession(actionType: SecureActionType, actionId: SecureActionId? = null): Boolean

    /**
     * Lấy tuổi của phiên xác thực hợp lệ trong mili giây
     *
     * @param actionType Loại hành động cần kiểm tra
     * @return Tuổi của phiên trong mili giây, hoặc 0 nếu không có phiên hợp lệ
     */
    fun getSessionAge(actionType: SecureActionType): Long

    /**
     * Tạo phiên xác thực mới cho một loại hành động
     *
     * @param actionType Loại hành động đã được xác thực
     */
    fun createSession(actionType: SecureActionType)

    /**
     * Hủy tất cả phiên xác thực hiện tại
     */
    fun invalidateAllSessions()

    /**
     * Theo dõi trạng thái của phiên xác thực
     */
    fun observeSessionState(): Flow<Map<String, AuthSessionState>>
}

/**
 * Triển khai mặc định của SecureAuthSessionManager
 */
class DefaultSecureAuthSessionManager(
    private val sessionDuration: kotlin.time.Duration = 5.minutes, // Thời gian mặc định là 5 phút
    private val encryptedStorage: SecureStorageWrapper,
    private val timeProvider: TimeProvider
) : SecureAuthSessionManager {

    private val sessionState = MutableStateFlow<Map<String, AuthSessionState>>(emptyMap())

    init {
        // Khôi phục session từ encrypted storage khi khởi tạo
        restoreSessionState()
    }

    override fun hasValidSession(actionType: SecureActionType, actionId: SecureActionId?): Boolean {
        // ViewHighSecurityContact luôn yêu cầu xác thực mới
        if (actionId == SecureActionId.ViewHighSecurityContact) {
            return false
        }
        
        // Lấy trạng thái hiện tại
        val currentState = sessionState.value

        // Lấy level xác thực của action type
        val requestedLevel = getAuthenticationLevel(actionType)

        // Kiểm tra các phiên hiện có
        return currentState.values.any { session ->
            // Phiên phải còn hợp lệ
            session.isValid &&
                    // Thời gian chưa hết hạn
                    isSessionTimestampValid(session.timestamp) &&
                    // Level xác thực của phiên phải cao hơn hoặc bằng level được yêu cầu
                    getAuthenticationLevel(session.actionType) >= requestedLevel
        }
    }

    override fun getSessionAge(actionType: SecureActionType): Long {
        // Lấy trạng thái hiện tại
        val currentState = sessionState.value

        // Lấy level xác thực của action type
        val requestedLevel = getAuthenticationLevel(actionType)

        // Tìm phiên hợp lệ có level cao nhất
        val validSession = currentState.values
            .filter { session ->
                session.isValid &&
                        isSessionTimestampValid(session.timestamp) &&
                        getAuthenticationLevel(session.actionType) >= requestedLevel
            }
            .maxByOrNull { getAuthenticationLevel(it.actionType) }

        // Tính tuổi của phiên
        return validSession?.let {
            (timeProvider.now() - it.timestamp).inWholeMilliseconds
        } ?: 0L
    }

    override fun createSession(actionType: SecureActionType) {
        val now = timeProvider.now()
        val newSession = AuthSessionState(
            actionType = actionType,
            timestamp = now,
            isValid = true
        )

        // Cập nhật state hiện tại với session mới
        val updatedSessions = sessionState.value.toMutableMap()
        updatedSessions[actionType.toString()] = newSession
        sessionState.value = updatedSessions

        // Lưu vào encrypted storage
        persistSessionState()
    }

    override fun invalidateAllSessions() {
        sessionState.value = emptyMap()
//        encryptedStorage.remove(SESSION_STORAGE_KEY) //TODO check to remove key
    }

    override fun observeSessionState(): Flow<Map<String, AuthSessionState>> {
        return sessionState.map { sessions ->
            // Lọc các session còn hợp lệ
            sessions.filter { (_, session) ->
                session.isValid && isSessionTimestampValid(session.timestamp)
            }
        }
    }

    /**
     * Kiểm tra xem timestamp của session có còn hợp lệ không
     */
    private fun isSessionTimestampValid(timestamp: Instant): Boolean {
        val now = timeProvider.now()
        val sessionAge = now - timestamp  // Kết quả trả về là kotlin.time.Duration
        return sessionAge < sessionDuration  // So sánh trực tiếp hai Duration
    }

    /**
     * Xác định mức độ xác thực của SecureActionType
     * Mức độ cao hơn có thể thay thế mức độ thấp hơn
     */
    private fun getAuthenticationLevel(actionType: SecureActionType): Int {
        return when (actionType) {
            is SecureActionType.None -> 0
            is SecureActionType.RequirePin, SecureActionType.RequireBiometryOrPin -> 1
            is SecureActionType.Require2FA -> 2
        }
    }

    /**
     * Lưu trạng thái session vào encrypted storage
     */
    private fun persistSessionState() {
        val serializableSessions = sessionState.value.mapValues { (_, session) ->
            mapOf(
                "type" to session.actionType.toString(),
                "timestamp" to session.timestamp.toEpochMilliseconds(),
                "valid" to session.isValid
            )
        }
        encryptedStorage.saveValue(
            SESSION_STORAGE_KEY,
            serializableSessions.toString()
        )
    }

    /**
     * Khôi phục trạng thái session từ encrypted storage
     */
    private fun restoreSessionState() {
        val storedSessions = encryptedStorage.getValue(SESSION_STORAGE_KEY)
        if (storedSessions.isNullOrEmpty()) return

        try {
            // Phân tích cú pháp stored session và chuyển đổi thành AuthSessionState
            // Thực tế cần sử dụng thư viện JSON để parsing
            // Code này chỉ mang tính minh họa
        } catch (e: Exception) {
            // Xử lý lỗi parsing
            invalidateAllSessions()
        }
    }

    companion object {
        private const val SESSION_STORAGE_KEY = "secure_auth_sessions"
    }
}

/**
 * Interface cung cấp thời gian hiện tại
 * Giúp việc kiểm thử dễ dàng hơn
 */
interface TimeProvider {
    fun now(): Instant
}

/**
 * Triển khai mặc định của TimeProvider
 */
class SystemTimeProvider : TimeProvider {
    override fun now(): Instant = Instant.fromEpochMilliseconds(localDateTimeToMillis(
        localDateTimeNow()
    ))
}