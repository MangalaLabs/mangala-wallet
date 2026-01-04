package com.mangala.browser_bridge_base.personal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.browser_bridge_base.ConfirmTransactionViewModel
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.pin.presentation.unlock.UnlockPinScreen
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTitle4
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class SignPersonalMessageScreen(
    val url: String,
    val callbackId: Long,
    val message: ByteArray?,
    val onSign: (message: String) -> Unit,
    val onConfirm: (isOpenPin: Boolean) -> Unit,
    val onDecline: () -> Unit
) : BaseScreen<ConfirmTransactionViewModel>() {

    override val screenName: String = MangalaAnalytics.Screens.BROWSER_SIGN_PERSONAL_MESSAGE
    override val screenClassName: String = SignPersonalMessageScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ConfirmTransactionViewModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: ConfirmTransactionViewModel) {
        val myAddress: Address? = screenModel.getAddress()

        val globalNavigator = LocalGlobalNavigator.current
        val pinScreen =
            UnlockPinScreen(
                SharedScreen.UnlockPinScreen.CONFIRM_DAPP,
                unlockPinCallback = {
                    onConfirm(false)
                    if (it) {
                        screenModel.signPersonalMessage(message ?: ByteArray(0))
                    }
                },
                antelopeAccountName = null
            )

        val result by screenModel.signPersonalMessage.collectAsStateMultiplatform()
        result?.let {
            onSign(it.toHexString())
        }

        Column(
            modifier = Modifier.fillMaxWidth().background(Color.Transparent)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            horizontalAlignment = Alignment.Start
        ) {
            SignPersonalMessageContent(
                url = url,
                accountName = "Mangala",
                address = myAddress?.hex ?: "",
                onSign = {
                    globalNavigator.push(pinScreen)
                    onConfirm(true)
                },
                onDecline = {
                    onDecline()
                }
            )
        }

    }

    @Composable
    fun SignPersonalMessageContent(
        url: String,
        accountName: String,
        address: String,
        onSign: () -> Unit,
        onDecline: () -> Unit
    ) {
        val title = MR.strings.title_sign_personal_message.desc().localized()

        val message = StringDesc.ResourceFormatted(MR.strings.message_refer_detail, url).localized()

        val decline = MR.strings.decline.desc().localized()
        val sign = MR.strings.sign.desc().localized()
        val fromAccount = MR.strings.message_from.desc().localized() + " " + accountName

        Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(Spacing.SMALL)) {
            Spacer(modifier = Modifier.height(Spacing.TINY))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextTitle4(
                    text = title,
                )
                MangalaWalletIconButton(
                    icon = MangalaWalletPack.Clear,
                    modifier = Modifier.size(36.dp),
                    onClick = onDecline
                )
            }

            Text(
                text = url,
                color = MaterialTheme.colors.onSecondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = FontType.TITLE_3,
                fontFamily = fontFamilyResource(MR.fonts.sfpro),
            )

            Spacer(modifier = Modifier.height(Spacing.MEDIUM))

            TextNormal(message)

            Spacer(modifier = Modifier.height(Spacing.LARGE))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f).clickable {
                        onDecline()
                    },
                    contentAlignment = Alignment.Center
                ) {
                    TextNormal(text = decline)
                }
                Button(
                    onClick = { onSign() },
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = 2.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onSecondary)
                ) {
                    TextNormal(
                        text = sign,
                        color = Color.White
                    )
                }
            }
        }
    }

}