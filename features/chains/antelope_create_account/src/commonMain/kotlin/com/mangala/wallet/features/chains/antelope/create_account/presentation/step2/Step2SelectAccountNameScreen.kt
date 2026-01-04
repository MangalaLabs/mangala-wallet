package com.mangala.wallet.features.chains.antelope.create_account.presentation.step2

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.Step3CreateAccountPaymentScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.SelectAccountName
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.createbyfriend.CreateByFriendBottomSheetUiState
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.CreateImportButton
import com.mangala.wallet.ui.component.GradientBackground
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaButton
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class Step2SelectAccountNameScreen(
    private val accountNameType: AccountNameType // Should only be read as initial state to pass in ScreenModel, as we allow users to change account type in this screen
) : BaseScreen<Step2SelectAccountNameScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_SELECT_ACCOUNT_NAME
    override val screenClassName: String = Step2SelectAccountNameScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): Step2SelectAccountNameScreenModel {
        return getScreenModel<Step2SelectAccountNameScreenModel> {
            parametersOf(
                "",
                null,
                accountNameType
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: Step2SelectAccountNameScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val uiState by screenModel.uiState.collectAsStateMultiplatform()

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.Small,
                topEnd = CornerRadius.Small
            )
        ) {
            val bottomNavigator = LocalBottomSheetNavigator.current

            AccountSetupScreen(
                uiState,
                onClickBack = {
                    navigator.pop()
                },
                onAccountNameChange = {
                    screenModel.onAccountNameChange(it)
                },
                onClickSuggest = {
                    screenModel.suggestValidName()
                },
                onClickSetAccountType = {
                    val screen = ScreenRegistry.get(
                        SharedScreen.SelectAccountTypeScreen(
                            it
                        ) { newAccountType ->
                            screenModel.onAccountTypeChange(newAccountType)
                            bottomNavigator.hide()
                        }
                    )
                    bottomNavigator.show(screen)
                },
                onClickCreate = {
                    val currentState = uiState as? Step2SelectAccountNameUiState.Ready ?: return@AccountSetupScreen
                    navigator.push(
                        Step3CreateAccountPaymentScreen(
                            initialAccountName = it,
                            initialAccountSuffix = currentState.accountNameSuffix,
                            initialAccountType = currentState.accountType
                        )
                    )
                },
                onAccountNameReset = {
                    screenModel.onAccountNameChange(TextFieldValue(""))
                },
                isDevelopmentEnvironment = screenModel.isDevelopmentEnvironment(),
                onClickCreateFromEvm = {
                    val currentState = uiState as? Step2SelectAccountNameUiState.Ready ?: return@AccountSetupScreen
                    navigator.push(
                        ScreenRegistry.get(
                            SharedScreen.CreateEosAccountViaEVMScreen(
                                accountName = currentState.accountName.text,
                                accountNameSuffix = currentState.accountNameSuffix,
                                accountNameType = currentState.accountType
                            )
                        )
                    )
                }
            )
        }
    }

    override val isBottomBarVisible: Boolean = false

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun AccountSetupScreen(
        uiState: Step2SelectAccountNameUiState,
        onClickBack: () -> Unit,
        onAccountNameChange: (TextFieldValue) -> Unit,
        onClickSuggest: () -> Unit,
        onClickSetAccountType: (AccountNameType) -> Unit,
        onClickCreate: (accountName: String) -> Unit,
        onAccountNameReset : () -> Unit,
        isDevelopmentEnvironment: Boolean,
        onClickCreateFromEvm: () -> Unit
    ) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        if (uiState is Step2SelectAccountNameUiState.Ready) {
            GradientBackground {
                MaxSizeColumn(
                    modifier =  Modifier.pointerInput(Unit) {
                        keyboardController?.hide()
                        detectTapGestures { focusManager.clearFocus() }
                    }
                    , verticalArrangement = Arrangement.SpaceBetween) {
                    MaxWidthColumn {
                        MangalaWalletTopBar(
                            modifier = Modifier.background(Color.Transparent),
                            text = "",
                            onBackClicked = onClickBack
                        )
                    }
                    SelectAccountName(
                        uiState,
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.default, top = Dimensions.Padding.extraLarge),
                        onClickSetAccountType,
                        onAccountNameChange,
                        onClickSuggest
                    )
                    val buttonTextResource =
                        if (accountNameType == AccountNameType.Premium) {
                            MR.strings.button_step_2_select_account_name_account_create_premium_account
                        } else {
                            MR.strings.button_step_2_select_account_name_account_create_account
                        }

                    MangalaButton(
                        label = buttonTextResource.desc().localized(),
                        onClick = {
                            onClickCreate(uiState.accountName.text)
                            onAccountNameReset()
                        },
                        enabled = uiState.isAccountNameValid,
                        modifier = Modifier.padding(horizontal = Dimensions.Padding.default).fillMaxWidth(),
                        style = MangalaTypography.Size17Medium(),
                        disabledBackgroundColor = Color.White,
                        disabledContentColor = Colors.mistGray
                    )

                    if (isDevelopmentEnvironment) {
                        VerticalSpacer(Spacing.XTINY)
                        TextButton(
                            onClick = onClickCreateFromEvm,
                            enabled = uiState.isAccountNameValid,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Colors.darkDarkGray,
                                disabledContentColor = Colors.mistGray
                            ),
                            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                                .defaultMinSize(minHeight = 44.dp)
                        ) {
                            Text(
                                text = MR.strings.title_create_from_evm.desc().localized(),
                                fontWeight = FontWeight.Medium,
                                fontFamily = getSfProFamilyFont(
                                    weight = FontWeight.Medium,
                                    fontStyle = FontStyle.Normal
                                ),
                                fontSize = FontType.REGULAR
                            )
                        }
                    }
                }
            }
        }
    }
}