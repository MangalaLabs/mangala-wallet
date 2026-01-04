package com.mangala.wallet.core.security.config

import com.mangala.wallet.core.security.models.SecurityLevel

/**
 * Interface for providing security configuration
 * Can be implemented with remote config, local settings, or role-based policies
 */
interface SecurityConfigProvider {
    /**
     * Get security policy for a specific action
     */
    fun getSecurityPolicyForAction(actionId: String): String?
    
    /**
     * Get amount-based security thresholds
     */
    fun getAmountThresholds(): List<AmountThreshold>
    
    /**
     * Check if a specific security feature is enabled
     */
    fun isSecurityFeatureEnabled(feature: SecurityFeature): Boolean
}

/**
 * Amount threshold for security level escalation
 */
data class AmountThreshold(
    val amountUSD: Double,
    val securityLevel: SecurityLevel
)

/**
 * Security features that can be toggled
 */
enum class SecurityFeature {
    BIOMETRIC_AUTHENTICATION,
    TWO_FACTOR_AUTHENTICATION,
    SESSION_BASED_AUTH,
    DELAYED_EXECUTION,
    RISK_BASED_AUTH
}

/**
 * Default implementation with hardcoded values
 */
class DefaultSecurityConfigProvider : SecurityConfigProvider {
    
    override fun getSecurityPolicyForAction(actionId: String): String? = null
    
    override fun getAmountThresholds(): List<AmountThreshold> = listOf(
        AmountThreshold(0.0, SecurityLevel.None),           // $0-10
        AmountThreshold(10.0, SecurityLevel.RequirePin),    // $10-100
        AmountThreshold(100.0, SecurityLevel.RequireBiometryOrPin), // $100-1000
        AmountThreshold(1000.0, SecurityLevel.Require2FA)   // >$1000
    )
    
    override fun isSecurityFeatureEnabled(feature: SecurityFeature): Boolean {
        return when (feature) {
            SecurityFeature.BIOMETRIC_AUTHENTICATION -> true
            SecurityFeature.TWO_FACTOR_AUTHENTICATION -> true
            SecurityFeature.SESSION_BASED_AUTH -> false // Phase 2
            SecurityFeature.DELAYED_EXECUTION -> false  // Phase 2
            SecurityFeature.RISK_BASED_AUTH -> false    // Phase 2
        }
    }
}