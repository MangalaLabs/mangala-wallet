package com.mangala.wallet.twofactorauth.presentation.unlock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient
import kotlin.time.Duration.Companion.seconds

class Unlock2FaScreen(
    @Transient private val onUnlockSuccess: () -> Unit,
    @Transient private val onUnlockCancelled: () -> Unit
) : BaseScreen<Unlock2FaScreenModel>() {

    override val screenName: String = "2FA Verification"
    override val screenClassName: String = "Unlock2FaScreen"
    override val isBottomBarVisible: Boolean = false

    init {
        onBackPressedCallback = {
            onUnlockCancelled()
            true
        }
    }

    @Composable
    override fun createScreenModel(): Unlock2FaScreenModel = getScreenModel {
        parametersOf(onUnlockSuccess)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ScreenContent(screenModel: Unlock2FaScreenModel) {
        val uiState by screenModel.uiState.collectAsState()

        var isBackupCodeMode by remember { mutableStateOf(false) }

        // Timer for OTP expiration countdown
        var remainingSeconds by remember { mutableStateOf(30) }
        val timerProgress by animateFloatAsState(
            targetValue = remainingSeconds / 30f,
            label = "Timer Animation"
        )

        LaunchedEffect(Unit) {
            while (true) {
                delay(1.seconds)
                remainingSeconds = (remainingSeconds - 1).coerceAtLeast(0)

                if (remainingSeconds == 0) {
                    remainingSeconds = 30 // Reset timer
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Two-Factor Authentication") },
                    navigationIcon = {
                        IconButton(onClick = onUnlockCancelled) {
                            Icon(
                                imageVector = MangalaWalletPack.IcBack,
                                contentDescription = "Cancel"
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Security icon or image could go here

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = if (isBackupCodeMode) "Enter Backup Code" else "Enter Verification Code",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Please enter the code from your authenticator app to continue",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isBackupCodeMode) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = timerProgress,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "$remainingSeconds s",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // OTP Input Field
                    if (!isBackupCodeMode) {
                        OtpInputField(
                            otpText = uiState.currentEnteredOtp,
                            onOtpTextChange = screenModel::setCurrentEnteredOtp,
                            otpCount = 6,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = uiState.currentEnteredBackupCode,
                            onValueChange = screenModel::setCurrentEnteredBackupCode,
                            label = { Text("Backup Code") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = uiState.verificationError != null
                        )
                    }

                    AnimatedVisibility(visible = uiState.verificationError != null) {
                        Text(
                            text = uiState.verificationError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${uiState.remainingAttempts} attempts remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (uiState.remainingAttempts <= 2) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isBackupCodeMode) {
                                screenModel.verifyBackupCode()
                            } else {
                                screenModel.verifyOtp()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = if (isBackupCodeMode)
                            uiState.currentEnteredBackupCode.isNotEmpty() && !uiState.isVerifying
                        else
                            uiState.currentEnteredOtp.length == 6 && !uiState.isVerifying
                    ) {
                        if (uiState.isVerifying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Verify")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { isBackupCodeMode = !isBackupCodeMode }
                    ) {
                        Text(
                            text = if (isBackupCodeMode) "Use OTP code instead" else "Use backup code instead",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (!isBackupCodeMode) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = uiState.rememberDevice,
                                onCheckedChange = screenModel::setRememberDevice
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Remember this device",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Don't ask for verification on this device for 30 days",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
fun OtpInputField(
    otpText: String,
    onOtpTextChange: (String) -> Unit,
    otpCount: Int = 6,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = otpText,
        onValueChange = { newValue ->
            if (newValue.length <= otpCount && newValue.all { it.isDigit() }) {
                onOtpTextChange(newValue)
            }
        },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        placeholder = { Text("Enter ${otpCount}-digit code") },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        textStyle = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            letterSpacing = 8.sp
        )
    )
}