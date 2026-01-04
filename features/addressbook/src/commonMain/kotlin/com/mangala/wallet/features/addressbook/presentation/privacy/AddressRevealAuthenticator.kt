package com.mangala.wallet.features.addressbook.presentation.privacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.presentation.security.SecureActionHandler
import com.mangala.wallet.features.addressbook.presentation.security.SecureActionId
import com.mangala.wallet.features.addressbook.presentation.security.SecureAuthProvider
import com.mangala.wallet.features.addressbook.presentation.security.SecureButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Address Reveal Authenticator
 * 
 * Handles authentication for revealing obfuscated wallet addresses.
 * Integrates with existing SecureActionHandler infrastructure.
 * 
 * Features:
 * - Contact-level authentication requirements
 * - Address-level security policies
 * - Session management integration
 * - Analytics and audit logging
 * - Composable integration helpers
 */
class AddressRevealAuthenticator(
    private val secureActionHandler: SecureActionHandler,
    private val coroutineScope: CoroutineScope
) {
    
    /**
     * Authenticate user to reveal a sensitive address
     * 
     * @param contact The contact containing the address
     * @param walletAddress The specific wallet address to reveal
     * @return Boolean indicating authentication success
     */
    suspend fun authenticateForReveal(
        contact: ContactEntity,
        walletAddress: WalletAddressEntity? = null
    ): Boolean {
        // Determine authentication requirements based on contact and address settings
        val actionId = determineSecureActionId(contact, walletAddress)
        
        return suspendCancellableCoroutine { continuation ->
            secureActionHandler.runSecureActionForId(
                actionId = actionId,
                onSuccess = {
                    // Log the successful address reveal for audit trail
                    logAddressReveal(contact, walletAddress, success = true)
                    continuation.resume(true)
                },
                onCancel = {
                    // Log the cancelled authentication attempt
                    logAddressReveal(contact, walletAddress, success = false)
                    continuation.resume(false)
                }
            )
        }
    }
    
    /**
     * Batch authenticate for revealing multiple addresses
     * 
     * @param contact The contact containing the addresses
     * @param addresses List of wallet addresses to reveal
     * @return Boolean indicating authentication success for all addresses
     */
    suspend fun authenticateForBatchReveal(
        contact: ContactEntity,
        addresses: List<WalletAddressEntity>
    ): Boolean {
        // Use the highest security requirement among all addresses
        val highestSecurityAction = addresses.maxOfOrNull { address ->
            determineSecureActionId(contact, address)
        } ?: SecureActionId.RevealAddress
        
        return suspendCancellableCoroutine { continuation ->
            secureActionHandler.runSecureActionForId(
                actionId = highestSecurityAction,
                onSuccess = {
                    addresses.forEach { address ->
                        logAddressReveal(contact, address, success = true)
                    }
                    continuation.resume(true)
                },
                onCancel = {
                    addresses.forEach { address ->
                        logAddressReveal(contact, address, success = false)
                    }
                    continuation.resume(false)
                }
            )
        }
    }
    
    /**
     * Check if authentication is required for revealing an address
     */
    fun isAuthenticationRequired(
        contact: ContactEntity,
        walletAddress: WalletAddressEntity? = null,
        privacyModeEnabled: Boolean
    ): Boolean {
        if (!privacyModeEnabled) return false
        
        return AddressObfuscator.shouldObfuscate(
            contact = contact,
            walletAddressIsSensitive = walletAddress?.isSensitive ?: false,
            privacyModeEnabled = privacyModeEnabled
        )
    }
    
    /**
     * Determine the appropriate SecureActionId based on contact and address security settings
     */
    private fun determineSecureActionId(
        contact: ContactEntity,
        walletAddress: WalletAddressEntity?
    ): SecureActionId {
        return when {
            // Maximum security for SECRET contacts
            contact.privacyDisplayMode == DisplayMode.SECRET -> SecureActionId.RevealSecretAddress
            
            // High security for sensitive contacts or addresses
            contact.isSensitive == true || (walletAddress?.isSensitive == true) -> SecureActionId.RevealSensitiveAddress
            
            // Standard security for hidden addresses
            contact.privacyDisplayMode == DisplayMode.HIDDEN -> SecureActionId.RevealAddress
            
            // Default reveal action
            else -> SecureActionId.RevealAddress
        }
    }
    
    /**
     * Log address reveal action for audit trail
     */
    private fun logAddressReveal(
        contact: ContactEntity,
        walletAddress: WalletAddressEntity?,
        success: Boolean
    ) {
        coroutineScope.launch {
            try {
                // TODO: Integrate with existing security audit logging system
                // This would typically go to SecurityAuditRepository
                println(
                    "Address reveal: contactId=${contact.id}, " +
                    "addressId=${walletAddress?.id}, " +
                    "success=$success, " +
                    "timestamp=${kotlinx.datetime.Clock.System.now()}"
                )
            } catch (e: Exception) {
                // Don't fail the main operation if logging fails
                println("Failed to log address reveal: ${e.message}")
            }
        }
    }
}

