package com.mangala.wallet.features.chains.antelope.create_account.presentation.selectaccounttype

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step1.ListAccountType
import com.mangala.wallet.ui.component.CreateImportButton
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.component.MangalaButton
import com.mangala.wallet.ui.component.MangalaTopBarTitleInMiddle
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlin.jvm.Transient

class SelectAccountTypeScreen(
    private val initialAccountType: AccountNameType,
    @Transient val accountTypeSelected: (accountType: AccountNameType) -> Unit = {},
) : BaseScreen<SelectAccountTypeScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_SELECT_ACCOUNT_TYPE_BOTTOM_SHEET
    override val screenClassName: String = SelectAccountTypeScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): SelectAccountTypeScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: SelectAccountTypeScreenModel) {
        val bottomNavigator = LocalBottomSheetNavigator.current

        val accountType = remember { mutableStateOf(initialAccountType) }

        MaxWidthColumn(
            Modifier
                .fillMaxSize(Dimensions.fullScreenBottomSheetFraction)
                .background(Colors.appleBg)
                .safeDrawingPadding(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            MaxWidthColumn {
                MangalaTopBarTitleInMiddle(titleTopBar = MR.strings.title_select_account_type.desc().localized(), onBackClicked = {bottomNavigator.hide()})
                VerticalSpacer(Spacing.BASE)
                MaxWidthColumn(Modifier.padding(Dimensions.Padding.default)) {
                    ListAccountType(
                        accountType.value,
                        forceHideCreateAccountForFriend = false, // Always false, as create for friend option will navigate to a QR scanning screen instead
                        onClickItem = {
                            accountType.value = it
                        }
                    )
                }
            }
            MangalaButton(
                label = MR.strings.all_continue.desc().localized(),
                onClick = {
                    accountTypeSelected(accountType.value)
                },
                modifier = Modifier.padding(horizontal = Dimensions.Padding.default).fillMaxWidth(),
                style = MangalaTypography.Size17Medium(),
                disabledBackgroundColor = Color.White,
                disabledContentColor = Colors.mistGray,
            )
        }
    }
}