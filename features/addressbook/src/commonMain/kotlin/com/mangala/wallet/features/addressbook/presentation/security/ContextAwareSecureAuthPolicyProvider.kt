package com.mangala.wallet.features.addressbook.presentation.security

import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactByIdUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Extended SecureActionId to support context-aware actions
 */
enum class ContactSecureActionId {
    EditNormalContact,
    EditHighSecurityContact,
    DeleteNormalContact,
    DeleteHighSecurityContact
}

/**
 * Context-aware authentication policy provider that considers the security level of contacts
 * before determining authentication requirements
 */
class ContextAwareSecureAuthPolicyProvider(
    private val defaultProvider: SecureAuthPolicyProvider = DefaultSecureAuthPolicyProvider(),
    private val getContactByIdUseCase: GetContactByIdUseCase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : SecureAuthPolicyProvider {
    
    // State flow to track current contact context
    private val _currentContactId = MutableStateFlow<String?>(null)
    val currentContactId: StateFlow<String?> = _currentContactId
    
    // Cache for contact security levels to avoid blocking
    private val contactSecurityCache = mutableMapOf<String, SecurityLevel>()
    
    /**
     * Set the current contact ID context for policy evaluation
     */
    fun setContactContext(contactId: String?) {
        _currentContactId.value = contactId
    }
    
    /**
     * Clear the contact context
     */
    fun clearContactContext() {
        _currentContactId.value = null
    }
    
    /**
     * Preload contact security level into cache
     */
    suspend fun preloadContactSecurity(contactId: String) {
        try {
            val contact = getContactByIdUseCase(contactId)
            contact?.securityLevel?.let {
                contactSecurityCache[contactId] = it
            }
        } catch (e: Exception) {
            // Ignore errors, will use default policy
        }
    }
    
    /**
     * Get policy based on contact ID and action using cached data
     */
    fun getPolicyForContact(action: SecureActionId, contactId: String): SecureActionType {
        return when (action) {
            SecureActionId.EditContact,
            SecureActionId.DeleteContact -> {
                // Get cached security level or default to requiring auth
                val securityLevel = contactSecurityCache[contactId]
                
                // If contact has NORMAL security, no authentication needed
                if (securityLevel == SecurityLevel.NORMAL) {
                    return SecureActionType.None
                }
                
                // For HIGH or MAXIMUM security, require authentication
                return when (securityLevel) {
                    SecurityLevel.HIGH -> SecureActionType.RequireBiometryOrPin
                    SecurityLevel.MAXIMUM -> SecureActionType.RequireBiometryOrPin
                    null -> SecureActionType.RequireBiometryOrPin // Default to secure if unknown
                    else -> defaultProvider.getPolicyFor(action)
                }
            }
            
            else -> defaultProvider.getPolicyFor(action)
        }
    }
    
    override fun getPolicyFor(action: SecureActionId): SecureActionType {
        // For Edit and Delete actions, check if we have contact context
        when (action) {
            SecureActionId.EditContact,
            SecureActionId.DeleteContact -> {
                val contactId = _currentContactId.value
                if (contactId != null) {
                    return getPolicyForContact(action, contactId)
                }
            }
            
            // For other actions, use default policy
            else -> {}
        }
        
        return defaultProvider.getPolicyFor(action)
    }
}