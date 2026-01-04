package com.mangala.wallet.wallet.presentation.backup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription1
import com.mangala.wallet.ui.TextTitle3
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class BackupWalletGuideScreen(private val page: Int, private val blockchainUid: String, private val antelopeAccountName: String?) : Screen {

    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.BACKUP_WALLET_GUIDE + "Step$page",
                BackupWalletGuideScreen::class.simpleName.orEmpty()
            )
        })

        val navigator = LocalNavigator.currentOrThrow
        val backupWalletGuideScreen = rememberScreen(SharedScreen.BackupWalletGuideScreen(2, blockchainUid, antelopeAccountName))

        if (page == 1) {
            val title = MR.strings.title_backup_wallet_guide1.desc().localized()
            val description1 = MR.strings.description_backup_wallet_guide1.desc().localized()
            val textButton = MR.strings.back_up_now.desc().localized().toUpperCase(Locale.current)
            BaseBackupWalletGuideScreen(title, description1, textButton, {
                navigator.push(backupWalletGuideScreen)
            }, {
                navigator.pop()
            })
        } else if(page == 2) {
            val title = MR.strings.title_backup_wallet_guide2.desc().localized()
            val description1 = MR.strings.description_backup_wallet_guide2.desc().localized()
            val textButton = MR.strings.start.desc().localized().toUpperCase(Locale.current)
            BaseBackupWalletGuideScreen(title, description1, textButton, {
                val networkType = BlockchainType.fromUid(blockchainUid).networkType

                val unlockPinCase = when (networkType) {
                    NetworkType.EVM -> SharedScreen.UnlockPinScreen.SHOW_WORDS_PHRASE
                    NetworkType.ANTELOPE -> SharedScreen.UnlockPinScreen.BACKUP_ANTELOPE_ACCOUNT
                    NetworkType.BITCOIN -> TODO()
                    NetworkType.OTHER -> TODO()
                    NetworkType.UNSUPPORTED -> TODO()
                }

                val unlockPinScreen = ScreenRegistry.get(SharedScreen.UnlockPinScreen(unlockPinCase, antelopeAccountName = antelopeAccountName))
                navigator.push(unlockPinScreen)
            }, {
                navigator.pop()
            })
        }
    }

    @Composable
    fun BaseBackupWalletGuideScreen(
        title: String,
        description1: String,
        textButton: String,
        onClickBackupNow: (Boolean) -> Unit,
        onBackClicked: (Boolean) -> Unit,
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

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
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
            TextDescription1(
                text = description1,
                modifier = Modifier.padding(16.dp)
            )

            val spaceTop = 72.dp

            Spacer(modifier = Modifier.height(spaceTop))


            Column(
                modifier = Modifier.padding(
                    start = Spacing.SMALL,
                    end = Spacing.SMALL,
                    bottom = Spacing.SMALL,
                    top = Spacing.SMALL
                )
            ) {
                ButtonNormal(textButton, enabled = true, modifier = Modifier.fillMaxWidth()) {
                    onClickBackupNow(true)
                }
            }

        }
    }
}