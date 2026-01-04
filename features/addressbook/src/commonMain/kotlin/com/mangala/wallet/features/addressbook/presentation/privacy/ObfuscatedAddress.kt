package com.mangala.wallet.features.addressbook.presentation.privacy

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import kotlinx.coroutines.delay

/**
 * Obfuscated Address Component
 * 
 * Displays wallet addresses with privacy-aware obfuscation based on:
 * - Global privacy mode state
 * - Contact sensitivity settings
 * - Address-specific sensitivity
 * - User display preferences
 * 
 * Features:
 * - Tap-to-reveal functionality with authentication
 * - Copy protection when obfuscated
 * - Smooth animations between states
 * - Accessibility support
 * - Visual feedback for different states
 */
@Composable
fun ObfuscatedAddress(
    address: String,
    contact: ContactEntity,
    walletAddress: WalletAddressEntity? = null,
    privacyModeEnabled: Boolean,
    modifier: Modifier = Modifier,
    onRevealRequest: suspend () -> Boolean = { true },
    onCopy: (String) -> Unit = { },
    showCopyButton: Boolean = true,
    maxLines: Int = 1
) {
    var isRevealed by remember(address, privacyModeEnabled) { mutableStateOf(false) }
    var isAuthenticating by remember { mutableStateOf(false) }
    var revealTimeRemaining by remember { mutableStateOf(0) }
    
    val clipboardManager = LocalClipboardManager.current
    
    // Determine if address should be obfuscated
    val shouldObfuscate = AddressObfuscator.shouldObfuscate(
        contact = contact,
        walletAddressIsSensitive = walletAddress?.isSensitive ?: false,
        privacyModeEnabled = privacyModeEnabled
    )
    
    // Get display address
    val displayAddress = remember(address, shouldObfuscate, isRevealed, contact.privacyDisplayMode) {
        when {
            !shouldObfuscate || isRevealed -> address
            else -> {
                val obfuscated = AddressObfuscator.obfuscate(address, contact.privacyDisplayMode)
                obfuscated
            }
        }
    }
    
    // Animation for reveal state
    val alpha by animateFloatAsState(
        targetValue = if (isAuthenticating) 0.7f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "address_alpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isAuthenticating) 0.98f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "address_scale"
    )
    
    // Auto-hide revealed address after 10 seconds
    LaunchedEffect(isRevealed) {
        if (isRevealed && shouldObfuscate) {
            revealTimeRemaining = 10
            while (revealTimeRemaining > 0) {
                delay(1000)
                revealTimeRemaining--
            }
            isRevealed = false
        }
    }
    
    Surface(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        color = if (shouldObfuscate && !isRevealed) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .semantics {
                    role = Role.Button
                    contentDescription = if (shouldObfuscate && !isRevealed) {
                        "Sensitive address hidden. Tap to reveal."
                    } else {
                        "Wallet address: $address"
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Address content
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lock icon for obfuscated addresses
                if (shouldObfuscate && !isRevealed) {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                // Address text with animation
                AnimatedContent(
                    targetState = displayAddress,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300)) + 
                         slideInVertically(animationSpec = tween(300)) { it / 2 })
                            .togetherWith(
                                fadeOut(animationSpec = tween(150)) + 
                                slideOutVertically(animationSpec = tween(150)) { -it / 2 }
                            )
                    },
                    label = "address_text_transition",
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = shouldObfuscate && !isRevealed && !isAuthenticating,
                            onClick = {
                                isAuthenticating = true
                            }
                        )
                ) { currentAddress ->
                    Text(
                        text = currentAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = if (currentAddress != address) FontFamily.Default else FontFamily.Monospace,
                        color = if (shouldObfuscate && !isRevealed) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = maxLines,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Reveal indicator for obfuscated addresses
                if (shouldObfuscate && !isRevealed) {
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    if (isAuthenticating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            Icons.Outlined.Visibility,
                            contentDescription = "Tap to reveal",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Auto-hide countdown
                if (isRevealed && revealTimeRemaining > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${revealTimeRemaining}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Copy button (only shown for non-obfuscated addresses)
            if (showCopyButton && (!shouldObfuscate || isRevealed)) {
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(address))
                        onCopy(address)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Outlined.ContentCopy,
                        contentDescription = "Copy address",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    
    // Handle authentication
    LaunchedEffect(isAuthenticating) {
        if (isAuthenticating) {
            try {
                val authSuccess = onRevealRequest()
                if (authSuccess) {
                    isRevealed = true
                }
            } catch (e: Exception) {
                // Authentication failed or cancelled
            } finally {
                isAuthenticating = false
            }
        }
    }
}

/**
 * Simple Obfuscated Address - just text, no decorations
 */
@Composable
fun SimpleObfuscatedAddress(
    address: String,
    contact: ContactEntity,
    walletAddress: WalletAddressEntity? = null,
    privacyModeEnabled: Boolean,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val shouldObfuscate = AddressObfuscator.shouldObfuscate(
        contact = contact,
        walletAddressIsSensitive = walletAddress?.isSensitive ?: false,
        privacyModeEnabled = privacyModeEnabled
    )
    
    val displayAddress = remember(address, shouldObfuscate, contact.privacyDisplayMode) {
        when {
            !shouldObfuscate -> address
            else -> AddressObfuscator.obfuscate(address, contact.privacyDisplayMode)
        }
    }
    
    Text(
        text = displayAddress,
        style = style,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}

/**
 * Address Privacy Summary - shows overview of address privacy states
 */
@Composable
fun AddressPrivacySummary(
    addresses: List<WalletAddressEntity> = emptyList(),
    contact: ContactEntity,
    privacyModeEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val (obfuscatedCount, totalCount) = remember(addresses, privacyModeEnabled) {
        val total = addresses.size
        val obfuscated = addresses.count { address ->
            AddressObfuscator.shouldObfuscate(
                contact = contact,
                walletAddressIsSensitive = address.isSensitive,
                privacyModeEnabled = privacyModeEnabled
            )
        }
        obfuscated to total
    }
    
    if (totalCount > 0) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // TODO: Create PrivacyModeIndicator component
            Icon(
                Icons.Outlined.Lock,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = if (obfuscatedCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = when {
                    !privacyModeEnabled -> "$totalCount addresses visible"
                    obfuscatedCount == 0 -> "$totalCount addresses visible"
                    obfuscatedCount == totalCount -> "$totalCount addresses hidden"
                    else -> "$obfuscatedCount of $totalCount addresses hidden"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Address Group with Privacy Support
 * 
 * Groups multiple addresses with consistent privacy handling
 */
@Composable
fun AddressGroup(
    addresses: List<WalletAddressEntity>,
    contact: ContactEntity,
    privacyModeEnabled: Boolean,
    modifier: Modifier = Modifier,
    onRevealRequest: suspend () -> Boolean = { true },
    onCopy: (String) -> Unit = { },
    title: String? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (title != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                AddressPrivacySummary(
                    addresses = addresses,
                    contact = contact,
                    privacyModeEnabled = privacyModeEnabled
                )
            }
        }
        
        addresses.forEach { walletAddress ->
            ObfuscatedAddress(
                address = walletAddress.address,
                contact = contact,
                walletAddress = walletAddress,
                privacyModeEnabled = privacyModeEnabled,
                onRevealRequest = onRevealRequest,
                onCopy = onCopy
            )
        }
    }
}