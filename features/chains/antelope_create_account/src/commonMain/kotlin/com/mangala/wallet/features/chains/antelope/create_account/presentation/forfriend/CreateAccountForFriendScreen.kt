package com.mangala.wallet.features.chains.antelope.create_account.presentation.forfriend

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.linh.antelope_qr.domain.model.CreateAccountForFriendRequest
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.AntelopeResourceProviderFeeDialog
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import org.koin.core.parameter.parametersOf

class CreateAccountForFriendScreen(
    private val accountName: String,
    private val activePublicKey: String,
    private val ownerPublicKey: String,
    private val blockchainUid: String
) : BaseScreen<CreateAccountForFriendScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_FOR_FRIEND
    override val screenClassName: String = CreateAccountForFriendScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): CreateAccountForFriendScreenModel {
        return getScreenModel<CreateAccountForFriendScreenModel>(
            parameters = {
                parametersOf(
                    CreateAccountForFriendRequest(accountName, activePublicKey, ownerPublicKey, blockchainUid)
                )
            }
        )
    }

    override val isBottomBarVisible: Boolean
        get() = false

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: CreateAccountForFriendScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        val navigator = LocalNavigator.currentOrThrow

        BottomSheetNavigator {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            LaunchedEffect((uiState as? CreateAccountForFriendUiState.Loaded)?.promptConfirmTransaction) {
                if ((uiState as? CreateAccountForFriendUiState.Loaded)?.promptConfirmTransaction == true) {
                    val unlockPinScreen = ScreenRegistry.get(
                        SharedScreen.UnlockPinScreen(
                            SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                            onUnlockSuccess = {
                                screenModel.onAuthenticationSuccess()
                                bottomSheetNavigator.hide()
                            },
                            antelopeAccountName = null
                        )
                    )

                    bottomSheetNavigator.show(unlockPinScreen)
                    screenModel.onPinPromptShown()
                }
            }

            if ((uiState as? CreateAccountForFriendUiState.Loaded)?.resourceRequiredBreakdown != null) {
                val uiModel = uiState as? CreateAccountForFriendUiState.Loaded

                AntelopeResourceProviderFeeDialog(
                    feeBreakdown = uiModel?.resourceRequiredBreakdown,
                    resourceRequiredTotal = uiModel?.resourceRequiredTotal,
                    onClick = {
                        screenModel.onConfirmResourceProviderFee()
                    },
                    onDismiss = {
                        screenModel.onDismissTransactionFeeBreakdown()
                    }
                )
            }

            CreateAccountForFriendScreen(
                uiState,
                onBackClicked = { navigator.pop() },
                onClickSelectAccount = {
                    val selectedAccount = (uiState as? CreateAccountForFriendUiState.Loaded)?.selectedAccount ?: return@CreateAccountForFriendScreen

                    val screen = ScreenRegistry.get(SharedScreen.SelectPaymentAccountScreen(selectedAccount.accountName, onSelectAccount = screenModel::onSelectAccount))
                    bottomSheetNavigator.show(screen)
                },
                onClickCreateAccount = {
                    screenModel.onRequestTransaction()
                }
            )
        }
    }

    @Composable
    fun CreateAccountForFriendScreen(
        uiState: CreateAccountForFriendUiState,
        onBackClicked: () -> Unit,
        onClickSelectAccount: () -> Unit,
        onClickCreateAccount: () -> Unit
    ) {
        MaxWidthColumn(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
            MangalaWalletTopBar(text = MR.strings.title_create_account_for_friend_screen.desc().localized(), onBackClicked = {
                onBackClicked()
            })
            when (uiState) {
                is CreateAccountForFriendUiState.Created -> {
                    Text(MR.strings.message_create_account_for_friend_screen_account_created_txHash.format(uiState.txHash).localized())
                }

                is CreateAccountForFriendUiState.Initial -> {
                    CircularProgressIndicator()
                }

                is CreateAccountForFriendUiState.Loaded -> {
                    Text(MR.strings.message_create_account_for_friend_screen_account_name_to_create.format(uiState.createAccountForFriendRequest.accountName).localized())
                    Text(MR.strings.message_create_account_for_friend_screen_active_public_key.format(uiState.createAccountForFriendRequest.activePublicKey).localized())
                    Text(MR.strings.message_create_account_for_friend_screen_owner_public_key.format(uiState.createAccountForFriendRequest.ownerPublicKey).localized())
                    VerticalSpacer(32.dp)
                    Button(onClick = onClickSelectAccount) {
                        Text(MR.strings.button_create_account_for_friend_screen_account_selected.format(uiState.selectedAccount?.accountName ?: "").localized())
                    }
                    VerticalSpacer(32.dp)
                    uiState.error?.let {
                        Text(it, color = Color.Red)
                    }
                    Button(onClick = onClickCreateAccount, enabled = uiState.isLoading.not()) {
                        Text(MR.strings.button_create_account_for_friend_screen_create_account.desc().localized())
                    }
                }
                CreateAccountForFriendUiState.NoAccount -> {
                    Text(MR.strings.message_create_account_for_friend_screen_no_have_account.desc().localized())
                }
            }
        }
    }
}