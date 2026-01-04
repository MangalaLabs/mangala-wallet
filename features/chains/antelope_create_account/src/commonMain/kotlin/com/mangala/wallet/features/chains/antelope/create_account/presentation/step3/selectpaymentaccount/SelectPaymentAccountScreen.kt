package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.selectpaymentaccount

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.mangala.wallet.mokoresources.MR
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.component.CreateImportButton
import com.mangala.wallet.ui.component.MangalaButton
import com.mangala.wallet.ui.component.MangalaTopBarTitleInMiddle
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.modifier.roundedCornerItemShape
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class SelectPaymentAccountScreen(
    private val initialAccountName: String,
    @Transient private val onSelectAccount: (String) -> Unit
) : BaseScreen<SelectPaymentAccountScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_WITH_EXISTING_SELECT_PAYMENT_ACCOUNT
    override val screenClassName: String = SelectPaymentAccountScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): SelectPaymentAccountScreenModel {
        return getScreenModel<SelectPaymentAccountScreenModel> { parametersOf(initialAccountName) }
    }

    @Composable
    override fun ScreenContent(screenModel: SelectPaymentAccountScreenModel) {
        val bottomNavigator = LocalBottomSheetNavigator.current

        val uiState = screenModel.uiState.collectAsStateMultiplatform()

        MaxWidthColumn(
            Modifier.fillMaxSize(Dimensions.fullScreenBottomSheetFraction)
                .background(Colors.appleBg), verticalArrangement = Arrangement.SpaceBetween
        ) {
            MaxWidthColumn(Modifier.weight(1f)) {
                MangalaTopBarTitleInMiddle(
                    titleTopBar = MR.strings.title_select_payment_account.desc().localized(),
                    onBackClicked = {bottomNavigator.hide()}
                )
                LazyColumn(Modifier.fillMaxWidth().padding(Dimensions.Padding.default)) {
                    item(ITEM_HEADER_KEY) {
                        VerticalSpacer(Spacing.BASE)
                        TextSubTitle(
                            text = MR.strings.title_select_payment_account_pick.desc().localized(),
                            fontWeight = FontWeight.Medium,
                            color = Colors.darkDarkGray
                        )
                        VerticalSpacer(Spacing.BASE)
                    }
                    (uiState.value as? SelectPaymentAccountUiState.Ready)?.accounts?.let {
                        itemsIndexed(it, key = { index, item ->
                            item.accountName
                        }) { index, account ->
                            AccountItem(
                                shape = roundedCornerItemShape(it, index),
                                item = account,
                                onClick = { screenModel.onSelectAccount(it) },
                                isSelected = index == (uiState.value as? SelectPaymentAccountUiState.Ready)?.selectedAccountIndex
                            )
                            if (index < it.lastIndex) {
                                VerticalSpacer(1.dp)
                            }
                        }
                    }
                }
            }
            MangalaButton(
                label = MR.strings.all_select.desc().localized(),
                onClick = {
                    val selectedAccountName =
                        (uiState.value as? SelectPaymentAccountUiState.Ready)?.selectedAccount?.accountName
                            ?: return@MangalaButton

                    onSelectAccount(selectedAccountName)
                    bottomNavigator.hide()
                },
                style = MangalaTypography.Size17Medium(),
                modifier = Modifier.padding(horizontal = Dimensions.Padding.default).fillMaxWidth(),
                disabledBackgroundColor = Colors.white,
                disabledContentColor = Colors.mistGray
            )
        }
    }

    @Composable
    fun AccountItem(
        shape: Shape,
        item: AccountsUiModel,
        isSelected: Boolean,
        onClick: (AccountsUiModel) -> Unit
    ) {
        MaxWidthRow(
            modifier = Modifier
                .clip(shape)
                .background(Colors.white)
                .animateContentSize()
                .clickable(onClick = {
                    onClick(item)
                }).padding(
                    start = Dimensions.Padding.default
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val balanceVisible = isSelected || item.nativeCoinBalance != null
            MaxWidthRow(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = if (balanceVisible) Alignment.Top else Alignment.CenterVertically) {
                Column(Modifier.padding(vertical = Dimensions.Padding.medium).weight(1f)) {
                    TextDescription2(
                        text = item.accountName,
                        color = Colors.darkDarkGray
                    )
                    if (balanceVisible) {
                        VerticalSpacer(Spacing.XTINY)
                        MaxWidthRow(
                            Modifier.mangalaWalletPlaceholder(
                                item.nativeCoinBalance == null && isSelected,
                                modifier = Modifier.width(100.dp)
                            )
                        ) {
                            TextTiny(
                                text = item.nativeCoinBalance.orEmpty(),
                                color = Colors.caption
                            )
                            TextTiny(
                                text = " ",
                                color = Colors.caption
                            )
                            TextTiny(
                                text = MR.strings.label_select_payment_account_balance_available.desc()
                                    .localized(),
                                color = Colors.caption,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                RadioButton(
                    isSelected,
                    onClick = { onClick(item) },
                    colors = RadioButtonDefaults.colors(
                        unselectedColor = Colors.second,
                        selectedColor = Colors.second
                    ),
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }

    companion object {
        const val ITEM_HEADER_KEY = "header"
    }
}