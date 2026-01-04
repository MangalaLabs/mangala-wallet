package com.mangala.wallet.core.security.models

/**
 * Base sealed class for all secure actions across the app
 */
abstract class SecureAction {
    abstract val actionId: String
}

/**
 * Security level required for an action
 */
enum class SecurityLevel(val index: Int) {
    None(0),
    RequireConfirmation(1),
    RequirePin(2),
    RequireBiometryOrPin(3),
    Require2FA(4);

    companion object {
        val defaultSecurityLevel = RequireBiometryOrPin // High default security level to prevent accidental skipping

        fun parseSecurityLevel(level: String): SecurityLevel {
            return try {
                SecurityLevel.valueOf(level)
            } catch (e: Exception) {
                defaultSecurityLevel // Fallback to default if unknown
            }
        }
    }
}

/**
 * Result of a security check
 */
sealed class SecurityCheckResult {
    data object Allowed : SecurityCheckResult()
    
    data class AuthenticationRequired(
        val level: SecurityLevel,
        val reason: String? = null
    ) : SecurityCheckResult()
    
    data class Denied(
        val reason: String
    ) : SecurityCheckResult()
}

/**
 * Exception thrown when security requirements are not met
 */
class SecurityException(message: String) : Exception(message)