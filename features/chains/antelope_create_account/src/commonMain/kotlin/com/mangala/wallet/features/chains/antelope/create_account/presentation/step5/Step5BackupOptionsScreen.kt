package com.mangala.wallet.features.chains.antelope.create_account.presentation.step5

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.component.StepIndicator
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.getPlatform
import com.mangala.wallet.utils.PlatformType
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step6.VaultaBackupPrivateKeyScreen

class Step5BackupOptionsScreen(
    private val accountName: String,
    private val accountSuffix: String
) : BaseScreen<Step5BackupOptionsScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_BACKUP_OPTIONS
    override val screenClassName: String = Step5BackupOptionsScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): Step5BackupOptionsScreenModel {
        return getScreenModel<Step5BackupOptionsScreenModel> {
            parametersOf(
                accountName,
                accountSuffix
            )
        }
    }

    @Composable
    override fun ScreenContent(screenModel: Step5BackupOptionsScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val navigationState by screenModel.navigationState.collectAsStateMultiplatform()
        val isIOS = remember { getPlatform().type == PlatformType.IOS }
        var contentVisible by remember { mutableStateOf(false) }
        // Hide iCloud option for now (will be implemented later)
        val showICloudOption = false // TODO: Implement iCloud backup option
        
        // Handle navigation based on state
        LaunchedEffect(navigationState) {
            navigationState?.let { navState ->
                when (navState) {
                    is NavigationState.NavigateToHome -> {
                        val homeScreen = ScreenRegistry.get(SharedScreen.HomeScreen())
                        navigator.replaceAll(homeScreen)
                    }
                    is NavigationState.NavigateToSetupPin -> {
                        val setupPinScreen = ScreenRegistry.get(
                            SharedScreen.SetupPinScreen(
                                blockchainUid = navState.blockchainUid,
                                antelopeAccountName = navState.accountName,
                                pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_CONTINUE_HOME_SCREEN.name
                            )
                        )
                        screenModel.clearNavigationState()
                        navigator.push(setupPinScreen)
                    }
                }
                screenModel.clearNavigationState()
            }
        }
        
        // Set default selection based on platform
        LaunchedEffect(showICloudOption) {
            if (uiState.selectedOption == null) {
                screenModel.selectOption(
                    if (showICloudOption) BackupOption.ICLOUD else BackupOption.RECOVERY_PHRASE
                )
            }
            delay(100)
            contentVisible = true
        }
        
        OnboardingGradientBackground(
            circleBackgroundEnabled = true,
            afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar with progress dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { navigator.pop() }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.ArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    
                    // Step indicator (all 4 steps completed for backup options)
                    StepIndicator(
                        totalSteps = 4,
                        currentStep = 4
                    )
                    
                    Spacer(modifier = Modifier.size(40.dp)) // Balance the layout
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Header section with animation
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) + 
                            slideInVertically(
                                initialOffsetY = { it / 6 },
                                animationSpec = tween(600, delayMillis = 200)
                            )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Final Step: Save Your Access",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            letterSpacing = (-0.2).sp,
                            lineHeight = 28.sp,
                            fontFamily = getInterFontFamily()
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFFA5B4CB),
                                        fontSize = 14.sp,
                                        fontFamily = getInterFontFamily()
                                    )
                                ) {
                                    append("Your wallet is ready! Now let's make sure you never lose access to ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontFamily = getInterFontFamily()
                                    )
                                ) {
                                    append("$accountName$accountSuffix")
                                }
                            },
                            letterSpacing = (-0.14).sp,
                            lineHeight = 19.6.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Backup options with animation
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) + 
                            slideInVertically(
                                initialOffsetY = { it / 4 },
                                animationSpec = tween(600, delayMillis = 400)
                            )
                ) {
                    Column(
                        modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // iCloud Keychain option (iOS only - mocked for Android testing)
                    if (showICloudOption) {
                        BackupOptionCard(
                            title = "iCloud Keychain",
                            subtitle = "Automatic • Encrypted • Easy recovery",
                            isRecommended = true,
                            isSelected = uiState.selectedOption == BackupOption.ICLOUD,
                            onClick = { screenModel.selectOption(BackupOption.ICLOUD) },
                            icon = {
                                AppleIcon()
                            }
                        )
                    }
                    
                    // Manual Vaulta Keys Backup option
                    BackupOptionCard(
                        title = "Manual Backup",
                        subtitle = "Manual • Export Vaulta keys • Your responsibility",
                        isRecommended = false,
                        isSelected = uiState.selectedOption == BackupOption.RECOVERY_PHRASE,
                        onClick = { screenModel.selectOption(BackupOption.RECOVERY_PHRASE) },
                        icon = {
                            RecoveryPhraseIcon()
                        }
                    )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Warning section with animation
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 500)) + 
                            slideInVertically(
                                initialOffsetY = { it / 4 },
                                animationSpec = tween(600, delayMillis = 500)
                            )
                ) {
                    WarningSection(isIOS = isIOS)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Primary action button
                val buttonText = when (uiState.selectedOption) {
                    BackupOption.ICLOUD -> "Secure with iCloud"
                    BackupOption.RECOVERY_PHRASE -> "Backup Vaulta Keys"
                    null -> "Continue"
                }
                
                // Primary action button with animation
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 700))
                ) {
                    Column {
                        OnboardingButton(
                            text = buttonText,
                            onClick = { 
                                when (uiState.selectedOption) {
                                    BackupOption.RECOVERY_PHRASE -> {
                                        val vaultaBackupScreen = VaultaBackupPrivateKeyScreen(accountName, accountSuffix)
                                        navigator.push(vaultaBackupScreen)
                                    }
                                    BackupOption.ICLOUD -> {
                                        // TODO: Navigate to iCloud backup flow
                                        screenModel.onContinueClick()
                                    }
                                    null -> {
                                        // No option selected
                                    }
                                }
                            },
                            isPrimary = uiState.selectedOption != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                        
                        TextButton(
                            onClick = {
                                screenModel.onContinueWithoutBackup()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(top = 8.dp, bottom = 24.dp)
                                .height(52.dp)
                                .clip(RoundedCornerShape(1000.dp))
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.01f),
                                    shape = RoundedCornerShape(1000.dp)
                                ),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFFA5B4CB)
                            ),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                            shape = RoundedCornerShape(1000.dp)
                        ) {
                            Text(
                                text = "Continue with risk",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFA5B4CB),
                                textAlign = TextAlign.Center,
                                letterSpacing = (-0.14).sp,
                                lineHeight = 19.6.sp,
                                fontFamily = getInterFontFamily()
                            )
                        }
                    }
                }
            }
        }
    }

    override val isBottomBarVisible: Boolean = false
}

