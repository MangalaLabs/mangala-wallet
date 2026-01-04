package com.mangala.wallet.pin.presentation.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.*
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class ForgotPinScreen : Screen {

    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.FORGOT_PIN,
                ForgotPinScreen::class.simpleName.orEmpty()
            )
        })

        val navigator = LocalNavigator.currentOrThrow
//        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val resetWalletScreen = rememberScreen(SharedScreen.ResetWalletScreen)
        val restoreWalletScreen = rememberScreen(SharedScreen.RestoreWalletScreen)

        ForgotPinScreen({
            navigator.push(resetWalletScreen)
        }, {
            navigator.push(restoreWalletScreen)
        }, {
//            bottomSheetNavigator.hide()
            navigator.pop()
        })

    }

    @Composable
    private fun ForgotPinScreen(
        onClickResetWallet: (Boolean) -> Unit,
        onClickRestoreWallet: (Boolean) -> Unit,
        onClickClose: (Boolean) -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            horizontalAlignment = Alignment.Start
        ) {

            Spacer(modifier = Modifier.height(Spacing.TINY))

            IconButton(onClick = {
                onClickClose(true)
            }) {
                Icon(
                    imageVector = MangalaWalletPack.ArrowLeft,
                    contentDescription = "Back"
                )
            }

            TextTitle3(
                text = MR.strings.forgot_pin.desc().localized(),
                modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)
            )

            Spacer(modifier = Modifier.height(Spacing.TINY))
            val description = MR.strings.message_forgot_pin.desc().localized()
            TextDescription1(
                text = description,
                modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)
            )

            Spacer(modifier = Modifier.height(Spacing.TINY))

            Box(
                modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL).clickable {
                    onClickResetWallet(true)
                }
            ) {
                val resetWallet = MR.strings.reset_wallet.desc().localized()

                val underlinedText = buildAnnotatedString {
                    append(resetWallet)
                    addStyle(
                        SpanStyle(textDecoration = TextDecoration.Underline),
                        0,
                        resetWallet.length
                    )
                }

                TextUnderLine(
                    text = underlinedText
                )
            }

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            Column(modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)) {
                val restoreWallet = MR.strings.button_restore_wallet.desc().localized().toUpperCase(Locale.current)
                ButtonNormal(restoreWallet) {
                    onClickRestoreWallet(true)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.SMALL))
        }
    }
}