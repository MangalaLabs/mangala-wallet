package com.mangala.wallet.features.chains.antelope.presentation.importaccount.keycert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import org.koin.core.parameter.parametersOf

class ImportAccountByKeyCertScreen(private val keyCert: String) :
    BaseScreen<ImportAccountByKeyCertScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_IMPORT_ACCOUNT_KEYCERT
    override val screenClassName: String = ImportAccountByKeyCertScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ImportAccountByKeyCertScreenModel {
        return getScreenModel(parameters = {
            parametersOf(keyCert)
        })
    }

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: ImportAccountByKeyCertScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        ImportAccountByKeyCertScreen(
            uiState,
            mnemonic = screenModel.mnemonic.value,
            onBackPressed = {
                navigator.pop()
            },
            onMnemonicChange = { mnemonic ->
                screenModel.onMnemonicChanged(mnemonic)
            },
            onImportClicked = {
                screenModel.onClickImport()
            },
            onCreateNewActiveKey = {
                screenModel.onCreateNewActiveKey()
            },
            onClickSetupPin = {
                if (screenModel.onGetIsPinSetup()) {
                    val homeScreen = ScreenRegistry.get(SharedScreen.HomeScreen())
                    navigator.replaceAll(homeScreen)
                } else {
                    val setupPinScreen =
                        ScreenRegistry.get(
                            SharedScreen.SetupPinScreen(
                                antelopeAccountName = screenModel.getAccountName(),
                                blockchainUid = screenModel.getBlockchainUid(),
                                pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_BACKUP_ANTELOPE.name
                            )
                        )
                    navigator.push(setupPinScreen)
                }
            }
        )
    }

    @Composable
    fun ImportAccountByKeyCertScreen(
        uiState: ImportAccountByKeyCertUiState,
        mnemonic: String,
        onBackPressed: () -> Unit,
        onMnemonicChange: (String) -> Unit,
        onImportClicked: () -> Unit,
        onCreateNewActiveKey: () -> Unit,
        onClickSetupPin: () -> Unit
    ) {
        MaxSizeColumn(Modifier.background(Color.Gray).windowInsetsPadding(WindowInsets.safeDrawing)) {
            MangalaWalletTopBar(
                modifier = Modifier.background(Color.Transparent),
                text = MR.strings.title_import_account_by_key_cert_screen_import_key_from_cert.desc().localized(),
                onBackClicked = onBackPressed
            )

            MaxWidthColumn(Modifier.verticalScroll(rememberScrollState())) {
                when (uiState) {
                    is ImportAccountByKeyCertUiState.CreateSuccess -> {
                        Text(MR.strings.message_import_account_by_key_cert_screen_account_imported_successfully.desc().localized())
                        Text(MR.strings.message_import_account_by_key_cert_screen_ask_create_new_active_key.desc().localized())
                        Text(MR.strings.message_import_account_by_key_cert_screen_warning.desc().localized())
                        Button(
                            onClick = {
                                onCreateNewActiveKey()
                            }
                        ) {
                            Text(MR.strings.button_import_account_by_key_cert_screen_create.desc().localized())
                        }
                    }

                    is ImportAccountByKeyCertUiState.Error -> {
                        Text(MR.strings.all_error.format(uiState.message).localized())
                    }

                    ImportAccountByKeyCertUiState.Loading -> {
                        Text(MR.strings.all_loading.desc().localized())
                    }

                    is ImportAccountByKeyCertUiState.Success -> {
                        Text(MR.strings.message_import_account_by_key_cert_screen_account_name.format(uiState.accountName).localized())
                        Text(MR.strings.message_import_account_by_key_cert_screen_permission_name.format(uiState.permissionName).localized())
                        Text(MR.strings.message_import_account_by_key_cert_screen_network.format(uiState.blockchainType.name).localized())
                        VerticalSpacer(16.dp)
                        TextField(
                            mnemonic,
                            onValueChange = onMnemonicChange,
                            placeholder = {
                                Text(MR.strings.message_import_account_by_key_cert_screen_mnemonic.desc().localized())
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            )
                        )
                        VerticalSpacer(20.dp)
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.Black)
                        }
                        Button(
                            onClick = onImportClicked,
                            enabled = mnemonic.isNotBlank() && !uiState.isLoading
                        ) {
                            Text(MR.strings.button_import_account_by_key_cert_screen_import.desc().localized())
                        }
                    }

                    ImportAccountByKeyCertUiState.CreatePermissionSuccess -> {
                        Text(MR.strings.button_import_account_by_key_cert_screen_create_and_save_new_permission_success.desc().localized())
                        Button(
                            onClick = {
                                onClickSetupPin()
                            }
                        ) {
                            Text(MR.strings.button_import_account_by_key_cert_screen_setup_pin.desc().localized())
                        }
                    }
                }
            }
        }
    }
}