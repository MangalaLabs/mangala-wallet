package com.mangala.wallet.features.chains.antelope.create_account.presentation.step6

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
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
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Hide
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Show
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.component.StepIndicator
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.getPlatform
import com.mangala.wallet.utils.PlatformType
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf

class VaultaBackupPrivateKeyScreen(
    private val accountName: String,
    private val accountSuffix: String
) : BaseScreen<VaultaBackupPrivateKeyScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_BACKUP_PRIVATE_KEY
    override val screenClassName: String = VaultaBackupPrivateKeyScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): VaultaBackupPrivateKeyScreenModel {
        return getScreenModel<VaultaBackupPrivateKeyScreenModel> {
            parametersOf(accountName + accountSuffix)
        }
    }

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: VaultaBackupPrivateKeyScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val navigationState by screenModel.navigationState.collectAsStateMultiplatform()
        val platformTexts = getVaultaBackupPlatformTexts()
        var contentVisible by remember { mutableStateOf(false) }
        
        // Handle navigation based on state
        LaunchedEffect(navigationState) {
            navigationState?.let { navState ->
                when (navState) {
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
        
        LaunchedEffect(Unit) {
            delay(100)
            contentVisible = true
        }

        OnboardingGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
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
                        
                        // Step indicator (all 4 steps completed for backup screen)
                        StepIndicator(
                            totalSteps = 4,
                            currentStep = 4
                        )
                        
                        Spacer(modifier = Modifier.size(40.dp)) // Balance the layout
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = platformTexts.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            letterSpacing = (-0.24).sp,
                            lineHeight = 33.6.sp,
                            fontFamily = getInterFontFamily()
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Subtitle with highlighted account name using buildAnnotatedString
                        val subtitleTemplate = platformTexts.subtitle
                        val beforeAccount = subtitleTemplate.substringBefore("{accountName}")
                        val afterAccount = subtitleTemplate.substringAfter("{accountName}")
                        
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFFA5B4CB),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = getInterFontFamily()
                                    )
                                ) {
                                    append(beforeAccount)
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = getInterFontFamily()
                                    )
                                ) {
                                    append("$accountName$accountSuffix")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFFA5B4CB),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = getInterFontFamily()
                                    )
                                ) {
                                    append(afterAccount)
                                }
                            },
                            textAlign = TextAlign.Start,
                            letterSpacing = (-0.16).sp,
                            lineHeight = 22.4.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Warning section
                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) + 
                                slideInVertically(
                                    initialOffsetY = { it / 6 },
                                    animationSpec = tween(600, delayMillis = 200)
                                )
                    ) {
                        WarningCard(platformTexts.warningText)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Keys section with Tabs
                    var selectedTabIndex by remember { mutableStateOf(0) }
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // Custom Tab Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Owner Tab
                            TabButton(
                                text = "Owner",
                                isSelected = selectedTabIndex == 0,
                                hasViewed = uiState.hasViewedOwnerKey,
                                onClick = { selectedTabIndex = 0 },
                                modifier = Modifier.weight(1f)
                            )
                            
                            // Active Tab
                            TabButton(
                                text = "Active",
                                isSelected = selectedTabIndex == 1,
                                hasViewed = uiState.hasViewedActiveKey,
                                onClick = { selectedTabIndex = 1 },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
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
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                // Content with padding
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                ) {
                                    when (selectedTabIndex) {
                                        0 -> {
                                            // Owner Permission
                                            KeyPairSection(
                                                permissionName = "Owner Permission",
                                                description = "Full control over your account",
                                                publicKey = uiState.ownerPublicKey,
                                                privateKey = uiState.ownerPrivateKey,
                                                isVisible = uiState.isOwnerKeyVisible,
                                                onToggleVisibility = { screenModel.toggleOwnerKeyVisibility() },
                                                onCopyPrivateKey = { screenModel.copyOwnerPrivateKey() },
                                                onCopyPublicKey = { screenModel.copyOwnerPublicKey() }
                                            )
                                        }
                                        1 -> {
                                            // Active Permission
                                            KeyPairSection(
                                                permissionName = "Active Permission",
                                                description = "Daily transactions and operations",
                                                publicKey = uiState.activePublicKey,
                                                privateKey = uiState.activePrivateKey,
                                                isVisible = uiState.isActiveKeyVisible,
                                                onToggleVisibility = { screenModel.toggleActiveKeyVisibility() },
                                                onCopyPrivateKey = { screenModel.copyActivePrivateKey() },
                                                onCopyPublicKey = { screenModel.copyActivePublicKey() }
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Reminder to view both keys
                                if (!uiState.hasViewedAllKeys) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "View both private keys to continue",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = Color(0xFFA5B4CB),
                                            fontFamily = getInterFontFamily()
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                
                                // Bottom completion button - moved inside scrollable area
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp)
                                ) {
                                    val buttonGradient = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF3B90FF),
                                            Color(0xFFC27DFF)
                                        )
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .clip(RoundedCornerShape(1000.dp))
                                            .background(
                                                if (uiState.hasViewedAllKeys) buttonGradient 
                                                else Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFF64748B),
                                                        Color(0xFF475569)
                                                    )
                                                )
                                            )
                                            .clickable(enabled = uiState.hasViewedAllKeys) { 
                                                screenModel.onBackupCompleted()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = platformTexts.completeButtonText,
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (uiState.hasViewedAllKeys) Color.White else Color(0xFFA5B4CB),
                                                textAlign = TextAlign.Center,
                                                letterSpacing = (-0.17).sp,
                                                lineHeight = 23.8.sp,
                                                fontFamily = getInterFontFamily()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Button is now inside the scrollable content area
            }
        }
    }
}

