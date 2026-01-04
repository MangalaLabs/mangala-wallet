package com.mangala.wallet.features.addressbook.presentation.privacy

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mangala.wallet.common.mokoresources.ColorsNew

/**
 * Privacy Mode Onboarding Dialog
 * 
 * Introduces users to privacy mode features and functionality.
 */
@Composable
fun PrivacyModeOnboardingDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onEnablePrivacyMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = ColorsNew.primary_600
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Privacy Mode",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Protect your sensitive wallet addresses with enhanced privacy controls.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Features list
                    PrivacyFeatureItem(
                        icon = Icons.Outlined.VisibilityOff,
                        title = "Hide Sensitive Addresses",
                        description = "Wallet addresses are automatically obfuscated for privacy"
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    PrivacyFeatureItem(
                        icon = Icons.Outlined.TouchApp,
                        title = "Tap to Reveal",
                        description = "Authenticate to temporarily reveal hidden addresses"
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    PrivacyFeatureItem(
                        icon = Icons.Outlined.Timer,
                        title = "Auto-Hide Timer",
                        description = "Revealed addresses automatically hide after 10 seconds"
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Not Now",
                                fontSize = 14.sp
                            )
                        }
                        
                        Button(
                            onClick = {
                                onEnablePrivacyMode()
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorsNew.primary_600
                            )
                        ) {
                            Text(
                                text = "Enable",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Privacy Feature Item Component
 * 
 * Used within onboarding to show individual privacy features.
 */
@Composable
private fun PrivacyFeatureItem(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = ColorsNew.primary_100,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = ColorsNew.primary_600
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

/**
 * Privacy Tips Card
 * 
 * A informational card that can be shown in settings or help sections.
 */
@Composable
fun PrivacyTipsCard(
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColorsNew.primary_50
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = ColorsNew.primary_200
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = ColorsNew.primary_600
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Privacy Tips",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorsNew.primary_900,
                    modifier = Modifier.weight(1f)
                )
                
                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Dismiss",
                            modifier = Modifier.size(16.dp),
                            tint = ColorsNew.primary_600
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            PrivacyTipItem(
                "Use privacy mode in public spaces to protect your wallet addresses"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            PrivacyTipItem(
                "Tap any hidden address to authenticate and reveal it temporarily"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            PrivacyTipItem(
                "Privacy mode persists across app sessions for consistent protection"
            )
        }
    }
}

/**
 * Individual Privacy Tip Item
 */
@Composable
private fun PrivacyTipItem(
    tip: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(
                    color = ColorsNew.primary_400,
                    shape = RoundedCornerShape(3.dp)
                )
                .padding(top = 6.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = tip,
            fontSize = 13.sp,
            color = ColorsNew.primary_700,
            lineHeight = 18.sp
        )
    }
}

/**
 * Quick Privacy Mode Toggle with Explanation
 * 
 * Shows current privacy state and allows quick toggle with context.
 */
@Composable
fun PrivacyModeQuickToggle(
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) ColorsNew.primary_50 else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Privacy Mode",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (isEnabled) {
                        "Sensitive addresses are hidden for privacy"
                    } else {
                        "All wallet addresses are visible"
                    },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ColorsNew.primary_600,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}