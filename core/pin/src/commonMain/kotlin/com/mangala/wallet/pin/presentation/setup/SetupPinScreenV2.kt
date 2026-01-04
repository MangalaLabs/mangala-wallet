package com.mangala.wallet.pin.presentation.setup

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.biometry.presentation.BiometryByDevice
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.pin.presentation.base.BasePinScreenModel
import com.mangala.wallet.pin.presentation.base.PinScreenFlow
import com.mangala.wallet.pin.presentation.confirm.ConfirmPinScreenV2
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.animateText
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.navigation.BackHandler
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.PlatformType
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.getPlatform
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class SetupPinScreenV2(
    private val blockchainUid: String? = null,
    private val antelopeAccountName: String? = null,
    private val listString: List<String>? = null,
    private val name: String? = null,
    @Transient private val onPinSetupSuccess: (() -> Unit)? = null,
    @Transient private val onPinSetupCancel: (() -> Unit)? = null,
    private val pinCase: String
) : BaseScreen<SetupPinScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.SETUP_PIN
    override val screenClassName: String = SetupPinScreenV2::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): SetupPinScreenModel = getScreenModel(
        parameters = {
            parametersOf(
                SharedScreen.SetupPinScreen.SetupPinScreenCase.valueOf(pinCase)
            )
        }
    )

    @Composable
    override fun ScreenContent(screenModel: SetupPinScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current

        val homeScreen = rememberScreen(SharedScreen.HomeScreen())

        val state by screenModel.pinScreenFlowState.collectAsStateMultiplatform()
        if (state == PinScreenFlow.ShowConfirmPinScreen) {
            screenModel.setPinScreenFlowState(PinScreenFlow.ShowSetUpPinScreen)
            navigator.push(
                ConfirmPinScreenV2(
                    pin = screenModel.pinEntered.value,
                    blockchainUid = blockchainUid,
                    antelopeAccountName = antelopeAccountName,
                    listString = listString,
                    name = name,
                    onPinSetupSuccess = onPinSetupSuccess,
                    pinCase = screenModel.pinCase.name
                )
            )
            screenModel.resetPinEntered()
        }

        val onPinSetupCancelRef = rememberUpdatedState(onPinSetupCancel)
        val onBackClickRemembered = remember {
            {
                screenModel.resetPinEntered()
                onPinSetupCancelRef.value?.invoke()

                if (screenModel.pinCase == SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET ||
                    screenModel.pinCase == SharedScreen.SetupPinScreen.SetupPinScreenCase.RESTORE_WALLET ||
                    screenModel.pinCase == SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN ||
                    screenModel.pinCase == SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_CONTINUE
                ) {
                    navigator.pop()
                } else {
                    globalNavigator.replaceAll(homeScreen)
                }
            }
        }

        LaunchedEffect(Unit) {
            onBackPressedCallback = {
                try {
                    onBackClickRemembered()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                false
            }
        }

        SetupPinScreenV2(screenModel) {
            onBackClickRemembered()
        }
    }
}

@Composable
fun SetupPinScreenV2(screenModel: BasePinScreenModel, onBackClicked: (Boolean) -> Unit) {
    val platformTexts = getPlatformSpecificTexts()
    BaseSetupPinScreenV2(
        screenModel,
        platformTexts.title,
        platformTexts.description1,
        platformTexts.securityIndicator,
        onBackClicked
    )
}

data class PinScreenTexts(
    val title: String,
    val description1: String,
    val description2: String,
    val securityIndicator: String
)

@Composable
fun getPlatformSpecificTexts(): PinScreenTexts {
    return when (getPlatform().type) {
        PlatformType.ANDROID -> PinScreenTexts(
            title = "Secure your wallet 🔐",
            description1 = "Keep your crypto safer than your DMs 💬 PIN protects this app only.",
            description2 = "Pro tip: Avoid obvious patterns like 1234 or your birthday. Make it memorable but unique.",
            securityIndicator = "🛡️ Your PIN stays on this device only • 🔒 Encrypted with military-grade security"
        )

        PlatformType.IOS -> PinScreenTexts(
            title = "Create Your Passcode",
            description1 = "Quick access to your wallet with a 6-digit code that only you know. ✨",
            description2 = "This passcode secures app access, not wallet recovery. Your seed phrase handles the heavy lifting there.",
            securityIndicator = "🛡️ Device-only storage • Bank-level encryption"
        )

        PlatformType.DESKTOP -> PinScreenTexts(
            title = "Set Access PIN",
            description1 = "Create a 4-6 digit PIN for quick wallet access on this device.",
            description2 = "Important: This PIN cannot be recovered if forgotten. PIN is separate from wallet recovery mechanism.",
            securityIndicator = "🔒 AES-256 encryption • Device-specific authentication • Cleared if browser data reset"
        )
    }
}

@Composable
fun BaseSetupPinScreenV2(
    screenModel: BasePinScreenModel,
    title: String,
    description1: String,
    securityIndicator: String,
    onBackClicked: (Boolean) -> Unit
) {
    val onBackClickRef = rememberUpdatedState(onBackClicked)
    val onBackRemembered = remember {
        {
            onBackClickRef.value(true)
        }
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
                MangalaWalletTopBarCenteredTitle(
                    title = "",
                    onBackClicked = onBackRemembered
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
                com.mangala.wallet.pin.presentation.base.KeyPadV2(
                    screenModel,
                    false,
                    BiometryByDevice.ANDROID_FINGERPRINT
                )

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