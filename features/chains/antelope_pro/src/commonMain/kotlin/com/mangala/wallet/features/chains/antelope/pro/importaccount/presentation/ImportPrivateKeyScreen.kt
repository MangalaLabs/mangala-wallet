package com.mangala.wallet.features.chains.antelope.pro.importaccount.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Hide
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Show
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Scan
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Paste
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.GradientTermsCheckbox
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ImportPrivateKeyScreen : BaseScreen<ImportPrivateKeyScreenModel>(), KoinComponent {
    
    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_IMPORT_ACCOUNT_PRIVATE_KEY
    override val screenClassName: String = ImportPrivateKeyScreen::class.simpleName.orEmpty()
    
    private val scanQRCode: ScanQRCode by inject()
    
    @Composable
    override fun createScreenModel(): ImportPrivateKeyScreenModel {
        return getScreenModel<ImportPrivateKeyScreenModel>()
    }
    
    @Composable
    override fun ScreenContent(screenModel: ImportPrivateKeyScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val navigationState by screenModel.navigationState.collectAsStateMultiplatform()
        val clipboardManager = LocalClipboardManager.current
        val termsScreen = rememberScreen(SharedScreen.TermsAndPolicyScreen)
        
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
                                pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN.name
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
            screenModel.setContentVisible(true)
        }
        
        // Render different phases based on UI state
        when (val currentState = uiState) {
            is ImportPrivateKeyUiState.InputPhase, 
            is ImportPrivateKeyUiState.AccountsFound -> {
                val privateKey = when (currentState) {
                    is ImportPrivateKeyUiState.InputPhase -> currentState.privateKey
                    is ImportPrivateKeyUiState.AccountsFound -> currentState.privateKey
                    else -> "" // This branch will never be reached due to when expression
                }
                
                InputFormContent(
                    privateKey = privateKey,
                    validation = if (currentState is ImportPrivateKeyUiState.InputPhase) currentState.validation else null,
                    isValidating = if (currentState is ImportPrivateKeyUiState.InputPhase) currentState.isValidating else false,
                    error = when (currentState) {
                        is ImportPrivateKeyUiState.InputPhase -> currentState.error
                        is ImportPrivateKeyUiState.AccountsFound -> currentState.error
                        else -> null
                    },
                    accounts = if (currentState is ImportPrivateKeyUiState.AccountsFound) currentState.accounts else emptyList(),
                    hasActiveKey = if (currentState is ImportPrivateKeyUiState.AccountsFound) currentState.hasActiveKey else false,
                    hasOwnerKey = if (currentState is ImportPrivateKeyUiState.AccountsFound) currentState.hasOwnerKey else false,
                    contentVisible = uiState.contentVisible,
                    isKeyVisible = uiState.isKeyVisible,
                    onToggleVisibility = { screenModel.toggleKeyVisibility() },
                    isTermsAgreed = uiState.isTermsAgreed,
                    onTermsAgreedChange = { screenModel.setTermsAgreed(it) },
                    termsScreen = termsScreen,
                    clipboardManager = clipboardManager,
                    navigator = navigator,
                    screenModel = screenModel,
                    onImportClick = {
                        when (currentState) {
                            is ImportPrivateKeyUiState.InputPhase -> {
                                if (uiState.isTermsAgreed && currentState.validation.isValid()) {
                                    screenModel.onPrivateKeyChange(currentState.privateKey) // Trigger discovery
                                }
                            }
                            is ImportPrivateKeyUiState.AccountsFound -> {
                                if (uiState.isTermsAgreed) {
                                    screenModel.onImportAccount()
                                }
                            }
                            else -> {} // This branch will never be reached
                        }
                    },
                    importEnabled = when (currentState) {
                        is ImportPrivateKeyUiState.InputPhase -> 
                            uiState.isTermsAgreed && currentState.validation.isValid() && !currentState.isValidating
                        is ImportPrivateKeyUiState.AccountsFound -> 
                            uiState.isTermsAgreed
                        else -> false // This branch will never be reached
                    }
                )
            }
            is ImportPrivateKeyUiState.Importing -> {
                ImportingContent(
                    uiState = currentState,
                )
            }
            is ImportPrivateKeyUiState.AccountCreated -> {
                // This will be handled by navigation effect
                ImportingContent(
                    uiState = ImportPrivateKeyUiState.Importing(
                        privateKey = "",
                        accountName = currentState.accountName
                    ),
                )
            }
            is ImportPrivateKeyUiState.ImportError -> {
                ErrorContent(
                    uiState = currentState,
                    screenModel = screenModel
                )
            }
        }
    }
    
    @Composable
    private fun InputFormContent(
        privateKey: String,
        validation: PrivateKeyValidation?,
        isValidating: Boolean,
        error: String?,
        accounts: List<String>,
        hasActiveKey: Boolean,
        hasOwnerKey: Boolean,
        contentVisible: Boolean,
        isKeyVisible: Boolean,
        onToggleVisibility: () -> Unit,
        isTermsAgreed: Boolean,
        onTermsAgreedChange: (Boolean) -> Unit,
        termsScreen: cafe.adriel.voyager.core.screen.Screen,
        clipboardManager: ClipboardManager,
        navigator: cafe.adriel.voyager.navigator.Navigator,
        screenModel: ImportPrivateKeyScreenModel,
        onImportClick: () -> Unit,
        importEnabled: Boolean
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val hasFoundAccounts = accounts.isNotEmpty()
        
        OnboardingGradientBackground(
            circleBackgroundEnabled = true,
            afterBackgroundModifier = Modifier.safeDrawingPadding().imePadding()
        ) {
            MaxSizeColumn {
                MangalaWalletTopBarCenteredTitle(
                    title = "",
                    onBackClicked = navigator::pop
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(40.dp))
                    if (hasFoundAccounts) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Header Section with animation
                    ImportScreenHeader(
                        contentVisible = contentVisible,
                        foundAccountsCount = if (hasFoundAccounts) accounts.size else null
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Private Key Input Section with animation
                    PrivateKeyInputSection(
                        contentVisible = contentVisible,
                        privateKey = privateKey,
                        isKeyVisible = isKeyVisible,
                        hasActiveKey = hasActiveKey,
                        hasOwnerKey = hasOwnerKey,
                        showKeyTypeBadge = hasFoundAccounts,
                        onPrivateKeyChange = { screenModel.onPrivateKeyChange(it) },
                        onToggleVisibility = onToggleVisibility,
                        onPaste = {
                            clipboardManager.getText()?.let {
                                screenModel.onPrivateKeyChange(it.text)
                                keyboardController?.hide()
                            }
                        },
                        onClear = { screenModel.onPrivateKeyChange("") },
                        onScanQr = {
                            scanQRCode.scanQRCode(object : ScanQRCodeListener {
                                override fun onScanQRCodeResult(result: String) {
                                    screenModel.onPrivateKeyChange(result)
                                    keyboardController?.hide()
                                }
                            })
                        }
                    )
                    
                    // Show validation checklist only when not showing found accounts
                    if (!hasFoundAccounts && validation != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        AnimatedVisibility(
                            visible = contentVisible,
                            enter = fadeIn(animationSpec = tween(600, delayMillis = 600)) +
                                    slideInVertically(
                                        initialOffsetY = { it / 4 },
                                        animationSpec = tween(600, delayMillis = 600)
                                    )
                        ) {
                            ValidationChecklist(
                                privateKey = privateKey,
                                validation = validation,
                                isValidating = isValidating,
                                error = error,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(if (hasFoundAccounts) 32.dp else 24.dp))

                    // Found Accounts Section (only shown when accounts are found)
                    if (hasFoundAccounts) {
                        AnimatedVisibility(
                            visible = contentVisible,
                            enter = fadeIn(animationSpec = tween(800, delayMillis = 400)) +
                                    expandVertically(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        ),
                                        expandFrom = Alignment.Top
                                    )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            ) {
                                // Success indicator with animation
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AccountFoundIndicator()
                                }

                                // Account Labels with staggered animation
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    accounts.forEachIndexed { index, accountName ->
                                        AnimatedAccountCard(
                                            accountName = accountName,
                                            delayMillis = 600 + (index * 150)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    } else {
                        // Security Warning (only shown when no accounts found)
                        AnimatedVisibility(
                            visible = contentVisible,
                            enter = fadeIn(animationSpec = tween(600, delayMillis = 700)) +
                                    slideInVertically(
                                        initialOffsetY = { it / 4 },
                                        animationSpec = tween(600, delayMillis = 700)
                                    )
                        ) {
                            SecurityWarningCard(
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                TermsAndImportSection(
                    contentVisible = contentVisible,
                    isTermsAgreed = isTermsAgreed,
                    onTermsAgreedChange = onTermsAgreedChange,
                    onTermsClick = { navigator.push(termsScreen) },
                    onImportClick = onImportClick,
                    importEnabled = importEnabled,
                    delayMillis = if (hasFoundAccounts) 1000 else 800
                )
            }
        }
    }
    
    override val isBottomBarVisible: Boolean = false
}

@Composable
private fun PrivateKeyInputField(
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onPaste: () -> Unit,
    onClear: () -> Unit,
    onScanQr: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B))
            .border(
                width = 1.dp,
                color = Color(0xFF334155),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                visualTransformation = if (isVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = Color.White,
                    fontFamily = getInterFontFamily()
                ),
                cursorBrush = SolidColor(Color(0xFF3B90FF)),
                modifier = Modifier.weight(1f)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Paste icon (always visible for better UX)
                Icon(
                    imageVector = MangalaWalletPack.Paste,
                    contentDescription = "Paste",
                    tint = Color(0xFFA5B4CB),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onPaste() }
                )
                
                // 2. Visibility toggle (eye icon)
                Icon(
                    imageVector = if (isVisible) 
                        MangalaWalletPack.Hide 
                    else 
                        MangalaWalletPack.Show,
                    contentDescription = if (isVisible) "Hide" else "Show",
                    tint = Color(0xFFA5B4CB),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onToggleVisibility() }
                )
                
                // 3. QR Scanner (highlighted in blue)
                Icon(
                    imageVector = MangalaWalletPack.Scan,
                    contentDescription = "Scan QR",
                    tint = Color(0xFF3B90FF),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onScanQr() }
                )
                
                // Clear button (only visible when there's text)
                if (value.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = Color(0xFFA5B4CB),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onClear() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ValidationChecklist(
    privateKey: String,
    validation: PrivateKeyValidation,
    isValidating: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1D263E))
            .padding(12.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val isNotEmpty = privateKey.isNotEmpty()
        
        ValidationItem(
            isValid = validation.correctPrefix && isNotEmpty,
            text = "Starts with '5' or ${com.memtrip.eos.core.crypto.KeyType.K1.privateKeyPrefix}"
        )
        
        ValidationItem(
            isValid = validation.isValidWif && isNotEmpty,
            text = "Valid Private key format"
        )
        
        if (isValidating) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Discovering accounts...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFD1D1D1),
                    fontFamily = getInterFontFamily()
                )
            }
        }
        
        // Show error if any
        error?.let { errorMessage ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFEF4444),
                    fontFamily = getInterFontFamily()
                )
            }
        }
    }
}

