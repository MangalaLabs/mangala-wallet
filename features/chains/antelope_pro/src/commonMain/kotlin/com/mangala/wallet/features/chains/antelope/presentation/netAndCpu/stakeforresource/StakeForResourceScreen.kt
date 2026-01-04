package com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.stakeforresource

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.presentation.ui.ResourceScreen
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.AntelopeResourceProviderFeeDialog
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.ExecuteTransactionSuccess
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaTextButton
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.SuggestionChip
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent

class StakeForResourceScreen(
    private val accountName: String,
    private val isStakeRex: Boolean,
    private val isCpu: Boolean
) : BaseScreen<StakeForResourceScreenModel>(), KoinComponent {

    override val screenName: String =
        if (isCpu) MangalaAnalytics.Screens.ANTELOPE_STAKE_FOR_RESOURCES_CPU else MangalaAnalytics.Screens.ANTELOPE_STAKE_FOR_RESOURCES_NET
    override val screenClassName: String = StakeForResourceScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): StakeForResourceScreenModel = getScreenModel(parameters = {
        org.koin.core.parameter.parametersOf(
            accountName,
            isStakeRex,
            isCpu
        )
    })

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: StakeForResourceScreenModel) {
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val navigator = LocalNavigator.currentOrThrow
        if (uiState is StakeForResourceScreenUiState.ExecuteStakeForResourceSuccess) {
            RexExecuteSuccessState(
                onContinueTransaction = { screenModel.onContinueTransaction() },
                onBackHome = { navigator.popUntilRoot() }
            )
        } else {
            RexScreen(screenModel, navigator, uiState)
        }
    }

    @Composable
    private fun RexExecuteSuccessState(
        onContinueTransaction: () -> Unit,
        onBackHome: () -> Unit
    ) {
        MaxSizeBox(
            modifier = Modifier.background(MaterialTheme.mangalaColors.bg)
        ) {
            ExecuteTransactionSuccess(
                onClickBack = {},
                textTitle = if (isStakeRex) MR.strings.message_stake_successfully.desc()
                    .localized() else MR.strings.message_un_stake_successfully.desc().localized(),
                modifier = Modifier.background(color = MaterialTheme.mangalaColors.bg),
            ) {
                MangalaGradientButton(
                    label = if (isStakeRex) MR.strings.button_continue_stake.desc()
                        .localized() else MR.strings.button_continue_un_stake.desc().localized(),
                    onClick = onContinueTransaction,
                    size = MangalaButtonSize.Medium,
                    modifier = Modifier.fillMaxWidth()
                )

                VerticalSpacer(Spacing.SMALL)

                MangalaTextButton(
                    onClick = onBackHome,
                    label = MR.strings.button_transfer_ram_back_to_home.desc().localized(),
                    size = MangalaButtonSize.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun RexScreen(
        screenModel: StakeForResourceScreenModel,
        navigator: Navigator,
        uiState: StakeForResourceScreenUiState
    ) {
        MangalaBottomSheetNavigator(
            sheetShape = RectangleShape
        ) {
            val bottomNavigator = LocalBottomSheetNavigator.current

            LaunchedEffect((uiState as? StakeForResourceScreenUiState.Success)?.uiModel?.promptConfirmTransaction) {
                if ((uiState as? StakeForResourceScreenUiState.Success)?.uiModel?.promptConfirmTransaction == true) {
                    val unlockPinScreen = ScreenRegistry.get(
                        SharedScreen.UnlockPinScreen(
                            SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                            onUnlockSuccess = {
                                screenModel.onAuthenticationSuccess(accountName)
                                bottomNavigator.hide()
                            },
                            antelopeAccountName = null
                        )
                    )
                    bottomNavigator.show(unlockPinScreen)
                    screenModel.onPinPromptShown()
                }
            }

            if ((uiState as? StakeForResourceScreenUiState.Success)?.uiModel?.resourceRequiredBreakdown != null) {
                val uiModel = (uiState as? StakeForResourceScreenUiState.Success)?.uiModel

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

            RexScreenSuccessState(
                screenModel = screenModel,
                isRefreshing = screenModel.isRefreshing.value,
                uiState = uiState,
                onBackClicked = {
                    navigator.pop()
                },
                onPullToRefresh = {
                    screenModel.pullToRefresh()
                },
                onClickMainButton = {
                    screenModel.onRequestTransaction()
                }
            )
        }
    }

    @Composable
    private fun RexScreenSuccessState(
        screenModel: StakeForResourceScreenModel,
        uiState: StakeForResourceScreenUiState,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit,
        onBackClicked: () -> Unit,
        onClickMainButton: () -> Unit
    ) {
        val isLoading = remember(uiState) { uiState is StakeForResourceScreenUiState.Loading }
        val uiModel =
            remember(uiState) { (uiState as? StakeForResourceScreenUiState.Success)?.uiModel }

        ResourceScreen(
            title = if (isCpu) {
                if (isStakeRex) MR.strings.message_stake_cpu else MR.strings.message_un_stake_cpu
            } else {
                if (isStakeRex) MR.strings.message_stake_net else MR.strings.message_un_stake_net
            },
            pricesLabel = {
                Column {
                    TextDescription2(
                        text = MR.strings.current_ram_prices.desc().localized(),
                        fontSize = FontType.TINY,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.mangalaColors.textSecondary,
                    )

                    TextDescription2(
                        text = "Used CPU: ${uiModel?.cpuUsagePercentage}%",
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.mangalaWalletPlaceholder(uiModel?.account == null)
                    )
                    TextDescription2(
                        text = "Used NET: ${uiModel?.netUsagePercentage}%",
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.mangalaWalletPlaceholder(uiModel?.account == null)
                    )
                }
            },
            nativeCoinAvailableBalance = uiModel?.eosBalanceWithSymbol,
            buttonText = if (isStakeRex) MR.strings.button_stake_token.desc()
                .localized() else MR.strings.button_un_stake_token.desc().localized(),
            onBackClicked = onBackClicked,
            onValueChange = screenModel::onEosValueChange,
            amountValue = uiModel?.eosAmount,
            buttonEnabled = uiModel?.isEnableExecuteButton ?: false,
            error = if (uiModel?.isInsufficientInputAmount == true) {
                if (isStakeRex) {
                    MR.strings.message_fail_stake.desc().localized()
                } else {
                    MR.strings.message_fail_un_stake.desc().localized()
                }
            } else "",
            onPullToRefresh = onPullToRefresh,
            inputSectionTitle = if (isStakeRex) MR.strings.message_rex_net_stake_amount else MR.strings.message_rex_rent_amount_kb,
            isRefreshing = isRefreshing,
            onClickButton = onClickMainButton,
            unit = uiModel?.nativeToken.orEmpty(),
            suggestionInput = {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    items(uiModel?.suggestionInputUiModels ?: emptyList()) {
                        SuggestionChip(
                            value = it.amount.toString(),
                            isSelected = it.isSelected,
                            isLoading = isLoading,
                            onClick = {
                                screenModel.onSelectSuggestionInput(it)
                            }
                        )
                    }
                }
            },
            inputSectionEnabled = uiModel?.inputSectionEnabled == true
        )
    }
}