package com.mangala.wallet.features.addressbook.presentation.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.mangala.wallet.ui.component.MangalaButtonSize
import org.koin.compose.koinInject

/**
 * A secure button that is aware of contact context for authentication decisions
 */
@Composable
fun ContactAwareSecureButton(
    contactId: String,
    actionId: SecureActionId,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: MangalaButtonSize = MangalaButtonSize.Medium
) {
    // Get the context-aware policy provider
    val policyProvider = koinInject<SecureAuthPolicyProvider>() as? ContextAwareSecureAuthPolicyProvider
    
    // Set contact context when this composable enters composition
    LaunchedEffect(contactId) {
        policyProvider?.setContactContext(contactId)
        // Preload contact security level to avoid blocking
        policyProvider?.preloadContactSecurity(contactId)
    }
    
    // Clear context when this composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            policyProvider?.clearContactContext()
        }
    }
    
    // Use the regular SecureGradientButton which will now use the context
    SecureGradientButton(
        actionId = actionId,
        label = label,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        size = size
    )
}