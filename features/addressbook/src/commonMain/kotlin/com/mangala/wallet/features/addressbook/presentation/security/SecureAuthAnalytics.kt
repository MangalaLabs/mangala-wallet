package com.mangala.wallet.features.addressbook.presentation.security

/**
 * Triển khai mặc định của AnalyticsTracker
 */
class DefaultAnalyticsTracker : AnalyticsTracker {
    override fun logEvent(name: String, params: Map<String, String>) {
        // Triển khai mặc định, có thể sử dụng Firebase Analytics hoặc các dịch vụ khác
        // Đối với KMP, có thể sử dụng expect/actual để triển khai cho từng nền tảng
    }
}/**
 * Interface theo dõi và phân tích hành vi xác thực
 */
interface SecureAuthAnalytics {
    /**
     * Ghi lại sự kiện xác thực bắt đầu
     *
     * @param actionId ID của hành động yêu cầu xác thực
     * @param authenticationType Phương thức xác thực được sử dụng
     */
    fun trackAuthenticationStarted(actionId: SecureActionId, authenticationType: SecureActionType)

    /**
     * Ghi lại sự kiện xác thực thành công
     *
     * @param actionId ID của hành động yêu cầu xác thực
     * @param authenticationType Phương thức xác thực được sử dụng
     * @param durationMs Thời gian để hoàn thành xác thực (mili giây)
     */
    fun trackAuthenticationSuccess(
        actionId: SecureActionId,
        authenticationType: SecureActionType,
        durationMs: Long
    )

    /**
     * Ghi lại sự kiện xác thực thất bại
     *
     * @param actionId ID của hành động yêu cầu xác thực
     * @param authenticationType Phương thức xác thực được sử dụng
     * @param failureReason Lý do thất bại
     * @param attemptCount Số lần thử
     */
    fun trackAuthenticationFailure(
        actionId: SecureActionId,
        authenticationType: SecureActionType,
        failureReason: String,
        attemptCount: Int
    )

    /**
     * Ghi lại sự kiện từ chối xác thực
     *
     * @param actionId ID của hành động yêu cầu xác thực
     * @param authenticationType Phương thức xác thực được sử dụng
     */
    fun trackAuthenticationCancelled(actionId: SecureActionId, authenticationType: SecureActionType)

    /**
     * Ghi lại sự kiện sử dụng session có sẵn
     *
     * @param actionId ID của hành động
     * @param sessionAge Tuổi của session hiện tại (mili giây)
     */
    fun trackSessionReuse(actionId: SecureActionId, sessionAge: Long)
}

/**
 * Lý do lỗi xác thực
 */
sealed class AuthenticationFailureReason {
    object InvalidCredentials : AuthenticationFailureReason()
    object BiometricNotAvailable : AuthenticationFailureReason()
    object BiometricHardwareFailed : AuthenticationFailureReason()
    object TooManyAttempts : AuthenticationFailureReason()
    object Timeout : AuthenticationFailureReason()
    data class Other(val reason: String) : AuthenticationFailureReason()

    override fun toString(): String {
        return when (this) {
            is InvalidCredentials -> "INVALID_CREDENTIALS"
            is BiometricNotAvailable -> "BIOMETRIC_NOT_AVAILABLE"
            is BiometricHardwareFailed -> "BIOMETRIC_HARDWARE_FAILED"
            is TooManyAttempts -> "TOO_MANY_ATTEMPTS"
            is Timeout -> "TIMEOUT"
            is Other -> "OTHER: $reason"
        }
    }
}

/**
 * Triển khai mặc định của SecureAuthAnalytics
 */
class DefaultSecureAuthAnalytics(
    private val analyticsTracker: AnalyticsTracker,
    private val timeProvider: TimeProvider
) : SecureAuthAnalytics {

    // Map lưu thời điểm bắt đầu của các phiên xác thực
    private val authStartTimes = mutableMapOf<String, Long>()

    override fun trackAuthenticationStarted(
        actionId: SecureActionId,
        authenticationType: SecureActionType
    ) {
        val key = "${actionId}_${authenticationType}"
        authStartTimes[key] = timeProvider.now().toEpochMilliseconds()

        analyticsTracker.logEvent(
            "auth_started",
            mapOf(
                "action_id" to actionId.name,
                "auth_type" to authenticationType.toString()
            )
        )
    }

    override fun trackAuthenticationSuccess(
        actionId: SecureActionId,
        authenticationType: SecureActionType,
        durationMs: Long
    ) {
        val key = "${actionId}_${authenticationType}"
        val startTime = authStartTimes.remove(key)

        val duration = if (startTime != null) {
            timeProvider.now().toEpochMilliseconds() - startTime
        } else {
            durationMs
        }

        analyticsTracker.logEvent(
            "auth_success",
            mapOf(
                "action_id" to actionId.name,
                "auth_type" to authenticationType.toString(),
                "duration_ms" to duration.toString()
            )
        )
    }

    override fun trackAuthenticationFailure(
        actionId: SecureActionId,
        authenticationType: SecureActionType,
        failureReason: String,
        attemptCount: Int
    ) {
        analyticsTracker.logEvent(
            "auth_failure",
            mapOf(
                "action_id" to actionId.name,
                "auth_type" to authenticationType.toString(),
                "failure_reason" to failureReason,
                "attempt_count" to attemptCount.toString()
            )
        )
    }

    override fun trackAuthenticationCancelled(
        actionId: SecureActionId,
        authenticationType: SecureActionType
    ) {
        val key = "${actionId}_${authenticationType}"
        authStartTimes.remove(key)

        analyticsTracker.logEvent(
            "auth_cancelled",
            mapOf(
                "action_id" to actionId.name,
                "auth_type" to authenticationType.toString()
            )
        )
    }

    override fun trackSessionReuse(actionId: SecureActionId, sessionAge: Long) {
        analyticsTracker.logEvent(
            "auth_session_reused",
            mapOf(
                "action_id" to actionId.name,
                "session_age_ms" to sessionAge.toString()
            )
        )
    }
}

/**
 * Interface tracking analytics events
 */
interface AnalyticsTracker {
    fun logEvent(name: String, params: Map<String, String>)
}