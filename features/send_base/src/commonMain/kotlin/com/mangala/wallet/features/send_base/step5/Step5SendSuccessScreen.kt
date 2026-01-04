package com.mangala.wallet.features.send_base.step5

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.ExecuteTransactionSuccess
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaTextButton
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class Step5SendSuccessScreen(
    private val txHash: String,
    private val blockchainUid: String
) : BaseScreen<Step5SendSuccessScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.SEND_TOKEN_SUCCESS
    override val screenClassName: String = Step5SendSuccessScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel() = getScreenModel<Step5SendSuccessScreenModel>(
        parameters = {
            parametersOf(
                txHash,
                blockchainUid
            )
        }
    )

    @Composable
    override fun ScreenContent(screenModel: Step5SendSuccessScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(true) {
            onBackPressedCallback = {
                navigator.popUntilRoot()
                false
            }
        }

        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

        Step5SendSuccessScreen(
            uiModel = uiModel,
            onDismiss = {
                navigator.popUntilRoot()
            }
        )
    }

    @Composable
    fun Step5SendSuccessScreen(uiModel: Step5SendSuccessScreenUiModel, onDismiss: () -> Unit) {
        val uriHandler = LocalUriHandler.current

        ExecuteTransactionSuccess(
            onClickBack = onDismiss,
            textTitle = MR.strings.message_transfer_ram_success.desc().localized(),
            bottomButton = {
                MangalaGradientButton(
                    label = MR.strings.all_back_to_home.desc().localized(),
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
                VerticalSpacer(Spacing.SMALL)
                MangalaTextButton(
                    label = MR.strings.all_view_on_block_explorer.desc().localized(),
                    onClick = {
                        uriHandler.openUri(uiModel.txBlockExplorerLink)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}