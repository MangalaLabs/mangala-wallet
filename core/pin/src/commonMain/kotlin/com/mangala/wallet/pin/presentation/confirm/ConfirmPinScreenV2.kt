package com.mangala.wallet.pin.presentation.confirm

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.biometry.BiometryAuthenticator
import com.mangala.wallet.biometry.presentation.BiometryByDevice
import com.mangala.wallet.biometry.presentation.BiometryScreen
import com.mangala.wallet.biometry.presentation.IBiometryScreenModel
import com.mangala.wallet.pin.presentation.base.*
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.*
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.getPlatform
import com.mangala.wallet.utils.PlatformType
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class ConfirmPinScreenV2(
    private val pin: String,
    val blockchainUid: String? = null,
    val antelopeAccountName: String? = null,
    val listString: List<String>? = null,
    val name: String? = null,
    @Transient val onPinSetupSuccess: (() -> Unit)? = null,
    private val pinCase: String
) : BaseScreen<ConfirmPinScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.CONFIRM_PIN
    override val screenClassName: String = ConfirmPinScreenV2::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ConfirmPinScreenModel {
        return getScreenModel(parameters = {
            parametersOf(
                pin, SharedScreen.SetupPinScreen.SetupPinScreenCase.valueOf(pinCase)
            )
        })
    }

    override val isBottomBarVisible = false

    @Composable
    override fun ScreenContent(screenModel: ConfirmPinScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current
        val homeScreen = rememberScreen(SharedScreen.HomeScreen())

        ConfirmPinScreenV2(screenModel) {
            navigator.pop()
        }

        val state by screenModel.pinScreenFlowState.collectAsStateMultiplatform()
        val shouldShowBiometricSetup by screenModel.shouldShowBiometricSetup.collectAsStateMultiplatform()
        
        if (shouldShowBiometricSetup) {
            LaunchedEffect(Unit) {
                val biometryScreen = BiometryScreen(
                    blockchainUid = blockchainUid,
                    antelopeAccountName = antelopeAccountName,
                    pinCase = pinCase,
                    listString = listString,
                    name = name,
                    onBiometryCallback = { enabled ->
                        screenModel.onBiometricSetupComplete(enabled)
                    },
                    onCancel = {
                        screenModel.onBiometricSetupComplete(false)
                    }
                )
                navigator.push(biometryScreen)
            }
        }

        when (state) {
            PinScreenFlow.ShowHomeScreen -> {
                globalNavigator.replaceAll(homeScreen)
            }
            PinScreenFlow.ShowCreateWalletScreen -> {
                handleCreateWalletScreen(navigator)
            }
            PinScreenFlow.ShowBackLastScreen -> {
                handleBackLastScreen(navigator)
            }
            PinScreenFlow.ShowPopFromSetupPinScreen -> {
                handleRestoreWalletScreen(navigator)
            }
            PinScreenFlow.BackupAntelopeAccountScreen -> {
                val accountName = antelopeAccountName ?: return
                val blockchainUid = blockchainUid ?: return

                val backupAntelopeAccountScreen = ScreenRegistry.get(
                    SharedScreen.BackupAntelopeAccountScreen(accountName, blockchainUid)
                )
                navigator.replaceAll(backupAntelopeAccountScreen)
            }
            PinScreenFlow.ShowSetUpPinAndContinueScreen -> {
                onPinSetupSuccess?.invoke()
            }
            else -> {
            }
        }
    }

    @Composable
    private fun handleCreateWalletScreen(navigator: Navigator) {
        val blockchainUid = blockchainUid ?: return
        val antelopeAccountName = antelopeAccountName

        val step4CreatingAccountScreen = ScreenRegistry.get(
            SharedScreen.Step4CreatingAccountScreen(
                accountName = antelopeAccountName.orEmpty(),
                accountSuffix = "",
                SharedScreen.Step4CreatingAccountScreen.AccountOperationType.CREATE
            )
        )

        navigator.replaceAll(step4CreatingAccountScreen) // replace all to prevent navigate back
    }

    @Composable
    private fun handleBackLastScreen(navigator: Navigator) {
        val blockchainUid = blockchainUid ?: return
        val antelopeAccountName = antelopeAccountName

        val step4CreatingAccountScreen = ScreenRegistry.get(
            SharedScreen.Step4CreatingAccountScreen(
                accountName = antelopeAccountName.orEmpty(),
                accountSuffix = "",
                operationType = when (pinCase) {
                    SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET.name -> SharedScreen.Step4CreatingAccountScreen.AccountOperationType.CREATE
                    SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN.name -> SharedScreen.Step4CreatingAccountScreen.AccountOperationType.IMPORT
                    else -> SharedScreen.Step4CreatingAccountScreen.AccountOperationType.CREATE
                },
            )
        )

        navigator.replaceAll(step4CreatingAccountScreen) // replace all to prevent navigate back
    }

    private fun handleRestoreWalletScreen(
        navigator: Navigator
    ) {
        val mnemonicList = listString ?: emptyList()
        val walletName = name ?: ""

        val createWalletScreen = ScreenRegistry.get(
            SharedScreen.CreateWalletScreen(
                listString = mnemonicList,
                name = walletName,
                createWalletCase = SharedScreen.CreateWalletScreen.CreateWalletScreenCase.IMPORT_WALLET
            )
        )

        navigator.replaceAll(createWalletScreen) // replace all to prevent navigate back
    }

    @Composable
    fun ConfirmPinScreenV2(screenModel: BasePinScreenModel, onBackClicked: (Boolean) -> Unit) {
        val platformTexts = getConfirmPlatformSpecificTexts()
        BaseConfirmPinScreenV2(
            screenModel,
            platformTexts.title,
            platformTexts.description1,
            platformTexts.securityIndicator,
            onBackClicked
        )
    }
}

