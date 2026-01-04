package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.selectaccountname

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step2.Step2SelectAccountNameScreenModel
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step2.Step2SelectAccountNameUiState
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.SelectAccountName
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.component.CreateImportButton
import com.mangala.wallet.ui.component.MangalaButton
import com.mangala.wallet.ui.component.MangalaTopBarTitleInMiddle
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class SelectAccountNameBottomSheetScreen(
    private val initialAccountName: String,
    private val initialAccountSuffix: String?,
    private val initialAccountType: AccountNameType,
    @Transient private val onSelectAccountName: (String) -> Unit
) : BaseScreen<Step2SelectAccountNameScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_SELECT_ACCOUNT_NAME_BOTTOM_SHEET
    override val screenClassName: String = SelectAccountNameBottomSheetScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): Step2SelectAccountNameScreenModel {
        return getScreenModel<Step2SelectAccountNameScreenModel> { parametersOf(initialAccountName, initialAccountSuffix, initialAccountType) }
    }

    @Composable
    override fun ScreenContent(screenModel: Step2SelectAccountNameScreenModel) {
        val bottomNavigator = LocalBottomSheetNavigator.current

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        MaxWidthColumn(
            Modifier.fillMaxSize(Dimensions.fullScreenBottomSheetFraction)
                .background(Colors.appleBg)
                .pointerInput(Unit) {
                    detectTapGestures {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                }, verticalArrangement = Arrangement.SpaceBetween
        ) {
            MaxWidthColumn {
                MangalaTopBarTitleInMiddle(
                    titleTopBar = MR.strings.title_step_2_select_account_name.desc().localized(),
                    onBackClicked = { bottomNavigator.hide() }
                )
                MaxWidthColumn(
                    Modifier.padding(Dimensions.Padding.default).verticalScroll(
                        rememberScrollState()
                    )
                ) {
                    VerticalSpacer(Spacing.BASE)
                    if (uiState is Step2SelectAccountNameUiState.Ready) {
                        SelectAccountName(
                            uiState,
                            onClickSetAccountType = null, // Disable option to change account type
                            onAccountNameChange = screenModel::onAccountNameChange,
                            onClickSuggest = screenModel::suggestValidName
                        )
                    }
                }
            }
            MangalaButton(
                label = MR.strings.all_select.desc().localized(),
                onClick = {
                    val selectedAccountName =
                        (uiState as? Step2SelectAccountNameUiState.Ready)?.accountName?.text ?: return@MangalaButton
                    onSelectAccountName(selectedAccountName)
                },
                enabled = (uiState as? Step2SelectAccountNameUiState.Ready)?.isAccountNameValid == true,
                style = MangalaTypography.Size17Medium(),
                modifier = Modifier.padding(horizontal = Dimensions.Padding.default).fillMaxWidth(),
                disabledBackgroundColor = Colors.white,
                disabledContentColor = Colors.mistGray
            )
        }
    }
}