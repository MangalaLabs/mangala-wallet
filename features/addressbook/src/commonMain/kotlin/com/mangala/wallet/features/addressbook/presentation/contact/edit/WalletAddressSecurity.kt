package com.mangala.wallet.features.addressbook.presentation.contact.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithBlockchainModel

/**
 * Component to display wallet addresses with security options
 */
@Composable
fun WalletAddressSecurityList(
    walletAddresses: List<WalletAddressWithBlockchainModel>,
    onToggleSensitive: (String) -> Unit,
    onViewAddressDetails: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Wallet Addresses Security",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            walletAddresses.forEachIndexed { index, wallet ->
                WalletAddressSecurityItem(
                    wallet = wallet,
                    onToggleSensitive = { onToggleSensitive(wallet.walletAddress.id) },
                    onViewDetails = { onViewAddressDetails(wallet.walletAddress.id) }
                )
                
                if (index < walletAddresses.size - 1) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

/**
 * Individual wallet address security item
 */
@Composable
fun WalletAddressSecurityItem(
    wallet: WalletAddressWithBlockchainModel,
    onToggleSensitive: () -> Unit,
    onViewDetails: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Address alias and blockchain type
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Blockchain icon/badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = wallet.blockchainType.symbol,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Wallet alias and network
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = wallet.walletAddress.alias.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = wallet.blockchainType.symbol,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Sensitive toggle
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (wallet.walletAddress.isSensitive) 
                        Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = if (wallet.walletAddress.isSensitive) 
                        "Sensitive Address" else "Non-sensitive Address",
                    tint = if (wallet.walletAddress.isSensitive) 
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Switch(
                    checked = wallet.walletAddress.isSensitive,
                    onCheckedChange = { onToggleSensitive() }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Masked address with view details option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onViewDetails)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (wallet.walletAddress.isSensitive) {
                    maskAddress(wallet.walletAddress.address)
                } else {
                    wallet.walletAddress.address
                },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = if (wallet.walletAddress.isSensitive) 
                    Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = if (wallet.walletAddress.isSensitive) 
                    "Masked Address" else "Visible Address",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

/**
 * Utility function to mask sensitive address
 */
private fun maskAddress(address: String): String {
    if (address.length <= 10) return "•".repeat(address.length)
    
    val prefix = address.take(5)
    val suffix = address.takeLast(5)
    return "$prefix•••••$suffix"
}