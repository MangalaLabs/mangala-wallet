package com.mangala.wallet.features.chains.antelope.presentation.backupaccount.keycert

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

class BackupWithKeyCertScreen : BaseScreen<BackupWithKeyCertScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_BACKUP_WITH_KEYCERT
    override val screenClassName: String = BackupWithKeyCertScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): BackupWithKeyCertScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: BackupWithKeyCertScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform()

        BackupWithKeyCertScreen(
            uiState.value
        )
    }

    @Composable
    fun BackupWithKeyCertScreen(
        uiState: BackupWithKeyCertUiState
    ) {
        val composeUIWrapper = remember { ComposeUIWrapper() }

        MaxSizeColumn(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
            when (uiState) {
                is BackupWithKeyCertUiState.Error -> {
                    Text(MR.strings.all_error.format(uiState.message).localized())
                }

                BackupWithKeyCertUiState.Loading -> {
                    Text(MR.strings.all_loading.desc().localized())
                }

                is BackupWithKeyCertUiState.Success -> {
                    Text(MR.strings.message_backup_with_key_cert_screen_permission_name.format(uiState.permissionName).localized())
                    Text(MR.strings.all_account_name_custom.format(uiState.accountName).localized())
                    Text(MR.strings.message_backup_with_key_cert_screen_encryption_words.format(uiState.encryptionWords).localized())
                    composeUIWrapper.QRCodeImage(
                        uiState.keyCertString,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}