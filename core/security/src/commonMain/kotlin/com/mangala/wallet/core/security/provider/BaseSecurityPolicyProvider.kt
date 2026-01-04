package com.mangala.wallet.core.security.provider

import com.mangala.wallet.core.security.config.SecurityConfigProvider
import com.mangala.wallet.core.security.models.SecureAction
import com.mangala.wallet.core.security.models.SecurityCheckResult
import com.mangala.wallet.core.security.models.SecurityLevel

/**
 * Base implementation of SecurityPolicyProvider with common functionality
 */
abstract class BaseSecurityPolicyProvider<T : SecureAction> : SecurityPolicyProvider<T> {
    
    protected open val securityConfigProvider: SecurityConfigProvider? = null
    
    override fun checkSecurity(action: T, context: SecurityContext?): SecurityCheckResult {
        val baseLevel = getSecurityLevel(action)
        val finalLevel = adjustForContext(baseLevel, action, context)
        
        return when (finalLevel) {
            SecurityLevel.None -> SecurityCheckResult.Allowed
            else -> SecurityCheckResult.AuthenticationRequired(
                level = finalLevel,
                reason = getReason(action, context)
            )
        }
    }
    
    /**
     * Adjust security level based on context (e.g., transaction amount)
     */
    protected open fun adjustForContext(
        baseLevel: SecurityLevel,
        action: T,
        context: SecurityContext?
    ): SecurityLevel = baseLevel
    
    /**
     * Get a human-readable reason for the security requirement
     */
    protected open fun getReason(
        action: T, 
        context: SecurityContext?
    ): String? = null
    
    /**
     * Map string configuration to SecurityLevel
     */
    protected fun mapStringToSecurityLevel(policy: String): SecurityLevel {
        return when (policy.lowercase()) {
            "none" -> SecurityLevel.None
            "pin" -> SecurityLevel.RequirePin
            "biometryorpin", "biometric_or_pin" -> SecurityLevel.RequireBiometryOrPin
            "2fa", "two_factor" -> SecurityLevel.Require2FA
            else -> SecurityLevel.None
        }
    }
}