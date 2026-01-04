package com.mangala.wallet.features.addressbook.presentation.contact.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.presentation.contact.recent.BlockchainIcons
import com.mangala.wallet.features.addressbook.presentation.components.BlockchainIconBox
import com.mangala.wallet.features.addressbook.utils.getImageResourceForSymbol
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.imageloader.LocalImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ShowQrScreenContent(
    isLoading: Boolean,
    qrDisplayData: QrDisplayData?,
    error: String? = null,
    qrCodeImage: Any? = null,
    onRetry: () -> Unit = {},
    onBackClick: () -> Unit,
    generateQrContent: (QrDisplayData) -> String
) {
    val clipboardManager = LocalClipboardManager.current
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show snackbar when status message changes
    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            statusMessage = null
        }
    }
    
    // Adjust colors based on light/dark theme
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else ColorsNew.background
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF1E1E1E) else ColorsNew.white
    val textPrimaryColor = if (isDarkTheme) Color.White else ColorsNew.primary_900
    val textSecondaryColor = if (isDarkTheme) Color.LightGray else ColorsNew.primary_600

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            MangalaWalletTopBar(
                text = "QR Code",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                MangalaCircularProgressIndicator()
            } else if (qrDisplayData != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackgroundColor
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Dynamic content based on data type
                        QrDataDisplayContent(
                            qrDisplayData = qrDisplayData,
                            textPrimaryColor = textPrimaryColor,
                            textSecondaryColor = textSecondaryColor,
                            onCopyAddress = { address ->
                                clipboardManager.setText(AnnotatedString(address))
                                statusMessage = "Address copied to clipboard"
                            }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // QR Code Display
                        QrCodeDisplay(
                            qrCodeImage = qrCodeImage,
                            modifier = Modifier.size(200.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Share buttons row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Share QR Image button (simplified for now)
                            IconButton(
                                onClick = { 
                                    val qrContent = generateQrContent(qrDisplayData)
                                    clipboardManager.setText(AnnotatedString(qrContent))
                                    statusMessage = "QR data copied to clipboard"
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ColorsNew.primary_600)
                                    .size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share QR Image",
                                    tint = Color.White
                                )
                            }
                            
                            // Copy QR Data button
                            IconButton(
                                onClick = { 
                                    val qrContent = generateQrContent(qrDisplayData)
                                    clipboardManager.setText(AnnotatedString(qrContent))
                                    statusMessage = "QR data copied to clipboard"
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ColorsNew.primary_300)
                                    .size(48.dp)
                            ) {
                                Icon(
                                    imageVector = MangalaWalletPack.Copy,
                                    contentDescription = "Copy QR Data",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error ?: "Data not found",
                        color = textPrimaryColor,
                        modifier = Modifier.padding(16.dp)
                    )
                    
                    if (error != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRetry,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QrDataDisplayContent(
    qrDisplayData: QrDisplayData,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    onCopyAddress: (String) -> Unit
) {
    when (val data = qrDisplayData.data) {
        is ContactModel -> {
            ContactQrContent(
                contact = data,
                textPrimaryColor = textPrimaryColor,
                textSecondaryColor = textSecondaryColor,
                onCopyAddress = onCopyAddress
            )
        }
        is WalletAddressEntity -> {
            AddressQrContent(
                address = data,
                qrDisplayData = qrDisplayData,
                textPrimaryColor = textPrimaryColor,
                textSecondaryColor = textSecondaryColor,
                onCopyAddress = onCopyAddress
            )
        }
        is GroupModel -> {
            GroupQrContent(
                group = data,
                textPrimaryColor = textPrimaryColor,
                textSecondaryColor = textSecondaryColor
            )
        }
        else -> {
            // Generic display
            GenericQrContent(
                qrDisplayData = qrDisplayData,
                textPrimaryColor = textPrimaryColor,
                textSecondaryColor = textSecondaryColor,
                onCopyAddress = onCopyAddress
            )
        }
    }
}

@Composable
private fun ContactQrContent(
    contact: ContactModel,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    onCopyAddress: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    BlockchainIcons.getBackgroundColorForSymbol(contact.blockchainSymbol)
                ),
            contentAlignment = Alignment.Center
        ) {
            val imageResource = getImageResourceForSymbol(contact.blockchainSymbol)
            if (imageResource != null) {
                LocalImage(
                    modifier = Modifier.size(32.dp),
                    imageResource = imageResource,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = contact.contactName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimaryColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Network
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BlockchainIconBox(
                symbol = contact.blockchainSymbol,
                iconPath = contact.blockchainIcon,
                size = 24.dp,
                iconSize = 16.dp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = contact.blockchainSymbol,
                fontSize = 14.sp,
                color = textSecondaryColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Address with copy button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = contact.walletAddress.take(10) + "..." + 
                      contact.walletAddress.takeLast(8),
                fontSize = 14.sp,
                color = textSecondaryColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            IconButton(onClick = { onCopyAddress(contact.walletAddress) }) {
                Icon(
                    imageVector = MangalaWalletPack.Copy,
                    contentDescription = "Copy Address",
                    tint = ColorsNew.primary_600,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AddressQrContent(
    address: WalletAddressEntity,
    qrDisplayData: QrDisplayData,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    onCopyAddress: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Address icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(ColorsNew.primary_100),
            contentAlignment = Alignment.Center
        ) {
            qrDisplayData.icon?.let { icon ->
                val imageResource = getImageResourceForSymbol(qrDisplayData.symbol ?: "")
                if (imageResource != null) {
                    LocalImage(
                        modifier = Modifier.size(32.dp),
                        imageResource = imageResource,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Alias
        Text(
            text = address.alias ?: "Wallet Address",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimaryColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Type
        Text(
            text = "${address.walletType?.uppercase() ?: "WALLET"} • ${qrDisplayData.symbol ?: "ADDRESS"}",
            fontSize = 14.sp,
            color = textSecondaryColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Address with copy button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = address.address.take(10) + "..." + 
                      address.address.takeLast(8),
                fontSize = 14.sp,
                color = textSecondaryColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            IconButton(onClick = { onCopyAddress(address.address) }) {
                Icon(
                    imageVector = MangalaWalletPack.Copy,
                    contentDescription = "Copy Address",
                    tint = ColorsNew.primary_600,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun GroupQrContent(
    group: GroupModel,
    textPrimaryColor: Color,
    textSecondaryColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Group icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    try {
                        val colorString = group.color ?: "#FF6B6B"
                        Color(colorString.removePrefix("#").toLong(16) or 0x00000000FF000000)
                    } catch (e: Exception) {
                        ColorsNew.primary_600
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = group.name.take(2).uppercase(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group name
        Text(
            text = group.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimaryColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        group.description?.let { desc ->
            Text(
                text = desc,
                fontSize = 14.sp,
                color = textSecondaryColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Stats
        Text(
            text = "${group.walletAddressCount} addresses • ${group.mainBlockchainSymbol ?: "Multi-chain"}",
            fontSize = 14.sp,
            color = textSecondaryColor
        )
    }
}

@Composable
private fun GenericQrContent(
    qrDisplayData: QrDisplayData,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    onCopyAddress: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Generic icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(ColorsNew.primary_100),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = qrDisplayData.title.take(2).uppercase(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ColorsNew.primary_600
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = qrDisplayData.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimaryColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = qrDisplayData.subtitle,
            fontSize = 14.sp,
            color = textSecondaryColor
        )

        qrDisplayData.address?.let { address ->
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = address.take(10) + "..." + address.takeLast(8),
                    fontSize = 14.sp,
                    color = textSecondaryColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                IconButton(onClick = { onCopyAddress(address) }) {
                    Icon(
                        imageVector = MangalaWalletPack.Copy,
                        contentDescription = "Copy Address",
                        tint = ColorsNew.primary_600,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QrCodeDisplay(
    qrCodeImage: Any?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (qrCodeImage) {
            is ImageBitmap -> {
                Image(
                    bitmap = qrCodeImage,
                    contentDescription = "QR Code",
                    modifier = Modifier.fillMaxSize()
                )
            }
            null -> {
                MangalaCircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
            }
            else -> {
                Text(
                    text = "Failed to generate QR",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }
    }
}