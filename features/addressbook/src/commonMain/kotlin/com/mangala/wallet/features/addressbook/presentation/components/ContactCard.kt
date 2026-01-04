package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.domain.model.ContactInfo
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.DocumentCopy
import com.mangala.wallet.features.addressbook.icon.contacticon.Qrcode
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarIcon
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.clipEntryOf
import kotlinx.coroutines.launch

data class ContactCardStyle(
    val containerColor: Color,
    val borderStroke: BorderStroke? = null,
    val shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    val padding: Dp = 12.dp,
    val showActionIcon: Boolean = true,
    val showCopyAndQrActions: Boolean = false,
    val nameColor: Color,
    val detailsColor: Color,
    val nameStyle: TextStyle,
    val detailsStyle: TextStyle
)

@Composable
fun ContactCard(
    contact: ContactInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ContactCardStyle,
    blockchainNetwork: String? = null,
    blockchainAddress: String? = null,
    onCopyAddress: ((String) -> Unit)? = null,
) {
    val localClipboard = LocalClipboard.current
    val localCoroutineScope = rememberCoroutineScope()
    val navigator = LocalNavigator.currentOrThrow
    val blockchainNetworkData = remember(blockchainNetwork) { 
        blockchainNetwork?.let { 
            BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true)
                .find { it.name == blockchainNetwork } 
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .let { cardModifier ->
                style.borderStroke?.let { borderStroke ->
                    cardModifier.border(borderStroke, style.shape)
                } ?: cardModifier
            },
        colors = CardDefaults.cardColors(
            containerColor = style.containerColor
        ),
        shape = style.shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(style.padding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Contact avatar
            AvatarIcon(
                name = contact.name,
                iconString = null,
                size = 40.dp
            )
            
            // Contact details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = contact.name,
                    style = style.nameStyle,
                    fontWeight = FontWeight.Medium,
                    color = style.nameColor
                )
                
                // Show blockchain address if provided, otherwise show notes
                val displayText = blockchainAddress?.let { formatAddressOrAccountName(it) } ?: contact.notes
                displayText?.let { text ->
                    Text(
                        text = text,
                        style = style.detailsStyle,
                        color = style.detailsColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                blockchainNetworkData?.let {
                    NetworkBadge(network = blockchainNetworkData)
                }
            }
            
            // Action buttons
            if (style.showCopyAndQrActions && onCopyAddress != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.XTINY),
                ) {
                    IconButton(
                        onClick = {
                            localCoroutineScope.launch {
                                val addressToCopy = blockchainAddress ?: contact.addresses.firstOrNull()?.address
                                addressToCopy?.let { address ->
                                    localClipboard.setClipEntry(clipEntryOf(address))
                                    onCopyAddress(address)
                                }
                            }
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = ContactIcon.DocumentCopy,
                            contentDescription = "Copy Address",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            val addressToShow = blockchainAddress ?: contact.addresses.firstOrNull()?.address
                            addressToShow?.let { address ->
                                val networkType = blockchainNetworkData?.blockchainType?.networkType ?: NetworkType.OTHER

                                val screen = ScreenRegistry.get(
                                    SharedScreen.ReceiveTokenScreen(
                                        accountId = null,
                                        address = address,
                                        networkType = networkType,
                                        initialBlockchainUid = blockchainNetworkData?.blockChainUid
                                    )
                                )
                                navigator.push(screen)
                            }
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = ContactIcon.Qrcode,
                            contentDescription = "QR Code",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else if (style.showActionIcon) {
                // Default action icon
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View contact",
                    tint = style.detailsColor
                )
            }
        }
    }
}

@Composable
private fun NetworkBadge(network: BlockchainNetworkData) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFBF0D6))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        network.localImage?.let {
            LocalImage(Modifier.size(12.dp), imageResource = it)
        }
        Text(
            text = network.name,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFF3BC36)
        )
    }
}

private fun formatAddressOrAccountName(value: String): String {
    return when {
        value.length <= 12 -> value
        value.length > 12 -> "${value.take(6)}...${value.takeLast(4)}"
        else -> value
    }
}

// Predefined styles for common use cases
object ContactCardStyles {
    @Composable
    fun conversationUi() = ContactCardStyle(
        containerColor = Color.Transparent,
        borderStroke = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF227BFF),
                    Color(0xFF1C8DF9),
                    Color(0xFFB988EE),
                    Color(0xFFEE4D5D)
                )
            )
        ),
        shape = RoundedCornerShape(CornerRadius.Medium),
        padding = 16.dp,
        showActionIcon = false,
        showCopyAndQrActions = true,
        nameColor = MaterialTheme.mangalaColors.textPrimary,
        detailsColor = MaterialTheme.mangalaColors.textSecondary,
        nameStyle = MangalaTypography.Size14Medium(),
        detailsStyle = MangalaTypography.Size14Regular()
    )
    
    @Composable
    fun confirmationRenderer() = ContactCardStyle(
        containerColor = Color.Transparent,
        borderStroke = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF227BFF),
                    Color(0xFF1C8DF9),
                    Color(0xFFB988EE),
                    Color(0xFFEE4D5D)
                )
            )
        ),
        shape = RoundedCornerShape(CornerRadius.Medium),
        padding = 16.dp,
        showActionIcon = false,
        showCopyAndQrActions = true,
        nameColor = MaterialTheme.mangalaColors.textPrimary,
        detailsColor = MaterialTheme.mangalaColors.textSecondary,
        nameStyle = MangalaTypography.Size14Medium(),
        detailsStyle = MangalaTypography.Size14Regular()
    )
}