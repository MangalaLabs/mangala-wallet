package com.mangala.wallet.pin.presentation.setup

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.biometry.presentation.BiometryByDevice
import com.mangala.wallet.pin.presentation.base.*
import com.mangala.wallet.pin.presentation.confirm.ConfirmPinScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.*
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class SetupPinScreen(
    private val blockchainUid: String? = null,
    private val antelopeAccountName: String? = null,
    private val listString: List<String>? = null,
    private val name: String? = null,
    @Transient private val onPinSetupSuccess: (() -> Unit)? = null,
    private val pinCase: String
) : BaseScreen<SetupPinScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.SETUP_PIN
    override val screenClassName: String = SetupPinScreen::class.simpleName.orEmpty()

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

//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
//            horizontalAlignment = Alignment.Start
//        ) {
//            SetupPinScreen(screenModel) {
//                globalNavigator.replaceAll(homeScreen)
//            }
//        }

        val state by screenModel.pinScreenFlowState.collectAsStateMultiplatform()
        if (state == PinScreenFlow.ShowConfirmPinScreen) {
            screenModel.setPinScreenFlowState(PinScreenFlow.ShowSetUpPinScreen)
            navigator.push(
                ConfirmPinScreen(
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

        val backgroundColor = MaterialTheme.colors.background
//        val halfScreenHeight = (LocalConfiguration.current.screenHeightDp.dp * 80 / 100f) + LocalDensity.current.run {
//            56.dp // height of the bottom navigation bar
//        }

        val halfScreenHeight = 700.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // content of the new screen
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    SetupPinScreen(screenModel) {
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
            }
        }
    }

}


@Composable
fun SetupPinScreen(screenModel: BasePinScreenModel, onBackClicked: (Boolean) -> Unit) {
    BaseSetupPinScreen(
        screenModel,
        MR.strings.title_setUpPin.desc().localized(),
        MR.strings.description_setUpPin.desc().localized(),
        MR.strings.tip_setUpPin.desc().localized(),
        onBackClicked
    )
}

@Composable
fun BaseSetupPinScreen(
    screenModel: BasePinScreenModel,
    title: String,
    description1: String,
    description2: String,
    onBackClicked: (Boolean) -> Unit
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
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                IconButton(onClick = {
                    screenModel.resetPinEntered()
                    onBackClicked(true)
                }) {
                    Icon(
                        imageVector = MangalaWalletPack.ArrowLeft,
                        contentDescription = "Back"
                    )
                }

                TextTitle3(
                    text = title,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )
                TextDescription1(
                    text = description1,
                    modifier = Modifier.padding(16.dp)
                )

                val spaceTop = 72.dp

                Spacer(modifier = Modifier.height(spaceTop))

                val enableAnimationShakePin by screenModel.enableAnimationShakePin.collectAsStateMultiplatform()
                val coroutineScope = rememberCoroutineScope()
                val offsetX = remember { Animatable(0f) }

                Box(
                    modifier = Modifier.offset(offsetX.value.dp, 0.dp)
                ){
                    SixDigits(screenModel)
                }

                if(enableAnimationShakePin){
                    animateText(offsetX, coroutineScope)
                    screenModel.disableAnimationShakePin()
                }

                Spacer(modifier = Modifier.height(spaceTop))

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                TextDescription2(
                    text = description2,
                    modifier = Modifier.padding(16.dp)
                )
                KeyPad(screenModel, false, BiometryByDevice.ANDROID_FINGERPRINT)
            }
        }
    }
}
