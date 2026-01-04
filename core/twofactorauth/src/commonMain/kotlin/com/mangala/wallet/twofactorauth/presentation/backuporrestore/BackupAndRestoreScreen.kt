package com.mangala.wallet.twofactorauth.presentation.backuporrestore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen

class BackupAndRestoreScreen : BaseScreen<BackupAndRestoreScreenModel>() {
    override val screenName: String = "Backup & Restore"
    override val screenClassName: String = "BackupAndRestoreScreen"

    @Composable
    override fun createScreenModel(): BackupAndRestoreScreenModel = getScreenModel()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ScreenContent(screenModel: BackupAndRestoreScreenModel) {
        val uiState by screenModel.uiState.collectAsState()
        var selectedTab by remember { mutableStateOf(BackupRestoreTab.BACKUP) }
        val scope = rememberCoroutineScope()

        var showPassword by remember { mutableStateOf(false) }
        var showSuccessMessage by remember { mutableStateOf(false) }

        LaunchedEffect(uiState.operationSuccess) {
            if (uiState.operationSuccess) {
                showSuccessMessage = true
                kotlinx.coroutines.delay(3000)
                showSuccessMessage = false
                screenModel.resetOperationState()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Backup & Restore") },
                    navigationIcon = {
                        IconButton(onClick = { onBackPressedCallback?.invoke() }) {
                            Icon(
                                imageVector = MangalaWalletPack.IcBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = remember { SnackbarHostState() }) { data ->
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            TextButton(onClick = { data.dismiss() }) {
                                Text("Dismiss")
                            }
                        }
                    ) {
                        Text(data.visuals.message)
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tab selector
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == BackupRestoreTab.BACKUP,
                        onClick = { selectedTab = BackupRestoreTab.BACKUP },
                        text = { Text("Backup") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Backup,
                                contentDescription = null
                            )
                        }
                    )

                    Tab(
                        selected = selectedTab == BackupRestoreTab.RESTORE,
                        onClick = { selectedTab = BackupRestoreTab.RESTORE },
                        text = { Text("Restore") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = null
                            )
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        BackupRestoreTab.BACKUP -> BackupContent(
                            password = uiState.password,
                            onPasswordChange = screenModel::setPassword,
                            passwordStrength = uiState.passwordStrength,
                            showPassword = showPassword,
                            onShowPasswordChange = { showPassword = it },
                            isOperationInProgress = uiState.isOperationInProgress,
                            onBackupClick = screenModel::createBackup,
                            backupData = uiState.backupData,
                            onCopyBackupClick = screenModel::copyBackupToClipboard,
                            onSaveFileClick = screenModel::saveBackupToFile,
                            onCloudBackupClick = screenModel::backupToCloud
                        )

                        BackupRestoreTab.RESTORE -> RestoreContent(
                            password = uiState.password,
                            onPasswordChange = screenModel::setPassword,
                            showPassword = showPassword,
                            onShowPasswordChange = { showPassword = it },
                            isOperationInProgress = uiState.isOperationInProgress,
                            backupText = uiState.backupInputText,
                            onBackupTextChange = screenModel::setBackupInputText,
                            onRestoreClick = screenModel::restoreFromBackup,
                            onPickFileClick = screenModel::pickBackupFile,
                            onScanQrClick = screenModel::scanQrCode
                        )
                    }

                    // Success message overlay
                    this@Column.AnimatedVisibility(
                        visible = showSuccessMessage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = when (selectedTab) {
                                        BackupRestoreTab.BACKUP -> "Backup created successfully"
                                        BackupRestoreTab.RESTORE -> "Restore completed successfully"
                                    },
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // Error message
                    this@Column.AnimatedVisibility(
                        visible = uiState.errorMessage != null,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = uiState.errorMessage ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackupContent(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordStrength: Float,
    showPassword: Boolean,
    onShowPasswordChange: (Boolean) -> Unit,
    isOperationInProgress: Boolean,
    onBackupClick: () -> Unit,
    backupData: String?,
    onCopyBackupClick: () -> Unit,
    onSaveFileClick: () -> Unit,
    onCloudBackupClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Create Encrypted Backup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "This will create an encrypted backup of your 2FA configuration including your secret key and backup codes.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Backup Password") },
                    placeholder = { Text("Enter a strong password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { onShowPasswordChange(!showPassword) }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (showPassword) "Hide password" else "Show password"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Password strength indicator
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Password Strength:",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = when {
                                passwordStrength < 0.3f -> "Weak"
                                passwordStrength < 0.7f -> "Medium"
                                else -> "Strong"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                passwordStrength < 0.3f -> MaterialTheme.colorScheme.error
                                passwordStrength < 0.7f -> Color(0xFFFFA000) // Amber
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = passwordStrength,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = when {
                            passwordStrength < 0.3f -> MaterialTheme.colorScheme.error
                            passwordStrength < 0.7f -> Color(0xFFFFA000) // Amber
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBackupClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = password.length >= 8 && !isOperationInProgress
                ) {
                    if (isOperationInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Create Backup")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Password must be at least 8 characters and include a mix of letters, numbers, and symbols for best security.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AnimatedVisibility(visible = backupData != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Backup Created Successfully",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your backup contains encrypted 2FA data. Protect this file and the password you used.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Backup data box with copy option
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = backupData?.take(20) + "..." + backupData?.takeLast(20),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(onClick = onCopyBackupClick) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy backup data"
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(
                                onClick = onSaveFileClick,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save File")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = onCloudBackupClick,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cloud Backup")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestoreContent(
    password: String,
    onPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onShowPasswordChange: (Boolean) -> Unit,
    isOperationInProgress: Boolean,
    backupText: String,
    onBackupTextChange: (String) -> Unit,
    onRestoreClick: () -> Unit,
    onPickFileClick: () -> Unit,
    onScanQrClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Restore from Backup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Import your encrypted 2FA backup to restore your configuration.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Backup input methods
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = onPickFileClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload File")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onScanQrClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scan QR")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Backup text input
                OutlinedTextField(
                    value = backupText,
                    onValueChange = onBackupTextChange,
                    label = { Text("Backup Data") },
                    placeholder = { Text("Paste backup data here") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password input
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Backup Password") },
                    placeholder = { Text("Enter backup password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { onShowPasswordChange(!showPassword) }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (showPassword) "Hide password" else "Show password"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onRestoreClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = backupText.isNotEmpty() && password.isNotEmpty() && !isOperationInProgress
                ) {
                    if (isOperationInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Restore Backup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = "Warning: Restoring a backup will replace your current 2FA configuration. Make sure you have access to your existing backup codes before proceeding.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}