data class ConfirmPinScreenTexts(
    val title: String,
    val description1: String,
    val description2: String,
    val securityIndicator: String
)

@Composable
fun getConfirmPlatformSpecificTexts(): ConfirmPinScreenTexts {
    return when (getPlatform().type) {
        PlatformType.ANDROID -> ConfirmPinScreenTexts(
            title = "Confirm your PIN ✅",
            description1 = "Type it again to lock it in. No password recovery here - you're in full control! 🚀",
            description2 = "Your crypto, your responsibility. We can't help if you forget, so make it memorable! 🧠",
            securityIndicator = "🔒 PIN locked and encrypted"
        )
        PlatformType.IOS -> ConfirmPinScreenTexts(
            title = "Confirm Passcode",
            description1 = "Enter your 6-digit passcode once more to finalize setup.",
            description2 = "Your passcode will be stored securely on this device using industry-standard encryption.",
            securityIndicator = "✅ Passcode secured • 🔒 Your privacy, protected"
        )
        PlatformType.DESKTOP -> ConfirmPinScreenTexts(
            title = "Verify Access PIN",
            description1 = "Confirm your PIN to complete setup. No recovery option available.",
            description2 = "PIN will be encrypted locally using industry-standard security protocols.",
            securityIndicator = "✅ PIN verified and secured • 🛡️ Local encryption active"
        )
    }
}

@Composable
fun BaseConfirmPinScreenV2(
    screenModel: BasePinScreenModel,
    title: String,
    description1: String,
    securityIndicator: String,
    onBackClicked: (Boolean) -> Unit
) {
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
                MangalaWalletTopBarCenteredTitle(
                    title = "",
                    onBackClicked = {
                        screenModel.resetPinEntered()
                        onBackClicked(true)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                // Title and Description
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = title,
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
                        text = description1,
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
                    com.mangala.wallet.pin.presentation.base.SixDigitsV2(screenModel)
                }

                if (enableAnimationShakePin) {
                    animateText(offsetX, coroutineScope)
                    screenModel.disableAnimationShakePin()
                }
            }

            // Bottom section with keypad and text below
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Keypad
                com.mangala.wallet.pin.presentation.base.KeyPadV2(screenModel, false, BiometryByDevice.ANDROID_FINGERPRINT)
                
                Spacer(modifier = Modifier.height(64.dp))
                
                // Security indicator
                Text(
                    text = securityIndicator,
                    color = Color(0xFF8B95A7),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.12).sp,
                    lineHeight = 16.8.sp,
                    fontFamily = getInterFontFamily(),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}