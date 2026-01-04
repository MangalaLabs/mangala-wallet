package com.mangala.wallet.biometry.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.biometry.Platform
import com.mangala.wallet.biometry.getPlatform
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Faceid
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.ToastFactory
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import kotlin.jvm.Transient

class BiometryScreen(
    private val blockchainUid: String? = null,
    private val antelopeAccountName: String? = null,
    private val pinCase: String? = null,
    private val listString: List<String>? = null,
    private val name: String? = null,
    @Transient val onBiometryCallback: ((Boolean) -> Unit)? = null,
    @Transient val onCancel: () -> Unit = {}
): Screen, KoinComponent {

    private val biometryScreenModel: IBiometryScreenModel by inject()

    private val platform: Platform = getPlatform()

    private fun isAndroid() = platform.name == "Android"

    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.BIOMETRY,
                BiometryScreen::class.simpleName.orEmpty()
            )
        })

        val navigator = LocalNavigator.currentOrThrow

        val titleAndroid =  MR.strings.title_biometry_android.desc().localized()
        val titleIOS =  MR.strings.title_biometry_ios.desc().localized()
        val description =  MR.strings.message_biometry.desc().localized()
        val textButton =  MR.strings.button_biometry.desc().localized()
        val textButtonSkip =  MR.strings.button_biometry_skip.desc().localized()

        val titleBiometry =  MR.strings.title_request_biometry_sign_in.desc().localized()
        val reasonBiometry =  MR.strings.title_request_biometry_reason.desc().localized()
        val buttonBiometry =  MR.strings.title_request_biometry_button.desc().localized()

//        val screenModel = BiometryScreenModel()
        val toastFactory = get<ToastFactory>()
//
        val enableBiometry by biometryScreenModel.enableBiometry.collectAsStateMultiplatform()

        LaunchedEffect(enableBiometry) {
            when (enableBiometry) {
                BiometryState.NONE, BiometryState.UNLOCKING -> {

                }
                BiometryState.SUCCESS -> {
                    biometryScreenModel.enableBiometric(true)
                    navigateToNextScreen(navigator, enabled = true)
                }
                BiometryState.FAIL -> {
                    onBiometryCallback?.let { it(false) }
                    navigator.pop()
                }
            }
        }

        val title = if(isAndroid()){
            titleAndroid
        }else{
            titleIOS
        }

        BiometryScreen(title, description, textButton, textButtonSkip, {
            onCancel()
            navigator.pop()
        }, {
            if(biometryScreenModel.bioMetricByDevice() == BiometryByDevice.ANDROID_FINGERPRINT || biometryScreenModel.bioMetricByDevice() == BiometryByDevice.ANDROID_FACE_ID){
                biometryScreenModel.tryToAuth(titleBiometry, "", buttonBiometry)
            }else{
                biometryScreenModel.tryToAuth(titleBiometry, reasonBiometry, buttonBiometry)
            }
        },{
            navigateToNextScreen(navigator, enabled = false)
        })
    }

    @Suppress("NotConstructor")
    @Composable
    private fun BiometryScreen(
        title: String,
        description1: String,
        textButton: String,
        textButton2: String,
        onBackClicked: (Boolean) -> Unit,
        onEnableBiometry: (Boolean) -> Unit,
        onClickSkip: (Boolean) -> Unit,
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
                    
                    // Icon centered
                    val icon = when(biometryScreenModel.bioMetricByDevice()) {
                        BiometryByDevice.IOS_TOUCH_ID -> MangalaWalletPack.Faceid
                        BiometryByDevice.IOS_FACE_ID -> MangalaWalletPack.Faceid
                        BiometryByDevice.ANDROID_FINGERPRINT -> MangalaWalletPack.Faceid
                        BiometryByDevice.ANDROID_FACE_ID -> MangalaWalletPack.Faceid
                        else -> MangalaWalletPack.Faceid
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            modifier = Modifier.size(96.dp),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                // Bottom section with buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MangalaGradientButton(
                        label = textButton,
                        onClick = { onEnableBiometry(true) },
                        enabled = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MangalaGradientButton(
                        label = textButton2,
                        onClick = { onClickSkip(true) },
                        enabled = true,
                        buttonStyle = MangalaButtonStyle.TRANSPARENT,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
    
    private fun navigateToNextScreen(navigator: Navigator, enabled: Boolean) {
        pinCase?.let { case ->
            when (SharedScreen.SetupPinScreen.SetupPinScreenCase.valueOf(case)) {
                SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET -> {
                    val step4CreatingAccountScreen = ScreenRegistry.get(
                        SharedScreen.Step4CreatingAccountScreen(
                            accountName = antelopeAccountName.orEmpty(),
                            accountSuffix = "",
                            SharedScreen.Step4CreatingAccountScreen.AccountOperationType.CREATE
                        )
                    )
                    navigator.replaceAll(step4CreatingAccountScreen)
                }
                
                SharedScreen.SetupPinScreen.SetupPinScreenCase.CHANGE_PIN -> {
                    val homeScreen = ScreenRegistry.get(SharedScreen.HomeScreen())
                    navigator.replaceAll(homeScreen)
                }
                
                SharedScreen.SetupPinScreen.SetupPinScreenCase.RESTORE_WALLET -> {
                    val mnemonicList = listString ?: emptyList()
                    val walletName = name ?: ""
                    
                    val createWalletScreen = ScreenRegistry.get(
                        SharedScreen.CreateWalletScreen(
                            listString = mnemonicList,
                            name = walletName,
                            createWalletCase = SharedScreen.CreateWalletScreen.CreateWalletScreenCase.IMPORT_WALLET
                        )
                    )
                    navigator.replaceAll(createWalletScreen)
                }
                
                SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN -> {
                    val step4CreatingAccountScreen = ScreenRegistry.get(
                        SharedScreen.Step4CreatingAccountScreen(
                            accountName = antelopeAccountName.orEmpty(),
                            accountSuffix = "",
                            operationType = SharedScreen.Step4CreatingAccountScreen.AccountOperationType.IMPORT
                        )
                    )
                    navigator.replaceAll(step4CreatingAccountScreen)
                }
                
                SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_BACKUP_ANTELOPE -> {
                    val accountName = antelopeAccountName ?: return
                    val blockchainUid = blockchainUid ?: return
                    
                    val backupAntelopeAccountScreen = ScreenRegistry.get(
                        SharedScreen.BackupAntelopeAccountScreen(accountName, blockchainUid)
                    )
                    navigator.replaceAll(backupAntelopeAccountScreen)
                }

                SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_CONTINUE_HOME_SCREEN -> {
                    val homeScreen = ScreenRegistry.get(SharedScreen.HomeScreen())
                    navigator.replaceAll(homeScreen)
                }
                
                SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_CONTINUE -> {
                    onBiometryCallback?.let { it(enabled) }
                    navigator.pop()
                }
            }
        } ?: run {
            onBiometryCallback?.let { it(enabled) }
            navigator.pop()
        }
    }
}