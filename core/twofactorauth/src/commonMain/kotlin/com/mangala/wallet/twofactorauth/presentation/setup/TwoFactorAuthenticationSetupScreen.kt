package com.mangala.wallet.twofactorauth.presentation.setup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.BackgroundDefaultQr
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcSave
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Security
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Telegram
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient
import kotlin.time.Duration.Companion.seconds

class TwoFactorAuthenticationSetupScreen(
    @Transient private val onSuccess: () -> Unit,
) : BaseScreen<TwoFactorAuthenticationSetupScreenModel>() {
    override val screenName: String = "2FA Setup"
    override val screenClassName: String = "TwoFactorAuthenticationSetupScreen"
    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): TwoFactorAuthenticationSetupScreenModel = getScreenModel {
        parametersOf(LocalNavigator, onSuccess)
    }

    @Composable
    override fun ScreenContent(screenModel: TwoFactorAuthenticationSetupScreenModel) {
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        TwoFactorSetupContent(
            uiState = uiState,
            onStepChanged = screenModel::onStepChanged,
            onCopySecret = screenModel::copySecretToClipboard,
            onSaveBackupCodes = screenModel::saveBackupCodesAsFile,
            onSendBackupByEmail = screenModel::sendBackupCodesByEmail,
            onOtpChange = screenModel::setCurrentEnteredOtp,
            onVerifyClick = screenModel::verifyInitialOtp,
            onSetupComplete = {
                screenModel.completeTwoFactorSetup()
                onSuccess()
                navigator.pop()
            },
            onBackClick = { navigator.pop() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoFactorSetupContent(
    uiState: TwoFactorSetupUiState,
    onStepChanged: (TwoFactorSetupStep) -> Unit,
    onCopySecret: () -> Unit,
    onSaveBackupCodes: () -> Unit,
    onSendBackupByEmail: () -> Unit,
    onOtpChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onSetupComplete: () -> Unit,
    onBackClick: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    var showCopiedToast by remember { mutableStateOf(false) }
    var showBackupCodesSavedToast by remember { mutableStateOf(false) }

    LaunchedEffect(showCopiedToast) {
        if (showCopiedToast) {
            kotlinx.coroutines.delay(2.seconds)
            showCopiedToast = false
        }
    }

    LaunchedEffect(showBackupCodesSavedToast) {
        if (showBackupCodesSavedToast) {
            kotlinx.coroutines.delay(2.seconds)
            showBackupCodesSavedToast = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getStepTitle(uiState.currentStep)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = MangalaWalletPack.IcBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StepIndicator(
                    currentStep = uiState.currentStep,
                    totalSteps = TwoFactorSetupStep.values().size
                )

                Spacer(modifier = Modifier.height(8.dp))

                when (uiState.currentStep) {
                    TwoFactorSetupStep.INTRODUCTION -> {
                        IntroductionContent(
                            onNextClick = {
                                onStepChanged(TwoFactorSetupStep.QR_AND_SECRET)
                            }
                        )
                    }
                    TwoFactorSetupStep.QR_AND_SECRET -> {
                        QrAndSecretContent(
                            qrUri = uiState.qrCodeUri,
                            secretText = uiState.totpSecret,
                            onCopyClick = {
                                onCopySecret()
                                showCopiedToast = true
                            },
                            onNextClick = {
                                onStepChanged(TwoFactorSetupStep.BACKUP_CODES)
                            }
                        )
                    }
                    TwoFactorSetupStep.BACKUP_CODES -> {
                        BackupCodesContent(
                            backupCodes = uiState.backupCodes,
                            onCopyAllClick = {
                                clipboardManager.setText(AnnotatedString(uiState.backupCodes.joinToString("\n")))
                                showCopiedToast = true
                            },
                            onSaveAsFileClick = {
                                onSaveBackupCodes()
                                showBackupCodesSavedToast = true
                            },
                            onSendByEmailClick = onSendBackupByEmail,
                            onNextClick = {
                                onStepChanged(TwoFactorSetupStep.CONFIRMATION)
                            }
                        )
                    }
                    TwoFactorSetupStep.CONFIRMATION -> {
                        ConfirmationContent(
                            isVerifying = uiState.isVerifying,
                            verificationError = uiState.verificationError,
                            currentOtpCode = uiState.currentEnteredOtp,
                            onOtpChange = onOtpChange,
                            onVerifyClick = onVerifyClick
                        )
                    }
                    TwoFactorSetupStep.COMPLETED -> {
                        CompletedContent(
                            onFinishClick = onSetupComplete
                        )
                    }
                }
            }

            // Toast messages
            AnimatedVisibility(
                visible = showCopiedToast,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Copied to clipboard",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            AnimatedVisibility(
                visible = showBackupCodesSavedToast,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Backup codes saved",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: TwoFactorSetupStep, totalSteps: Int) {
    val progress = (currentStep.ordinal + 1).toFloat() / totalSteps

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Step ${currentStep.ordinal + 1} of $totalSteps",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun IntroductionContent(onNextClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = MangalaWalletPack.Security,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Protect Your Assets with Two-Factor Authentication",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BenefitItem(
                    title = "Enhanced Security",
                    description = "Adds an extra layer of protection beyond your seed phrase"
                )

                BenefitItem(
                    title = "Prevent Unauthorized Access",
                    description = "Even if someone has your seed phrase, they still can't access your funds"
                )

                BenefitItem(
                    title = "Protection Against Phishing",
                    description = "Verification codes change every 30 seconds, making phishing attempts ineffective"
                )

                BenefitItem(
                    title = "Works Offline",
                    description = "No internet connection needed for verification"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "You'll need an authenticator app like Google Authenticator, Authy, or Microsoft Authenticator to complete setup.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Begin Setup")
        }
    }
}

@Composable
private fun BenefitItem(title: String, description: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 2.dp)
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun QrAndSecretContent(
    qrUri: String,
    secretText: String,
    onCopyClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Scan this QR code with your authenticator app",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // QR Code image - in a real app this would be generated from qrUri
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.White)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // This is a placeholder. In a real app, render the QR code
            // using a library like zxing-android-embedded
            Image(
                imageVector = MangalaWalletPack.BackgroundDefaultQr,
                contentDescription = "QR Code",
                modifier = Modifier.size(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Or enter this secret key manually:",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = secretText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onCopyClick) {
                    Icon(
                        imageVector = MangalaWalletPack.Copy,
                        contentDescription = "Copy to clipboard"
                    )
                }
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
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Never share this secret key with anyone! Anyone with this key can generate verification codes for your wallet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue to Backup Codes")
        }
    }
}

@Composable
private fun BackupCodesContent(
    backupCodes: List<String>,
    onCopyAllClick: () -> Unit,
    onSaveAsFileClick: () -> Unit,
    onSendByEmailClick: () -> Unit,
    onNextClick: () -> Unit
) {
    var hasConfirmedSaving by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Backup Codes",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "If you lose access to your authenticator app, you can use these backup codes to access your wallet. Each code can only be used once.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                backupCodes.forEach { code ->
                    Text(
                        text = code,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Save your backup codes in a secure location",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(onClick = onCopyAllClick) {
                Icon(
                    imageVector = MangalaWalletPack.Copy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy All")
            }

            OutlinedButton(onClick = onSaveAsFileClick) {
                Icon(
                    imageVector = MangalaWalletPack.IcSave,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save File")
            }

            OutlinedButton(onClick = onSendByEmailClick) {
                Icon(
                    imageVector = MangalaWalletPack.Telegram, // TODO: change to email icon
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Email")
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
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Without these backup codes, you may lose access to your wallet if you lose your authenticator app or device!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = hasConfirmedSaving,
                onCheckedChange = { hasConfirmedSaving = it }
            )
            Text(
                text = "I have saved my backup codes in a secure location",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = hasConfirmedSaving
        ) {
            Text("Continue to Verification")
        }
    }
}

@Composable
private fun ConfirmationContent(
    isVerifying: Boolean,
    verificationError: String?,
    currentOtpCode: String,
    onOtpChange: (String) -> Unit,
    onVerifyClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Verify Your Authenticator App",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter the 6-digit code from your authenticator app to verify the setup",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // OTP Input Field
        OutlinedTextField(
            value = currentOtpCode,
            onValueChange = { newValue ->
                if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                    onOtpChange(newValue)
                }
            },
            label = { Text("6-digit code") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = verificationError != null
        )

        AnimatedVisibility(visible = verificationError != null) {
            Text(
                text = verificationError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show a circular progress indicator while verifying
        if (isVerifying) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onVerifyClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = currentOtpCode.length == 6 && !isVerifying
        ) {
            Text("Verify and Enable 2FA")
        }
    }
}

@Composable
private fun CompletedContent(onFinishClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .padding(16.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Two-Factor Authentication Enabled!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your wallet is now protected with an additional layer of security. You'll need to enter a verification code for sensitive operations.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Remember:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                BulletPoint("Keep your authenticator app installed")
                BulletPoint("Store your backup codes in a secure location")
                BulletPoint("Don't share your secret key with anyone")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onFinishClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Finish")
        }
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun getStepTitle(step: TwoFactorSetupStep): String {
    return when (step) {
        TwoFactorSetupStep.INTRODUCTION -> "Two-Factor Authentication"
        TwoFactorSetupStep.QR_AND_SECRET -> "Scan QR Code"
        TwoFactorSetupStep.BACKUP_CODES -> "Backup Codes"
        TwoFactorSetupStep.CONFIRMATION -> "Verify Setup"
        TwoFactorSetupStep.COMPLETED -> "Setup Complete"
    }
}
data class TwoFactorSetupUiState(
    val currentStep: TwoFactorSetupStep = TwoFactorSetupStep.INTRODUCTION,
    val totpSecret: String = "",
    val qrCodeUri: String = "",
    val backupCodes: List<String> = emptyList(),
    val currentEnteredOtp: String = "",
    val isVerifying: Boolean = false,
    val verificationError: String? = null,
    val isSetupComplete: Boolean = false
)