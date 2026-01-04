package com.mangala.wallet.pin.presentation.confirm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.pin.presentation.base.BasePinScreenModel
import com.mangala.wallet.pin.presentation.base.PinScreenFlow
import com.mangala.wallet.pin.presentation.setup.BaseSetupPinScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class ConfirmPinScreen(
    private val pin: String,
    val blockchainUid: String? = null,
    val antelopeAccountName: String? = null,
    val listString: List<String>? = null,
    val name: String? = null,
    @Transient val onPinSetupSuccess: (() -> Unit)? = null,
    private val pinCase: String
) : BaseScreen<ConfirmPinScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.CONFIRM_PIN
    override val screenClassName: String = ConfirmPinScreen::class.simpleName.orEmpty()

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

        ConfirmPinScreen(screenModel) {
            navigator.pop()
        }

        val state by screenModel.pinScreenFlowState.collectAsStateMultiplatform()

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
                accountSuffix = ""
            )
        )

        val biometryScreen = ScreenRegistry.get(
            SharedScreen.BiometryScreen(
                blockchainUid,
                antelopeAccountName = antelopeAccountName
            )
        )

        navigator.push(biometryScreen)
        navigator.replaceAll(step4CreatingAccountScreen) // replace all to prevent navigate back
    }

    @Composable
    private fun handleBackLastScreen(navigator: Navigator) {
        val blockchainUid = blockchainUid ?: return
        val antelopeAccountName = antelopeAccountName

        val step4CreatingAccountScreen = ScreenRegistry.get(
            SharedScreen.Step4CreatingAccountScreen(
                accountName = antelopeAccountName.orEmpty(),
                accountSuffix = ""
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
    fun ConfirmPinScreen(screenModel: BasePinScreenModel, onBackClicked: (Boolean) -> Unit) {
        BaseSetupPinScreen(
            screenModel,
            MR.strings.title_confirmPin.desc().localized(),
            MR.strings.description_confirmPin.desc().localized(),
            MR.strings.tip_confirmPin.desc().localized(),
            onBackClicked
        )
    }
}
