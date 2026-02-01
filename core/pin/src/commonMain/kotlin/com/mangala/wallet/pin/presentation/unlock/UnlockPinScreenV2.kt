package com.mangala.wallet.pin.presentation.unlock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.biometry.presentation.BiometryByDevice
import com.mangala.wallet.biometry.presentation.BiometryState
import com.mangala.wallet.biometry.presentation.IBiometryScreenModel
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.pin.presentation.base.*
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.*
import com.mangala.wallet.ui.SharedScreen.UnlockPinScreen.Companion.CONFIRM_DAPP
import com.mangala.wallet.ui.SharedScreen.UnlockPinScreen.Companion.ENABLE_BIOMETRY
import com.mangala.wallet.ui.SharedScreen.UnlockPinScreen.Companion.OPEN_APP
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.navigation.BackHandler
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.ISystemInfoManager
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import com.mangala.wallet.pin.di.PIN_UNLOCK_CALLBACKS
import kotlin.jvm.Transient

class UnlockPinScreenV2(
    private val unlockPinCase: Int,
    private val antelopeAccountName: String?,
    @Transient val onUnlockSuccess: (() -> Unit)? = null,
    @Transient private val unlockPinCallback: ((Boolean) -> Unit)? = null
) : BaseScreen<UnlockPinScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.UNLOCK_PIN
    override val screenClassName: String = UnlockPinScreenV2::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): UnlockPinScreenModel {
        return if (onUnlockSuccess != null) {
            // V2: callback-based approach
            val callbacks = object : PinUnlockCallbacks {
                override fun onSuccess() = onUnlockSuccess.invoke()
                override fun onError(error: String) {}
                override fun onLocked(unlockTime: String, remainingTime: String) {}
                override fun onRateLimited(retryAfterSeconds: Long) {}
                override fun onCancel() {}
            }
            getScreenModel(
                qualifier = named(PIN_UNLOCK_CALLBACKS),
                parameters = { parametersOf(callbacks, true) }
            )
        } else {
            // V1: pinCase-based approach (for OPEN_APP, etc.)
            getScreenModel(parameters = {
                parametersOf(unlockPinCase)
            })
        }
    }

    @Composable
    @OptIn(ExperimentalComposeUiApi::class)
    override fun ScreenContent(screenModel: UnlockPinScreenModel) {
        val biometryScreenModel = get<IBiometryScreenModel>()
        val systemInfoManager = get<ISystemInfoManager>()

        val keyboardController = LocalSoftwareKeyboardController.current
        val globalNavigator = LocalGlobalNavigator.current
        val navigator = LocalNavigator.currentOrThrow

        screenModel.resetPinScreenFlowState()

        var visible by remember { mutableStateOf(false) }
        var hasCompletedSuccessfully by remember { mutableStateOf(false) }

        val titleBiometry = MR.strings.title_request_biometry_sign_in.desc().localized()
        val reasonBiometry = MR.strings.title_request_biometry_reason.desc().localized()
        val buttonBiometry = MR.strings.title_request_biometry_button.desc().localized()
        
        LifecycleEffect(
            onStarted = {
                screenModel.onStarted()
                val isDeviceSecure = systemInfoManager.isDeviceSecure()
                if (isDeviceSecure) {
                    screenModel.checkPinIsSetUp()
                } else {
                    screenModel.showLockScreen()
                }

                if (biometryScreenModel.isBiometricAvailable() && (biometryScreenModel.enableBiometric() || unlockPinCase == ENABLE_BIOMETRY) && isDeviceSecure) {
                    biometryScreenModel.resetBiometryState()
                    if (biometryScreenModel.bioMetricByDevice() == BiometryByDevice.ANDROID_FINGERPRINT || biometryScreenModel.bioMetricByDevice() == BiometryByDevice.ANDROID_FACE_ID) {
                        //Add delay for android
                        biometryScreenModel.tryToAuth(titleBiometry, "", buttonBiometry)
                    } else {
                        biometryScreenModel.tryToAuth(titleBiometry, reasonBiometry, buttonBiometry)
                    }
                }
            },
            onDisposed = {
                // Clean up on disposal to prevent memory leaks
                screenModel.onDispose()
                biometryScreenModel.resetBiometryState()
                // Cancel any pending navigation callbacks only if not completed successfully
                if (!hasCompletedSuccessfully) {
                    // Log for monitoring in case of unexpected behavior
                    try {
                        unlockPinCallback?.invoke(false)
                    } catch (e: Exception) {
                        println("[UnlockPinScreenV2] Error invoking cancel callback: ${e.message}")
                    }
                }
            }
        )

        LaunchedEffect(true) {
            keyboardController?.hide()
        }

        val clickKeyPadBiometry by screenModel.keyPadClickBiometry.collectAsStateMultiplatform()
        if(clickKeyPadBiometry){
            screenModel.resetKeyPadClickBiometry()
            if (biometryScreenModel.isBiometricAvailable() && biometryScreenModel.enableBiometric()) {
                biometryScreenModel.resetBiometryState()
                if(biometryScreenModel.bioMetricByDevice() == BiometryByDevice.ANDROID_FINGERPRINT || biometryScreenModel.bioMetricByDevice() == BiometryByDevice.ANDROID_FACE_ID){
                    biometryScreenModel.tryToAuth(titleBiometry, "", buttonBiometry)
                }else{
                    biometryScreenModel.tryToAuth(titleBiometry, reasonBiometry, buttonBiometry)
                }
            }
        }

        if(unlockPinCase == CONFIRM_DAPP){
            visible = true
        }

        if(biometryScreenModel.isBiometricAvailable() && (biometryScreenModel.enableBiometric() || unlockPinCase == ENABLE_BIOMETRY)) {
            val enableBiometry by biometryScreenModel.enableBiometry.collectAsStateMultiplatform()
            if (enableBiometry == BiometryState.SUCCESS) {
                screenModel.navigateScreen()
            } else if (enableBiometry == BiometryState.FAIL) {
                if (unlockPinCase == ENABLE_BIOMETRY) {
                    LaunchedEffect(true) {
                        navigator.pop()
                    }
                } else {
                    visible = true
                }
            }
        }else{
            CoroutineScope(Dispatchers.Default).launch {
                delay(500)
                visible = true
            }
        }
        
        // Add BackHandler for cases where user should be able to cancel
        if (unlockPinCase == SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION ||
            unlockPinCase == SharedScreen.UnlockPinScreen.CONFIRM_DAPP) {
            BackHandler { _ ->
                println("[UnlockPinScreenV2] BackHandler triggered for case: $unlockPinCase")
                // Invoke callback with false to indicate cancellation
                try {
                    unlockPinCallback?.invoke(false)
                } catch (e: Exception) {
                    println("[UnlockPinScreenV2] Error in BackHandler callback: ${e.message}")
                }
                // Return false to allow navigation
                false
            }
        }

        UnlockPinScreenV2Content(
            screenModel,
            biometryScreenModel,
            navigator,
            unlockPinCase,
            visible,
            MR.strings.forgot_pin.desc().localized()
        ) {
            screenModel.showForgotPinScreen()
        }

        val state by screenModel.pinScreenFlowState.collectAsStateMultiplatform()
        when (state) {
            is PinScreenFlow.ShowForgotPinScreen -> {
                val forgotPinScreen = rememberScreen(SharedScreen.ForgotPinScreen)
                navigator.push(forgotPinScreen)
            }
            is PinScreenFlow.ShowHomeScreen -> {
                val homeScreen = rememberScreen(SharedScreen.HomeScreen())
                globalNavigator.replaceAll(homeScreen)
            }
            is PinScreenFlow.ShowSetUpPinScreen -> {
                val setUpPinScreen = ScreenRegistry.get(
                    SharedScreen.SetupPinScreen(
                        pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CHANGE_PIN.name
                    )
                )
                navigator.push(setUpPinScreen)
            }
            is PinScreenFlow.ShowSetUpPinAndContinueScreen -> {
                val setUpPinScreen = ScreenRegistry.get(
                    SharedScreen.SetupPinScreen(
                        pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_CONTINUE.name,
                        onPinSetupSuccess = onUnlockSuccess
                    )
                )
                navigator.replace(setUpPinScreen)
            }
            is PinScreenFlow.BackupAntelopeAccountScreen -> {
                antelopeAccountName?.let {
                    val backupAntelopeAccountScreen = rememberScreen(
                        SharedScreen.BackupAntelopeAccountScreen(
                            accountName = antelopeAccountName,
                            blockchainUid = null
                        )
                    )
                    navigator.replace(backupAntelopeAccountScreen)
                }
            }
            is PinScreenFlow.ShowRecoveryPhraseScreen -> {
                val showRecoveryPhraseScreen = rememberScreen(SharedScreen.ShowRecoveryPhraseScreen())
                navigator.replace(showRecoveryPhraseScreen)
            }
            is PinScreenFlow.ShowLockScreen -> {
                val lockScreen = rememberScreen(SharedScreen.LockScreen)
                navigator.replaceAll(lockScreen)
            }
            // V1 ADD_ACCOUNT cases removed - all callers now use V2 callback approach
            // is PinScreenFlow.ShowAddAccountScreen -> { }
            // is PinScreenFlow.ShowBitcoinAddAccountScreen -> { }
            is PinScreenFlow.ConfirmDappScreen -> {
                hasCompletedSuccessfully = true
                unlockPinCallback?.let { it(true) }
                navigator.pop()
            }
            is PinScreenFlow.ShowEnableBiometryScreen -> {
                biometryScreenModel.enableBiometric(true)
                navigator.pop()
            }
            is PinScreenFlow.ShowVerifyAndSendScreen -> {
                // V1 backward compatible - when no callback provided
                hasCompletedSuccessfully = true
                onUnlockSuccess?.invoke()
                unlockPinCallback?.invoke(true)
            }
            else -> {}
        }
    }

    @Composable
    private fun UnlockPinScreenV2Content(
        screenModel: UnlockPinScreenModel,
        biometryScreenModel: IBiometryScreenModel,
        navigator: cafe.adriel.voyager.navigator.Navigator,
        unlockPinCase: Int,
        visible: Boolean,
        messageForgotPin: String,
        onClickForgotPin: (Boolean) -> Unit,
    ) {
        val platformTexts = when (unlockPinCase) {
            OPEN_APP -> UnlockPinScreenTexts(
                title = "Welcome back! 👋",
                description = "Enter your PIN to access your wallet. Your crypto is right where you left it."
            )
            SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION -> UnlockPinScreenTexts(
                title = "Confirm Transaction",
                description = "Enter your PIN to authorize and send this transaction."
            )
            CONFIRM_DAPP -> UnlockPinScreenTexts(
                title = "Confirm Action",
                description = "Enter your PIN to authorize this action."
            )
            SharedScreen.UnlockPinScreen.CHANGE_PIN -> UnlockPinScreenTexts(
                title = "Change PIN",
                description = "Enter your current PIN to proceed with changing it."
            )
            SharedScreen.UnlockPinScreen.SHOW_WORDS_PHRASE -> UnlockPinScreenTexts(
                title = "View Recovery Phrase",
                description = "Enter your PIN to view your recovery phrase."
            )
            SharedScreen.UnlockPinScreen.ADD_ACCOUNT,
            SharedScreen.UnlockPinScreen.ADD_ACCOUNT_BITCOIN -> UnlockPinScreenTexts(
                title = "Add Account",
                description = "Enter your PIN to create a new account."
            )
            SharedScreen.UnlockPinScreen.BACKUP_ANTELOPE_ACCOUNT -> UnlockPinScreenTexts(
                title = "Backup Account",
                description = "Enter your PIN to backup your account."
            )
            ENABLE_BIOMETRY -> UnlockPinScreenTexts(
                title = "Enable Biometric",
                description = "Enter your PIN to enable biometric authentication."
            )
            else -> UnlockPinScreenTexts(
                title = "Enter PIN",
                description = "Enter your PIN to continue."
            )
        }
        
        OnboardingGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section with navigation and content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if (unlockPinCase != OPEN_APP) {
                        MangalaWalletTopBarCenteredTitle(
                            title = "",
                            onBackClicked = {
                                // Handle back action based on unlock case
                                when (unlockPinCase) {
                                    SharedScreen.UnlockPinScreen.ADD_ACCOUNT,
                                    SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                                    CONFIRM_DAPP -> {
                                        navigator.pop()
                                    }
                                    else -> {
                                        // No back action for other cases
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = platformTexts.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            letterSpacing = (-0.2).sp,
                            lineHeight = 28.sp,
                            fontFamily = getInterFontFamily()
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = platformTexts.description,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFA5B4CB),
                            textAlign = TextAlign.Start,
                            letterSpacing = (-0.14).sp,
                            lineHeight = 19.6.sp,
                            fontFamily = getInterFontFamily()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(56.dp))

                    // Animated section for PIN dots and keypad
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(durationMillis = 600, delayMillis = 300)) + 
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(durationMillis = 600, delayMillis = 300)
                                ),
                        exit = fadeOut(animationSpec = tween(400)) + 
                               slideOutVertically(
                                   targetOffsetY = { it },
                                   animationSpec = tween(400)
                               )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // PIN dots animation
                            val enableAnimationShakePin by screenModel.enableAnimationShakePin.collectAsStateMultiplatform()
                            val coroutineScope = rememberCoroutineScope()
                            val offsetX = remember { Animatable(0f) }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(offsetX.value.dp, 0.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                SixDigitsV2(screenModel)
                            }

                            if (enableAnimationShakePin) {
                                animateText(offsetX, coroutineScope)
                                screenModel.disableAnimationShakePin()
                            }
                            
                            // Error message
                            val state by screenModel.showErrorMessage.collectAsStateMultiplatform()
                            if (state) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CountdownScreen(screenModel)
                                }
                            }
                        }
                    }
                }

                // Bottom section with keypad and forgot PIN
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 600, delayMillis = 450)) + 
                            slideInVertically(
                                initialOffsetY = { it / 3 },
                                animationSpec = tween(durationMillis = 600, delayMillis = 450)
                            ),
                    exit = fadeOut(animationSpec = tween(400)) + 
                           slideOutVertically(
                               targetOffsetY = { it },
                               animationSpec = tween(400)
                           )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Keypad
                        KeyPadV2(
                            screenModel, 
                            biometryScreenModel.enableBiometric(), 
                            biometryScreenModel.bioMetricByDevice()
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Forgot PIN link
                        val showForgotPin by screenModel.showForgotPin.collectAsStateMultiplatform()
                        if(showForgotPin){
                            Text(
                                text = messageForgotPin,
                                color = Color(0xFF3B90FF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = (-0.14).sp,
                                lineHeight = 19.6.sp,
                                fontFamily = getInterFontFamily(),
                                modifier = Modifier
                                    .clickable { onClickForgotPin(true) }
                                    .padding(16.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun CountdownScreen(screenModel: UnlockPinScreenModel) {
        val durationSeconds = 5 * 60L
        var remainingSeconds by remember { mutableStateOf(durationSeconds) }
        val countdownTimer = remember {
            CountdownTimer(durationSeconds = durationSeconds,
                onTick = { remainingSeconds = it },
                onFinish = {
                    remainingSeconds = 0
                    screenModel.resetIncorrectAttempts()
                })
        }

        LaunchedEffect(key1 = countdownTimer) {
            countdownTimer.start()
        }
        
        val messageExceeded = StringDesc.ResourceFormatted(
            MR.strings.message_exceeded_attempts_lock_pin,
            formatDuration(remainingSeconds)
        ).localized()
        
        Text(
            text = messageExceeded,
            color = Color(0xFFEF4444),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.14).sp,
            lineHeight = 19.6.sp,
            fontFamily = getInterFontFamily(),
            textAlign = TextAlign.Center
        )
    }

    private fun formatDuration(durationSeconds: Long): String {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        return "$minutes:${seconds.toString().padEnd(2, '0')}"
    }
}

data class UnlockPinScreenTexts(
    val title: String,
    val description: String
)