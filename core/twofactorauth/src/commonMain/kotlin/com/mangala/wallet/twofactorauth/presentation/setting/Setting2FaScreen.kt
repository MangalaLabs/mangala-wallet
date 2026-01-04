package com.mangala.wallet.twofactorauth.presentation.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.GppBad
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HelpCenter
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Security
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen

class Setting2FaScreen : BaseScreen<Setting2FaScreenModel>() {

    override val screenName: String = "2FA Settings"
    override val screenClassName: String = "Setting2FaScreen"

    @Composable
    override fun createScreenModel(): Setting2FaScreenModel = getScreenModel()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ScreenContent(screenModel: Setting2FaScreenModel) {
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("2FA Settings") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TwoFactorStatusCard(enabled = uiState.is2faEnabled)
                }

                item {
                    SettingsSectionHeader(title = "Backup and Recovery")

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            SettingsItem(
                                icon = Icons.Outlined.Backup,
                                title = "Backup and Restore",
                                subtitle = "Export or import your 2FA configuration",
                                onClick = { screenModel.navigateToBackupRestore() }
                            )

                            Divider()

                            SettingsItem(
                                icon = Icons.Outlined.VpnKey,
                                title = "Backup Codes",
                                subtitle = "View or regenerate your backup codes",
                                onClick = { screenModel.navigateToBackupCodes() }
                            )
                        }
                    }
                }

                item {
                    SettingsSectionHeader(title = "Help")

                    Card(modifier = Modifier.fillMaxWidth()) {
                        SettingsItem(
                            icon = MangalaWalletPack.HelpCenter,
                            title = "Help & FAQ",
                            subtitle = "Learn how to use 2FA and troubleshoot issues",
                            onClick = { screenModel.navigateToHelp() }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { screenModel.showDisableDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.is2faEnabled
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.Security,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Disable 2FA")
                    }
                }
            }
        }

        // Show disable confirmation dialog if needed
        if (uiState.showDisableDialog) {
            var confirmationCode by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { screenModel.hideDisableDialog() },
                title = { Text("Disable 2FA?") },
                text = {
                    Column {
                        Text("Disabling two-factor authentication will make your account less secure. Enter your 2FA code to confirm.")

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = confirmationCode,
                            onValueChange = {
                                confirmationCode = it
                                screenModel.setDisableConfirmationCode(it)
                            },
                            label = { Text("Verification Code") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                        )

                        // Show error if there is one
                        uiState.disableResult?.let {
                            if (!it.startsWith("2FA has been successfully")) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            screenModel.disable2FA()
                            if (uiState.disableResult?.startsWith("2FA has been successfully") == true) {
                                screenModel.hideDisableDialog()
                            }
                        },
                        enabled = confirmationCode.length >= 6
                    ) {
                        Text(
                            "Disable",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { screenModel.hideDisableDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun TwoFactorStatusCard(enabled: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (enabled) MangalaWalletPack.Security else Icons.Outlined.GppBad,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (enabled) "2FA is Enabled" else "2FA is Disabled",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (enabled)
                        "Your wallet is protected with Two-Factor Authentication"
                    else
                        "Enable 2FA to add an extra layer of security",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}