/**
 * Composable integration helpers
 */

/**
 * Remember an AddressRevealAuthenticator instance
 */
@Composable
fun rememberAddressRevealAuthenticator(): AddressRevealAuthenticator {
    val secureActionHandler = SecureAuthProvider.current
    val coroutineScope = rememberCoroutineScope()
    
    return remember(secureActionHandler, coroutineScope) {
        AddressRevealAuthenticator(secureActionHandler, coroutineScope)
    }
}

/**
 * Secure Address Reveal Button Component
 * 
 * Integrates authentication directly into the button component
 */
@Composable
fun SecureAddressRevealButton(
    contact: ContactEntity,
    walletAddress: WalletAddressEntity? = null,
    onRevealSuccess: () -> Unit,
    onRevealCancelled: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val authenticator = rememberAddressRevealAuthenticator()
    val coroutineScope = rememberCoroutineScope()
    
    val actionId = remember(contact, walletAddress) {
        when {
            contact.privacyDisplayMode == DisplayMode.SECRET -> SecureActionId.RevealSecretAddress
            contact.isSensitive == true || (walletAddress?.isSensitive == true) -> SecureActionId.RevealSensitiveAddress
            else -> SecureActionId.RevealAddress
        }
    }
    
    SecureButton(
        actionId = actionId,
        onClick = {
            // Authentication succeeded, trigger reveal
            onRevealSuccess()
        },
        onCancel = {
            // Authentication was cancelled
            onRevealCancelled()
        },
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Secure Address Reveal Action Wrapper
 * 
 * Wraps any composable with secure reveal functionality
 */
@Composable
fun SecureAddressRevealAction(
    contact: ContactEntity,
    walletAddress: WalletAddressEntity? = null,
    onRevealSuccess: () -> Unit,
    onRevealCancelled: () -> Unit = {},
    content: @Composable (onClick: () -> Unit) -> Unit
) {
    val authenticator = rememberAddressRevealAuthenticator()
    val coroutineScope = rememberCoroutineScope()
    
    content { 
        coroutineScope.launch {
            val success = authenticator.authenticateForReveal(contact, walletAddress)
            if (success) {
                onRevealSuccess()
            } else {
                onRevealCancelled()
            }
        }
    }
}

/**
 * Hook for address reveal authentication in ViewModels/ScreenModels
 */
class AddressRevealUseCase(
    private val authenticator: AddressRevealAuthenticator
) {
    suspend fun revealAddress(
        contact: ContactEntity,
        walletAddress: WalletAddressEntity? = null
    ): Boolean {
        return authenticator.authenticateForReveal(contact, walletAddress)
    }
    
    suspend fun revealMultipleAddresses(
        contact: ContactEntity,
        addresses: List<WalletAddressEntity>
    ): Boolean {
        return authenticator.authenticateForBatchReveal(contact, addresses)
    }
    
    fun isAuthenticationRequired(
        contact: ContactEntity,
        walletAddress: WalletAddressEntity? = null,
        privacyModeEnabled: Boolean
    ): Boolean {
        return authenticator.isAuthenticationRequired(contact, walletAddress, privacyModeEnabled)
    }
}