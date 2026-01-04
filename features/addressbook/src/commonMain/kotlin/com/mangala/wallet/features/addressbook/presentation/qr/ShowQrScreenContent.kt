package com.mangala.wallet.features.addressbook.presentation.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.features.addressbook.domain.qr.QrDisplayData
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator

/**
 * QR screen content using new architecture
 */
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
    
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        containerColor = ColorsNew.background,
        topBar = {
            QrCodeHeader(
                onBackClick = onBackClick
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ColorsNew.background)
        ) {
            when {
                isLoading -> {
                    MangalaCircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    ErrorState(
                        error = error,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                qrDisplayData != null -> {
                    QrContentDisplay(
                        qrDisplayData = qrDisplayData,
                        qrCodeImage = qrCodeImage,
                        onCopyContent = { content ->
                            clipboardManager.setText(AnnotatedString(content))
                            statusMessage = "QR data copied to clipboard"
                        },
                        generateQrContent = generateQrContent
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error loading QR code",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = ColorsNew.error_600
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            fontSize = 14.sp,
            color = ColorsNew.primary_500,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorsNew.primary_600
            )
        ) {
            Text("Retry")
        }
    }
}

@Composable
private fun QrContentDisplay(
    qrDisplayData: QrDisplayData,
    qrCodeImage: Any?,
    onCopyContent: (String) -> Unit,
    generateQrContent: (QrDisplayData) -> String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // QR Code Image
        if (qrCodeImage != null) {
            Card(
                modifier = Modifier.size(280.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: Display QR code image based on platform
                    Text(
                        text = "QR Code\n(Image placeholder)",
                        textAlign = TextAlign.Center,
                        color = ColorsNew.primary_500
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = qrDisplayData.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorsNew.primary_900,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = qrDisplayData.subtitle,
                    fontSize = 14.sp,
                    color = ColorsNew.primary_500,
                    textAlign = TextAlign.Center
                )
                
                qrDisplayData.primaryInfo?.let { info ->
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = info,
                        fontSize = 12.sp,
                        color = ColorsNew.primary_700,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(
                                ColorsNew.primary_100,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Copy button
            IconButton(
                onClick = { 
                    val qrContent = generateQrContent(qrDisplayData)
                    onCopyContent(qrContent)
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(ColorsNew.primary_600)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = Color.White
                )
            }
            
            // Share button  
            IconButton(
                onClick = { 
                    // TODO: Implement sharing
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(ColorsNew.primary_600)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun QrCodeHeader(
    onBackClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = ColorsNew.background)
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = MangalaWalletPack.IcBack,
                contentDescription = "Back",
                tint = ColorsNew.primary_900
            )
        }

        // Title centered between back and invisible button
        Text(
            text = "QRCode",
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            color = ColorsNew.primary_950,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        // Invisible IconButton to balance the layout (same size as back button)
        IconButton(
            onClick = { },
            enabled = false
        ) {
            // Empty icon for spacing
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}