@Composable
private fun BackupOptionCard(
    title: String,
    subtitle: String,
    isRecommended: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF3B90FF),
            Color(0xFFC27DFF)
        )
    )
    
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            brush = gradientBrush,
                            shape = RoundedCornerShape(16.dp)
                        )
                    } else {
                        Modifier.border(
                            width = 1.dp,
                            color = Color(0xFF2A3E6C),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                )
                .clickable { onClick() }
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
                
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFF1F5F9),
                        fontFamily = getInterFontFamily(),
                        textAlign = TextAlign.Start
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFA5B4CB),
                        letterSpacing = (-0.12).sp,
                        fontFamily = getInterFontFamily(),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
        
        // Recommended badge
        if (isRecommended) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = (-11).dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(brush = gradientBrush)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Recommended",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    fontFamily = getInterFontFamily()
                )
            }
        }
    }
}

@Composable
private fun WarningSection(isIOS: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Warning",
            tint = Color(0xFFF9A207),
            modifier = Modifier.size(20.dp)
        )
        
        Text(
            text = if (isIOS) {
                "Without backup, lost access = lost funds. We recommend that you complete both backup methods to keep your crypto the safest."
            } else {
                "Without backup, lost access = lost funds."
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFF9A207),
            letterSpacing = (-0.12).sp,
            lineHeight = 16.8.sp,
            fontFamily = getInterFontFamily(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AppleIcon() {
    LocalImage(
        imageResource = MR.images.icloud_icon,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun RecoveryPhraseIcon() {
    LocalImage(
        imageResource = MR.images.recovery_phase_icon,
        modifier = Modifier.size(24.dp)
    )
}