@Composable
private fun WarningCard(warningText: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF59E0B).copy(alpha = 0.1f),
                            Color(0xFFEAB308).copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                
                Text(
                    text = warningText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFF59E0B),
                    letterSpacing = (-0.14).sp,
                    lineHeight = 19.6.sp,
                    fontFamily = getInterFontFamily(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun KeyPairSection(
    permissionName: String,
    description: String,
    publicKey: String,
    privateKey: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onCopyPrivateKey: () -> Unit,
    onCopyPublicKey: () -> Unit
) {
    val composeUIWrapper = remember { ComposeUIWrapper() }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E293B).copy(alpha = 0.8f),
                            Color(0xFF334155).copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = permissionName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            letterSpacing = (-0.18).sp,
                            lineHeight = 25.2.sp,
                            fontFamily = getInterFontFamily()
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = description,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFA5B4CB),
                            letterSpacing = (-0.12).sp,
                            lineHeight = 16.8.sp,
                            fontFamily = getInterFontFamily()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Public Key
                KeyField(
                    label = "Public Key",
                    value = publicKey,
                    isSecret = false,
                    onCopy = onCopyPublicKey
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Private Key with QR Code
                Column {
                    KeyField(
                        label = "Private Key",
                        value = privateKey,
                        isSecret = true,
                        isVisible = isVisible,
                        onToggleVisibility = onToggleVisibility,
                        onCopy = onCopyPrivateKey
                    )
                    
                    if (privateKey.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // QR Code
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(16.dp)
                            ) {
                                if (isVisible) {
                                    composeUIWrapper.QRCodeImage(privateKey)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFFF1F5F9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Hidden",
                                            color = Color(0xFF64748B),
                                            fontSize = 14.sp,
                                            fontFamily = getInterFontFamily()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyField(
    label: String,
    value: String,
    isSecret: Boolean,
    isVisible: Boolean = true,
    onToggleVisibility: (() -> Unit)? = null,
    onCopy: () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFA5B4CB),
            letterSpacing = (-0.12).sp,
            fontFamily = getInterFontFamily()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F172A))
                .border(
                    width = 1.dp,
                    color = Color(0xFF334155),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSecret && !isVisible) "••••••••••••••••••••••••••••••••••••••••••••••••••••" else value,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (isSecret && !isVisible) Color(0xFF64748B) else Color(0xFFF1F5F9),
                    letterSpacing = if (isSecret && !isVisible) 2.sp else (-0.11).sp,
                    lineHeight = 15.4.sp,
                    fontFamily = if (isSecret && !isVisible) FontFamily.Default else getInterFontFamily(),
                    modifier = Modifier.weight(1f)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isSecret && onToggleVisibility != null) {
                        IconButton(
                            onClick = onToggleVisibility,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isVisible) MangalaWalletPack.Hide else MangalaWalletPack.Show,
                                contentDescription = if (isVisible) "Hide" else "Show",
                                tint = Color(0xFFA5B4CB),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.Copy,
                            contentDescription = "Copy",
                            tint = Color(0xFFA5B4CB),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    hasViewed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3B90FF).copy(alpha = 0.3f),
                            Color(0xFFC27DFF).copy(alpha = 0.3f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E293B).copy(alpha = 0.5f),
                            Color(0xFF334155).copy(alpha = 0.5f)
                        )
                    )
                }
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF3B90FF) else Color(0xFF334155),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFFA5B4CB),
                fontFamily = getInterFontFamily()
            )
            
            // Show checkmark if private key has been viewed
            if (hasViewed) {
                Text(
                    text = "✓",
                    fontSize = 12.sp,
                    color = Color(0xFF10B981)
                )
            }
        }
    }
}

data class VaultaBackupTexts(
    val title: String,
    val subtitle: String,
    val warningText: String,
    val completeButtonText: String
)

@Composable
fun getVaultaBackupPlatformTexts(): VaultaBackupTexts {
    return when (getPlatform().type) {
        PlatformType.ANDROID -> VaultaBackupTexts(
            title = "Backup Your Vaulta Keys 🔐",
            subtitle = "Secure your {accountName} account with these private keys. Each permission has different access levels.",
            warningText = "Store these keys safely! Anyone with access to these private keys can control your account. Never share them online or with others.",
            completeButtonText = "I've Secured My Keys ✅"
        )
        PlatformType.IOS -> VaultaBackupTexts(
            title = "Backup Private Keys",
            subtitle = "Your {accountName} account keys are shown below. Keep them secure.",
            warningText = "Important: Store these keys in a secure location. Anyone with these keys can access your account.",
            completeButtonText = "Continue"
        )
        PlatformType.DESKTOP -> VaultaBackupTexts(
            title = "Vaulta Account Key Backup",
            subtitle = "Account: {accountName} - Secure these private keys for account recovery.",
            warningText = "Security Notice: These private keys provide access to your Vaulta account. Store them securely offline.",
            completeButtonText = "Backup Complete"
        )
    }
}