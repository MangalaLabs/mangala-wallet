package com.mangala.wallet.core.security.provider

import com.mangala.wallet.core.security.models.SecureAction
import com.mangala.wallet.core.security.models.SecurityCheckResult
import com.mangala.wallet.core.security.models.SecurityLevel

/**
 * Interface for providing security policies for different actions
 */
interface SecurityPolicyProvider<T : SecureAction> {
    /**
     * Get the base security level required for an action
     */
    fun getSecurityLevel(action: T): SecurityLevel
    
    /**
     * Check if an action is allowed and what security requirements it has
     */
    fun checkSecurity(action: T, context: SecurityContext? = null): SecurityCheckResult
}

/**
 * Context for security checks (e.g., amount, user role)
 */
data class SecurityContext(
    val userRole: String? = null,
    val transactionAmount: Double? = null,
    val currency: String? = null,
    val additionalData: Map<String, Any?> = emptyMap()
)