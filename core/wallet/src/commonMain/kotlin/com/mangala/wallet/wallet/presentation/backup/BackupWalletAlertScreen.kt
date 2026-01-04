package com.mangala.wallet.wallet.presentation.backup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.*
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

data class BackupWalletAlertScreen(val blockchainUid: String, val antelopeAccountName: String?) :
    BaseScreen<BackupWalletAlertScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.BACKUP_WALLET_ALERT
    override val screenClassName: String = BackupWalletAlertScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): BackupWalletAlertScreenModel = getScreenModel()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: BackupWalletAlertScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current

        val title = MR.strings.title_backup_wallet_alert.desc().localized()
        val description1 = MR.strings.message_backup_wallet_alert.desc().localized()
        val textButton = MR.strings.backup_now.desc().localized()
        val textButton2 = MR.strings.i_will_risk_it.desc().localized()

        BaseBackupWalletScreen(
            title = title,
            description1 = description1,
            textButton = textButton,
            textButton2 = textButton2,
            onClickBackupNow = {
                val backupWalletGuideScreen = ScreenRegistry.get(
                    SharedScreen.BackupWalletGuideScreen(
                        1,
                        blockchainUid,
                        antelopeAccountName
                    )
                )

                navigator.push(backupWalletGuideScreen)
            },
            onClickWillRisk = {
                val homeScreen = ScreenRegistry.get(SharedScreen.HomeScreen())
                globalNavigator.replaceAll(homeScreen)
            }
        )
    }

    @Composable
    fun BaseBackupWalletScreen(
        title: String,
        description1: String,
        textButton: String,
        textButton2: String,
        onClickBackupNow: (Boolean) -> Unit,
        onClickWillRisk: (Boolean) -> Unit,
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

//            IconButton(onClick = {
//                onBackClicked(true)
//            }) {
//                Icon(
//                    imageVector = MangalaWalletPack.ArrowLeft,
//                    contentDescription = "Back"
//                )
//            }

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


            Column(modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL, bottom = Spacing.SMALL, top = Spacing.SMALL)) {
                ButtonNormal(textButton, enabled = true, modifier = Modifier.fillMaxWidth()) {
                    onClickBackupNow(true)
                }
            }

            Column(modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL, bottom = Spacing.SMALL, top = Spacing.SMALL)) {
                ButtonNormal(textButton2, enabled = true, modifier = Modifier.fillMaxWidth()) {
                    onClickWillRisk(true)
                }
            }
        }
    }

}