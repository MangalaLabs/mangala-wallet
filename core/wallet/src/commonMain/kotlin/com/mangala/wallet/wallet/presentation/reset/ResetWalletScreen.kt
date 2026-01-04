package com.mangala.wallet.wallet.presentation.reset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.*
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class ResetWalletScreen: Screen {

    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.EVM_RESET_WALLET,
                ResetWalletScreen::class.simpleName.orEmpty()
            )
        })

        val navigator = LocalNavigator.currentOrThrow

        val title = MR.strings.reset_wallet.desc().localized()
        val description1 = MR.strings.title_reset_wallet.desc().localized()
        val description2 = MR.strings.message_reset_wallet.desc().localized()
        val confirmMessage = MR.strings.confirm_reset_wallet.desc().localized()

        ResetScreen(title, description1, description2, confirmMessage){
            navigator.pop()
        }
    }


}

@Composable
fun ResetScreen(
    title: String,
    description1: String,
    description2: String,
    confirmMessage: String,
    onBackClicked: (Boolean) -> Unit
) {
    var isChecked by remember { mutableStateOf(false) }

    MaxSizeColumn(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(Spacing.TINY))

        IconButton(onClick = {
            onBackClicked(true)
        }) {
            Icon(
                imageVector = MangalaWalletPack.ArrowLeft,
                contentDescription = "Back"
            )
        }

        TextTitle3(
            text = title,
            modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        TextSubTitle(
            text = description1,
            modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        TextDescription1(
            text = description2,
            modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = Spacing.SMALL)
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.onPrimary,
                    uncheckedColor = MaterialTheme.colors.secondary
                )
            )
            TextDescription1(text = confirmMessage)
        }
        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Column(modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)) {
            ButtonNormal(title, enabled = isChecked, modifier = Modifier.fillMaxWidth()) {

            }
        }

        Spacer(modifier = Modifier.height(Spacing.SMALL))
    }
}