package com.mangala.wallet.features.chains.antelope.presentation.powerup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.features.chains.antelope.presentation.ui.ResourceScreen
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.AntelopeResourceProviderFeeDialog
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaTextButton
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.ExecuteTransactionSuccess
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import org.koin.core.parameter.parametersOf

class PowerUpScreen(private val accountName: String, private val isCpu: Boolean) :
    BaseScreen<PowerUpScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_POWER_UP
    override val screenClassName: String = PowerUpScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): PowerUpScreenModel {
        return getScreenModel { parametersOf(accountName, isCpu) }
    }

    override val isBottomBarVisible: Boolean = false

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: PowerUpScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        MangalaBottomSheetNavigator(
            sheetShape = RectangleShape
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            LaunchedEffect((uiState as? PowerUpScreenUiState.Loaded)?.promptConfirmTransaction) {
                if ((uiState as? PowerUpScreenUiState.Loaded)?.promptConfirmTransaction == true) {
                    val unlockPinScreen = ScreenRegistry.get(
                        SharedScreen.UnlockPinScreen(
                            SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                            onUnlockSuccess = {
                                screenModel.onAuthenticationSuccess(accountName)
                                bottomSheetNavigator.hide()
                            },
                            antelopeAccountName = null
                        )
                    )
                    bottomSheetNavigator.show(unlockPinScreen)
                    screenModel.onPinPromptShown()
                }
            }

            if ((uiState as? PowerUpScreenUiState.Loaded)?.resourceRequiredBreakdown != null) {
                val uiModel = uiState as? PowerUpScreenUiState.Loaded

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

            PowerUpScreen(
                uiState = uiState,
                onAmountChange = { screenModel.onUpdateAmount(it) },
                onClickPowerUp = {
                    screenModel.onRequestTransaction()
                },
                onContinueTransaction = { screenModel.continuePowerUp() },
                onBackHome = {
                    navigator.popUntilRoot()
                },
                onBackClicked = {
                    navigator.pop()
                },
                isRefreshing = screenModel.isRefreshing.value,
                onPullToRefresh = {screenModel.pullToRefresh()}
            )
        }
    }

    @Composable
    fun PowerUpScreen(
        uiState: PowerUpScreenUiState,
        onAmountChange: (String) -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit,
        onClickPowerUp: () -> Unit,
        onContinueTransaction: () -> Unit,
        onBackHome: () -> Unit,
        onBackClicked: () -> Unit
    ) {
        when (uiState) {
            is PowerUpScreenUiState.ExecutePowerUpSuccess -> {
                PowerUpExecuteSuccessState(
                    onContinueTransaction = onContinueTransaction,
                    onBackHome = onBackHome
                )
            }

            else -> {
                val loadedState = remember(uiState) {uiState as? PowerUpScreenUiState.Loaded}

                ResourceScreen(
                    title = MR.strings.all_antelope_power_up,
                    inputSectionTitle = if (isCpu) MR.strings.message_power_up_cpu_stake_amount else MR.strings.message_power_up_net_stake_amount,
                    nativeCoinAvailableBalance = loadedState?.balanceFormatted,
                    buttonText = MR.strings.button_power_up_rent.desc().localized(),
                    buttonEnabled = loadedState?.buttonEnabled ?: false,
                    pricesLabel = {
                        Column {
                            val priceLabel =
                                if (isCpu) MR.strings.message_power_up_cpu_prices else MR.strings.message_power_up_net_prices

                            TextDescription2(
                                text = priceLabel.desc().localized(),
                                fontSize = FontType.TINY,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.mangalaColors.textSecondary
                            )

                            TextDescription2(
                                text = loadedState?.powerUpRateFormatted ?: "0.0000",
                                fontSize = FontType.SMALL,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.mangalaColors.textPrimary,
                                modifier = Modifier.mangalaWalletPlaceholder(loadedState?.powerUpRate?.rate == null || loadedState.powerUpRate.rate == BigDecimal.ZERO)
                            )

                            TextDescription2(
                                text = loadedState?.resourceUsedFormatted?.let {
                                    MR.strings.all_used.format(
                                        it
                                    ).localized()
                                } ?: "0.0%",
                                fontSize = FontType.SMALL,
                                color = MaterialTheme.mangalaColors.textSecondary,
                                modifier = Modifier.mangalaWalletPlaceholder(loadedState?.resourceUsedPercentage == null)
                            )
                        }
                    },
                    isRefreshing = isRefreshing,
                    onPullToRefresh = onPullToRefresh,
                    amountValue = loadedState?.amount,
                    unit = loadedState?.amountUnit.orEmpty(),
                    error = loadedState?.error,
                    onBackClicked = onBackClicked,
                    onValueChange = onAmountChange,
                    onClickButton = onClickPowerUp,
                    inputSectionEnabled = loadedState?.inputSectionEnabled == true
                )
            }
        }
    }

    @Composable
    private fun PowerUpExecuteSuccessState(
        onContinueTransaction: () -> Unit,
        onBackHome: () -> Unit
    ) {
        MaxSizeBox(
            modifier = Modifier.background(MaterialTheme.mangalaColors.bg)
        ) {
            ExecuteTransactionSuccess(
                onClickBack = {},
                textTitle = MR.strings.message_power_up_successfully.desc().localized(),
                modifier = Modifier.background(color = MaterialTheme.mangalaColors.bg),
            ) {
                MangalaGradientButton(
                    label = MR.strings.button_power_up_continue.desc().localized(),
                    onClick = onContinueTransaction,
                    enabled = true,
                    size = MangalaButtonSize.Medium,
                    modifier = Modifier.fillMaxWidth()
                )

                VerticalSpacer(Spacing.SMALL)

                MangalaTextButton(
                    label = MR.strings.button_transfer_ram_back_to_home.desc().localized(),
                    onClick = onBackHome,
                    size = MangalaButtonSize.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    style = MangalaTypography.Size14Medium()
                )
            }
        }
    }
}