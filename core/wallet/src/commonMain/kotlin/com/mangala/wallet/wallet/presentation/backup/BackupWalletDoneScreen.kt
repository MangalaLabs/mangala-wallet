package com.mangala.wallet.wallet.presentation.backup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.*
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class BackupWalletDoneScreen: BaseScreen<BackupWalletDoneScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.BACKUP_WALLET_DONE
    override val screenClassName: String = BackupWalletDoneScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): BackupWalletDoneScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: BackupWalletDoneScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val title = MR.strings.title_backed_up_wallet_done.desc().localized()
        val description1 = MR.strings.message_backed_up_wallet_done.desc().localized()
        val textButton = MR.strings.done.desc().localized().toUpperCase(Locale.current)

        BaseBackupWalletDoneScreen(title, description1, textButton,{
            navigator.popUntilRoot()
        },{
            navigator.pop()
        })
    }

    @Composable
    fun BaseBackupWalletDoneScreen(
        title: String,
        description1: String,
        textButton: String,
        onClickDone: (Boolean) -> Unit,
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
                    onClickDone(true)
                }
            }
        }
    }
}