package com.mangala.wallet.pin.presentation.unlock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.biometry.presentation.BiometryByDevice
import com.mangala.wallet.biometry.presentation.BiometryState
import com.mangala.wallet.biometry.presentation.IBiometryScreenModel
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.pin.presentation.base.*
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Logo
import com.mangala.wallet.ui.*
import com.mangala.wallet.ui.SharedScreen.UnlockPinScreen.Companion.CONFIRM_DAPP
import com.mangala.wallet.ui.SharedScreen.UnlockPinScreen.Companion.ENABLE_BIOMETRY
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.ISystemInfoManager
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
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
import kotlin.jvm.Transient

class UnlockPinScreen(
    private val unlockPinCase: Int,
    private val antelopeAccountName: String?,
    @Transient val onUnlockSuccess: (() -> Unit)? = null,
    @Transient private val unlockPinCallback: ((Boolean) -> Unit)? = null
) : BaseScreen<UnlockPinScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.UNLOCK_PIN
    override val screenClassName: String = UnlockPinScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean = false

    //    private val biometryScreenModel: IBiometryScreenModel by inject()
//    private val systemInfoManager: ISystemInfoManager by inject()

    @Composable
    override fun createScreenModel(): UnlockPinScreenModel {
        return getScreenModel(parameters = {
            parametersOf(unlockPinCase)
        })
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

//        visible = false

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
                    if(biometryScreenModel.bioMetricByDevice() == BiometryByDevice.ANDROID_FINGERPRINT || biometryScreenModel.bioMetricByDevice() == BiometryByDevice.ANDROID_FACE_ID){

                        //Add delay for android
                        biometryScreenModel.tryToAuth(titleBiometry, "", buttonBiometry)
                    }else{
                        biometryScreenModel.tryToAuth(titleBiometry, reasonBiometry, buttonBiometry)
                    }
                }

            },
            onDisposed = {
                screenModel.onDispose()
                biometryScreenModel.resetBiometryState()
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

        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(2000))
            ){
                UnlockPinScreen(
                    screenModel,
                    biometryScreenModel,
                    MR.strings.forgot_pin.desc().localized()
                ) {
                    screenModel.showForgotPinScreen()
                }
            }
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
                val showRecoveryPhraseScreen = rememberScreen(SharedScreen.ShowRecoveryPhraseScreen)
                navigator.replace(showRecoveryPhraseScreen)
            }
            is PinScreenFlow.ShowLockScreen -> {
                val lockScreen = rememberScreen(SharedScreen.LockScreen)
                navigator.replaceAll(lockScreen)
            }
            is PinScreenFlow.ShowAddAccountScreen -> {
                onUnlockSuccess?.invoke()
            }
            is PinScreenFlow.ShowBitcoinAddAccountScreen -> {
                onUnlockSuccess?.invoke()
            }
            is PinScreenFlow.ConfirmDappScreen -> {
                unlockPinCallback?.let { it(true) }
                navigator.pop()
            }
            is PinScreenFlow.ShowEnableBiometryScreen -> {
                biometryScreenModel.enableBiometric(true)
                navigator.pop()
            }
            is PinScreenFlow.ShowVerifyAndSendScreen -> {
                onUnlockSuccess?.invoke()
            }
            else -> {}
        }
    }

    @Composable
    private fun UnlockPinScreen(
        screenModel: UnlockPinScreenModel,
        biometryScreenModel: IBiometryScreenModel,
        messageForgotPin: String,
        onClickForgotPin: (Boolean) -> Unit,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        ) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(Spacing.XLARGE))

                    Image(
                        painter = rememberVectorPainter(MangalaWalletPack.Logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(Dimensions.IconLogoSize)
                            .clip(RoundedCornerShape(Dimensions.IconLogoSize / 3.75f))
                    )

                    Spacer(modifier = Modifier.height(Spacing.LARGE))

//                    SixDigits(screenModel)

                    val enableAnimationShakePin by screenModel.enableAnimationShakePin.collectAsStateMultiplatform()
                    val coroutineScope = rememberCoroutineScope()
                    val offsetX = remember { Animatable(0f) }

                    Box(
                        modifier = Modifier.offset(offsetX.value.dp, 0.dp)
                    ) {
                        SixDigits(screenModel)
                    }

                    if (enableAnimationShakePin) {
                        animateText(offsetX, coroutineScope)
                        screenModel.disableAnimationShakePin()
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val state by screenModel.showErrorMessage.collectAsStateMultiplatform()
                    if (state) {
                        CountdownScreen(screenModel)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End,
                ) {

                    KeyPad(screenModel, biometryScreenModel.enableBiometric(), biometryScreenModel.bioMetricByDevice())

                    Spacer(modifier = Modifier.height(24.dp))

                    val showForgotPin by screenModel.showForgotPin.collectAsStateMultiplatform()
                    if(showForgotPin){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Spacer(modifier = Modifier.width(128.dp))
                                Box(
                                    modifier = Modifier.clickable {
                                        onClickForgotPin(true)
                                    }
                                ) {
                                    TextCanClick(
                                        text = messageForgotPin,
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 8.dp,
                                            bottom = 8.dp
                                        )
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
        TextError(
            text = messageExceeded,
            modifier = Modifier.padding(16.dp),
        )
    }

    private fun formatDuration(durationSeconds: Long): String {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        return "$minutes:${seconds.toString().padEnd(2, '0')}"
    }

}