@Composable
private fun ValidationItem(
    isValid: Boolean,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.size(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = if (isValid) "Valid" else "Invalid",
                tint = if (isValid) Color(0xFFC27DFF) else Color(0xFF6B7280),
                modifier = Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = if (isValid) Color.White else Color(0xFF6B7280),
            fontFamily = getInterFontFamily()
        )
    }
}

@Composable
private fun LoadingIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Canvas(
        modifier = Modifier
            .size(16.dp)
            .rotate(rotation)
    ) {
        drawCircle(
            color = Color(0xFFA5B4CB),
            radius = size.minDimension / 2,
            style = Stroke(width = 1.5.dp.toPx())
        )
        
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFF3B90FF),
                    Color(0xFFA5B4CB),
                    Color(0xFF3B90FF)
                )
            ),
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}


@Composable
private fun SecurityWarningCard(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFEF3C7).copy(alpha = 0.1f))
            .border(
                width = 1.dp,
                color = Color(0xFFFBBF24).copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Warning",
            tint = Color(0xFFFBBF24),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = "Never share your private key with anyone. Anyone with access to your private key can control your account.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFFEF8C8),
            lineHeight = 16.8.sp,
            fontFamily = getInterFontFamily()
        )
    }
}

@Composable
private fun ImportingContent(
    uiState: ImportPrivateKeyUiState.Importing,
) {
    OnboardingGradientBackground(
        circleBackgroundEnabled = true,
        afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoadingIndicator()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Importing Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Setting up ${uiState.accountName}...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFA5B4CB),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorContent(
    uiState: ImportPrivateKeyUiState.ImportError,
    screenModel: ImportPrivateKeyScreenModel
) {
    OnboardingGradientBackground(
        circleBackgroundEnabled = true,
        afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Import Failed",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = uiState.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFA5B4CB),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            MangalaGradientButton(
                label = "Try Again",
                onClick = { screenModel.onBackToInput() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun KeyTypeBadge(
    hasActiveKey: Boolean,
    hasOwnerKey: Boolean
) {
    val keyTypeText = when {
        hasActiveKey && hasOwnerKey -> "Active & Owner Key"
        hasActiveKey -> "Active Key"
        hasOwnerKey -> "Owner Key"
        else -> return // Don't show badge if no key type detected
    }
    
    val badgeColor = when {
        hasActiveKey && hasOwnerKey -> Color(0xFF8B5CF6) // Purple for both
        hasActiveKey -> Color(0xFF10B981) // Green for active
        hasOwnerKey -> Color(0xFF3B90FF) // Blue for owner
        else -> Color(0xFF64748B)
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(badgeColor.copy(alpha = 0.1f))
            .border(
                width = 1.dp,
                color = badgeColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Detected",
                tint = badgeColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = keyTypeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = badgeColor,
                fontFamily = getInterFontFamily()
            )
        }
    }
}

@Composable
private fun AccountFoundIndicator() {
    var animationPlayed by remember { mutableStateOf(false) }
    
    val infiniteTransition = rememberInfiniteTransition()
    val scale by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    LaunchedEffect(Unit) {
        animationPlayed = true
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp)
    ) {
        // Glow effect
        Canvas(
            modifier = Modifier
                .size(80.dp)
                .scale(scale)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3B90FF).copy(alpha = glowAlpha),
                        Color(0xFF3B90FF).copy(alpha = 0f)
                    ),
                    radius = size.minDimension / 2
                )
            )
        }
        
        // Success icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3B90FF),
                            Color(0xFFC27DFF)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ImportScreenHeader(
    contentVisible: Boolean,
    foundAccountsCount: Int? = null
) {
    AnimatedVisibility(
        visible = contentVisible,
        enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                slideInVertically(
                    initialOffsetY = { it / 6 },
                    animationSpec = tween(600, delayMillis = 200)
                )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Vaulta Icon
            LocalImage(
                imageResource = MR.images.vaulta_account_name_default,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Import Existing Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.2).sp,
                lineHeight = 28.sp,
                fontFamily = getInterFontFamily()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (foundAccountsCount != null) {
                    "Found $foundAccountsCount account(s) for this private key"
                } else {
                    "Enter your private key to access your Vaulta account"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFA5B4CB),
                textAlign = TextAlign.Center,
                letterSpacing = (-0.14).sp,
                lineHeight = 19.6.sp,
                fontFamily = getInterFontFamily(),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
private fun PrivateKeyInputSection(
    contentVisible: Boolean,
    privateKey: String,
    isKeyVisible: Boolean,
    hasActiveKey: Boolean = false,
    hasOwnerKey: Boolean = false,
    showKeyTypeBadge: Boolean = false,
    onPrivateKeyChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onPaste: () -> Unit,
    onClear: () -> Unit,
    onScanQr: () -> Unit
) {
    AnimatedVisibility(
        visible = contentVisible,
        enter = fadeIn(animationSpec = tween(600, delayMillis = 600)) +
                slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(600, delayMillis = 600)
                )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Label with Key Type Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Private key",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFF1F5F9),
                        fontFamily = getInterFontFamily()
                    )
                    Text(
                        text = "*",
                        fontSize = 13.sp,
                        color = Color(0xFFEF4444),
                        fontFamily = getInterFontFamily()
                    )
                }

                // Key Type Badge (showing detected key types)
                if (showKeyTypeBadge) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(300)) +
                                scaleIn(animationSpec = tween(300))
                    ) {
                        KeyTypeBadge(
                            hasActiveKey = hasActiveKey,
                            hasOwnerKey = hasOwnerKey
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Field
            PrivateKeyInputField(
                value = privateKey,
                onValueChange = onPrivateKeyChange,
                isVisible = isKeyVisible,
                onToggleVisibility = onToggleVisibility,
                onPaste = onPaste,
                onClear = onClear,
                onScanQr = onScanQr
            )
        }
    }
}

@Composable
private fun TermsAndImportSection(
    contentVisible: Boolean,
    isTermsAgreed: Boolean,
    onTermsAgreedChange: (Boolean) -> Unit,
    onTermsClick: () -> Unit,
    onImportClick: () -> Unit,
    importEnabled: Boolean,
    delayMillis: Int = 800
) {
    AnimatedVisibility(
        visible = contentVisible,
        enter = fadeIn(animationSpec = tween(600, delayMillis = delayMillis))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GradientTermsCheckbox(
                isChecked = isTermsAgreed,
                onCheckedChange = onTermsAgreedChange,
                onTermsClick = onTermsClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            MangalaGradientButton(
                label = "Import Account",
                onClick = onImportClick,
                enabled = importEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AnimatedAccountCard(
    accountName: String,
    delayMillis: Int
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) + 
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E293B),
                            Color(0xFF334155).copy(alpha = 0.5f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3B90FF).copy(alpha = 0.6f),
                            Color(0xFFC27DFF).copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Account Discovered",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFA5B4CB),
                        fontFamily = getInterFontFamily()
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = accountName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontFamily = getInterFontFamily()
                    )
                }
                
                // Animated checkmark
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B90FF).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Ready",
                        tint = Color(0xFF3B90FF),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}