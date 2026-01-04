package com.mangala.wallet.features.addressbook.presentation.contact.scan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.features.addressbook.presentation.contact.list.ContactListScreen
import com.mangala.wallet.features.addressbook.domain.validation.QrCodeValidator
import com.mangala.wallet.features.addressbook.domain.validation.ValidationResult
import com.mangala.wallet.features.addressbook.domain.validation.QrDataType
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScanToAddContactScreen(
    private val onBackClick: () -> Unit = {}
) : Screen, KoinComponent {

    @Composable
    override fun Content() {
        com.mangala.wallet.ui.LocalBottomNavigationVisibility.current.value = false
        
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ScanToAddContactScreenModel>()
        val qrCodeValidator: QrCodeValidator by inject()
        
        var isScanning by remember { mutableStateOf(true) }
        var scanResult by remember { mutableStateOf<String?>(null) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var parsedContactData by remember { mutableStateOf<ParsedContactData?>(null) }

        ScanToAddContactContent(
            isScanning = isScanning,
            scanResult = scanResult,
            errorMessage = errorMessage,
            parsedContactData = parsedContactData,
            onBackClick = { onBackClick(); navigator.pop() },
            onQrScanned = { qrData ->
                scanResult = qrData
                isScanning = false
                
                // Validate and parse QR data
                val validation = qrCodeValidator.validateQrData(qrData)
                when (validation) {
                    is ValidationResult.Valid -> {
                        val parsed = parseQrDataToContact(qrData, validation.type)
                        if (parsed != null) {
                            parsedContactData = parsed
                            errorMessage = null
                        } else {
                            errorMessage = "Could not parse contact data from QR code"
                        }
                    }
                    is ValidationResult.Invalid -> {
                        errorMessage = validation.reason
                    }
                }
            },
            onRescan = {
                isScanning = true
                scanResult = null
                errorMessage = null
                parsedContactData = null
            },
            onCreateContact = { contactData ->
                // Navigate to NewContactScreen with pre-filled data
                navigator.push(
                    com.mangala.wallet.features.addressbook.presentation.contact.ContactScreen(
                        contactId = null, // Create mode
                        prefilledName = contactData.name,
                        prefilledAddress = contactData.address,
                        prefilledBlockchain = contactData.blockchain,
                        onBackClick = navigator::pop,
                        onSaveSuccess = {
                            navigator.popUntil { it is ContactListScreen }
                        }
                    )
                )
            }
        )
    }
    
    private fun parseQrDataToContact(qrData: String, dataType: QrDataType): ParsedContactData? {
        return try {
            when (dataType) {
                QrDataType.Contact -> {
                    // Legacy format: "name|address|blockchain"
                    val parts = qrData.split("|")
                    if (parts.size >= 2) {
                        ParsedContactData(
                            name = parts[0],
                            address = parts[1],
                            blockchain = parts.getOrNull(2) ?: ""
                        )
                    } else null
                }
                QrDataType.Address -> {
                    if (qrData.startsWith("{")) {
                        // JSON format
                        val json = Json { ignoreUnknownKeys = true }
                        val jsonElement = json.parseToJsonElement(qrData)
                        val jsonObject = jsonElement.jsonObject
                        
                        ParsedContactData(
                            name = "", // Will be filled by user
                            address = jsonObject["address"]?.jsonPrimitive?.content ?: qrData,
                            blockchain = jsonObject["blockchain"]?.jsonPrimitive?.content ?: ""
                        )
                    } else {
                        // Direct address
                        ParsedContactData(
                            name = "", // Will be filled by user
                            address = qrData,
                            blockchain = detectBlockchainFromAddress(qrData)
                        )
                    }
                }
                else -> null // Group, Tag, Unknown not supported for contact creation
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun detectBlockchainFromAddress(address: String): String {
        return when {
            address.startsWith("0x") && address.length == 42 -> "ETH"
            address.startsWith("bc1") || address.startsWith("1") || address.startsWith("3") -> "BTC"
            address.startsWith("L") || address.startsWith("M") -> "LTC"
            else -> ""
        }
    }
}

@Composable
fun ScanToAddContactContent(
    isScanning: Boolean,
    scanResult: String?,
    errorMessage: String?,
    parsedContactData: ParsedContactData?,
    onBackClick: () -> Unit,
    onQrScanned: (String) -> Unit,
    onRescan: () -> Unit,
    onCreateContact: (ParsedContactData) -> Unit
) {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            MangalaWalletTopBar(
                text = "Scan QR to Add Contact",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isScanning) {
                // QR Scanner
                Text(
                    text = "Point your camera at a QR code containing contact information",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Text(
                        text = "QR Scanner placeholder - implement platform-specific scanner",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                // Show scan results
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                } else if (parsedContactData != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Contact Information Found",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            if (parsedContactData.name.isNotEmpty()) {
                                Text(
                                    text = "Name: ${parsedContactData.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            
                            Text(
                                text = "Address: ${parsedContactData.address}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            if (parsedContactData.blockchain.isNotEmpty()) {
                                Text(
                                    text = "Blockchain: ${parsedContactData.blockchain}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                            
                            Button(
                                onClick = { onCreateContact(parsedContactData) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Create Contact")
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onRescan,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Scan Again")
                    }
                    
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

data class ParsedContactData(
    val name: String,
    val address: String,
    val blockchain: String
)