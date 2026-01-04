package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.TextTopBar
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class CryptoPaymentErrorScreen(
    private val error: String,
    private val errorDescription: String?,
    private val blockchainTypeUid: String?
) : BaseScreen<CryptoPaymentErrorScreenModel>() {
    override val isBottomBarVisible: Boolean
        get() = false

    override val screenName: String = MangalaAnalytics.Screens.PAY_WITH_CRYPTO_ERROR
    override val screenClassName: String = CryptoPaymentErrorScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): CryptoPaymentErrorScreenModel =
        getScreenModel<CryptoPaymentErrorScreenModel>()

    @Composable
    override fun ScreenContent(screenModel: CryptoPaymentErrorScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        MaxWidthColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.appleBg)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(
                    horizontal = Dimensions.Padding.default,
                )
            , verticalArrangement = Arrangement.SpaceBetween
        ) {
            MaxWidthColumn {
                TextTopBar(
                    text = MR.strings.title_pay_with_crypto_error.desc().localized(),
                    modifier = Modifier.padding(vertical = Dimensions.Padding.small)
                )
                MaxWidthColumn(
                    modifier = Modifier.padding(vertical = Dimensions.Padding.default),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    TextSubTitle(
                        text = MR.strings.title_pay_with_crypto_error_content.desc().localized(),
                        fontWeight = FontWeight.Medium,
                        color = Colors.darkDarkGray
                    )
                    VerticalSpacer(Spacing.SMALL)
                    Text(
                        text = error,
                        color = Colors.darkDarkGray
                    )
                    errorDescription?.let {
                        VerticalSpacer(Spacing.XSMALL)
                        Text(
                            text = errorDescription,
                            color = Colors.darkDarkGray
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            blockchainTypeUid?.let { screenModel.changeNetwork(it) }
                            navigator.replaceAll(ScreenRegistry.get(SharedScreen.HomeScreen()))
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Colors.darkDarkGray,
                            contentColor = Colors.white,
                            disabledBackgroundColor = Colors.white,
                            disabledContentColor = Colors.mistGray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 44.dp)
                            .padding(vertical = Dimensions.Padding.default),
                        shape = RoundedCornerShape(CornerRadius.Tiny)
                    ) {
                        Text(
                            text = MR.strings.button_payment_error_back_to_home_content.desc()
                                .localized(),
                        )
                    }
                }
            }
        }

    }
}