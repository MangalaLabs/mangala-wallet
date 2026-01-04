package com.mangala.wallet.wallet.presentation.restore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.*
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class RestoreWalletGuideScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val restoreRecoveryPhraseScreen = rememberScreen(SharedScreen.RestoreRecoveryPhraseScreen())

        val title = MR.strings.button_restore_wallet.desc().localized()
        val description1 = MR.strings.message_restore_wallet.desc().localized()
        val description2 = ""
        val confirmMessage = MR.strings.button_restore_wallet.desc().localized()

        RestoreScreen(title, description1, {
            navigator.push(restoreRecoveryPhraseScreen)
        }){
            navigator.pop()
        }
    }

    @Composable
    fun RestoreScreen(
        title: String,
        description1: String,
        onBackClicked: (Boolean) -> Unit,
        onRestoreWalletClicked: (Boolean) -> Unit,
    ) {
        var isChecked by remember { mutableStateOf(false) }

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


            TextDescription1(
                text = description1,
                modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)
            )


            Spacer(modifier = Modifier.height(Spacing.SMALL))

            Column(modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)) {
                ButtonNormal(title, modifier = Modifier.fillMaxWidth()) {
                    onRestoreWalletClicked(true)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.SMALL))
        }
    }

}