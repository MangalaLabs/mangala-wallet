package com.mangala.wallet.features.addressbook.presentation.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.theme.MangalaTypography
import androidx.compose.ui.text.TextStyle

/**
 * A secure button specifically for the Edit Contact screen that handles security level changes.
 * This button checks if authentication is needed based on the current security level in the UI,
 * not the saved security level in the database.
 */
@Composable
fun EditContactSecureButton(
    currentSecurityLevel: SecurityLevel,
    originalSecurityLevel: SecurityLevel,
    isAuthenticated: Boolean,
    label: String,
    onClick: () -> Unit,
    onAuthenticationSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: MangalaButtonSize = MangalaButtonSize.Medium,
    buttonStyle: MangalaButtonStyle = MangalaButtonStyle.GRADIENT,
    style: TextStyle = MangalaTypography.Size17SemiBold()
) {
    val secureActionHandler = SecureAuthProvider.current
    
    // Check if authentication is needed
    val needsAuthentication = when {
        // Already authenticated
        isAuthenticated -> {
            false
        }
        
        // Upgrading from NORMAL to HIGH/MAXIMUM
        originalSecurityLevel == SecurityLevel.NORMAL && 
        (currentSecurityLevel == SecurityLevel.HIGH || currentSecurityLevel == SecurityLevel.MAXIMUM) -> {
            true
        }
        
        // Already HIGH/MAXIMUM security
        currentSecurityLevel == SecurityLevel.HIGH || currentSecurityLevel == SecurityLevel.MAXIMUM -> {
            true
        }
        
        // Otherwise no authentication needed
        else -> {
            false
        }
    }
    MangalaGradientButton(
        label = label,
        onClick = {
            if (needsAuthentication) {
                secureActionHandler.runSecureActionForId(
                    actionId = SecureActionId.EditContact,
                    onSuccess = {
                        onAuthenticationSuccess()
                        onClick()
                    },
                    onCancel = {
                        println("[EditContactSecureButton] Authentication cancelled")
                    }
                )
            } else {
                onClick()
            }
        },
        enabled = enabled,
        size = size,
        modifier = modifier,
        buttonStyle = buttonStyle,
        style = style
    )
}