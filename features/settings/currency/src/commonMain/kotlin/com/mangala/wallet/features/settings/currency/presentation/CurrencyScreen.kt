package com.mangala.wallet.features.settings.currency.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Check
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MangalaWalletSearchBarWithBorder
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.modifier.roundedCornersItemShape
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class CurrencyScreen : BaseScreen<CurrencyScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.CURRENCY
    override val screenClassName: String = CurrencyScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): CurrencyScreenModel {
        val currencies = listSupportedCurrencyFiat()
        return getScreenModel(
            parameters = {
                parametersOf(currencies)
            }
        )
    }

    @Composable
    override fun ScreenContent(screenModel: CurrencyScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        Currency(
            onBackClicked = { navigator.pop() },
            screenModel = screenModel
        )
    }

    @Composable
    fun Currency(
        onBackClicked: (Boolean) -> Unit, screenModel: CurrencyScreenModel
    ) {
        val searchText = screenModel.searchText.collectAsStateMultiplatform()
        val supportedCurrencies = screenModel.currenciesList.collectAsStateMultiplatform().value
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        OnboardingGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    }
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = MR.strings.all_currency.desc().localized(),
                    onBackClicked = { onBackClicked(true) }
                )

                Spacer(modifier = Modifier.height(Spacing.SMALL))

                Column(
                    Modifier
                        .padding(horizontal = Dimensions.Padding.default)
                        .fillMaxSize()
                ) {
                    MangalaWalletSearchBarWithBorder(
                        query = searchText.value,
                        placeholder = MR.strings.message_currency_search_currency.desc()
                            .localized(),
                        onQueryChange = screenModel::onSearchTextChanged
                    )

                    Spacer(modifier = Modifier.height(Spacing.SMALL))
                    if (supportedCurrencies.isEmpty()) {
                        Box(Modifier.fillMaxSize()) {
                            TextDescription2(
                                MR.strings.message_currency_search_currency_not_found.desc()
                                    .localized(),
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.mangalaColors.textSecondary
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small))) {
                            itemsIndexed(supportedCurrencies) { index, item ->
                                if (index != 0) Spacer(modifier = Modifier.height(1.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .roundedCornersItemShape(
                                            list = supportedCurrencies,
                                            currentIndex = index
                                        )
                                        .background(color = MaterialTheme.mangalaColors.bgInnerCard)
                                        .fillMaxWidth()
                                        .clickable {
                                            StringDesc.localeType =
                                                StringDesc.LocaleType.Custom(item.currency.code)
                                            screenModel.changeCurrency(item.currency)
                                        }
                                        .padding(Dimensions.Padding.default)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Image(
                                            imageVector = item.currencyIcon,
                                            contentDescription = null,
                                            modifier = Modifier.width(Dimensions.IconButtonSize)
                                                .height(Dimensions.IconButtonSize)
                                        )
                                        Spacer(modifier = Modifier.width(Spacing.TINY))
                                        TextDescription2(
                                            text = item.currencyName,
                                            color = MaterialTheme.mangalaColors.textPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(Spacing.SMALL))
                                    }

                                    if (item.isSelected) {
                                        Icon(
                                            imageVector = MangalaWalletPack.Check,
                                            tint = MaterialTheme.mangalaColors.iconPrimary,
                                            contentDescription = "Selected",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}
