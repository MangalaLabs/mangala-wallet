package com.mangala.wallet.features.chains.antelope.presentation.createaccount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.features.chains.antelope_base.domain.model.CreateAccountRamOption
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class CreateAccountScreen(
    private val ownerPublicKey: ByteArray,
    private val activePublicKey: ByteArray
): BaseScreen<CreateAccountScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_UI
    override val screenClassName: String = CreateAccountScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @delegate:Transient
    private val scanQRCode: ScanQRCode by inject()
    @delegate:Transient
    private val parseQrCode: ParseQRCodeResultUseCase by inject()

    @Composable
    override fun createScreenModel(): CreateAccountScreenModel {
        return getScreenModel(
            parameters = {
                parametersOf(ownerPublicKey, activePublicKey)
            }
        )
    }

    @Composable
    override fun ScreenContent(screenModel: CreateAccountScreenModel) {
        val uiState = screenModel.uiState.collectAsState().value

        val composeUIWrapper = ComposeUIWrapper()

        Column {
            Text(MR.strings.message_create_account_screen_owner_public_key.format(uiState.ownerPublicKey).localized())
            Text(MR.strings.message_create_account_screen_active_public_key.format(uiState.activePublicKey).localized())
            when (uiState) {
                is CreateAccountScreenUiState.AccountNameNotConfirmed -> {
                    TextField(
                        value = uiState.accountName,
                        onValueChange = { screenModel.onAccountNameChanged(it) },
                        label = { Text(MR.strings.label_create_account_screen_account_name.desc().localized()) }
                    )
                    uiState.accountCharacterValidationResult?.let {
                        Text(MR.strings.message_create_account_screen_contains_only_valid_characters.format(it.containsOnlyValidCharacters).localized(), color = validationColor(it.containsOnlyValidCharacters))
                        Text(MR.strings.message_create_account_screen_exactly_length.format(it.isValidLength).localized(), color = validationColor(it.isValidLength))
                    }
                    uiState.error?.let {
                        Text(it, color = Color.Red)
                    }
                    Button(
                        onClick = { screenModel.onGenerateRandomAccountName() },
                    ) {
                        Text(MR.strings.button_create_account_screen_generate_random_account_name.desc().localized())
                    }
                    Button(
                        onClick = { screenModel.onConfirmAccountName() },
                        enabled = !uiState.isLoading && uiState.accountCharacterValidationResult?.isValid == true && uiState.error == null
                    ) {
                        Text(MR.strings.button_create_account_screen_confirm_account_name.desc().localized())
                    }
                }

                is CreateAccountScreenUiState.SelectNewAccountResourcePaymentOption -> {
                    Text(MR.strings.message_create_account_screen_account_name_available.format(uiState.accountName).localized())
                    Text(MR.strings.message_create_account_screen_pay_for_resource.desc().localized())
                    Text(MR.strings.message_create_account_screen_how_to_pay.desc().localized())
                    RadioButtonOption(MR.strings.message_create_account_screen_in_app_purchase.desc().localized(), uiState.accountCreationPaymentType == AccountCreationPaymentType.InAppPurchase) {
                        screenModel.onSelectIapResourcePaymentOption()
                    }
                    RadioButtonOption(MR.strings.message_create_account_screen_from_own_account.desc().localized(), uiState.accountCreationPaymentType is AccountCreationPaymentType.FromOwnAccount) {
                        screenModel.onSelectFromOwnAccountResourcePaymentOption()
                    }
                    if (uiState.accountCreationPaymentType is AccountCreationPaymentType.FromOwnAccount) {
                        RadioButtonOption(MR.strings.message_create_account_screen_buy_ram.desc().localized(), uiState.accountCreationPaymentType.ramOption == CreateAccountRamOption.BUY_RAM) {
                            screenModel.onSelectBuyRamResourcePaymentOption()
                        }
                        RadioButtonOption(MR.strings.message_create_account_screen_transfer_ram.desc().localized(), uiState.accountCreationPaymentType.ramOption == CreateAccountRamOption.TRANSFER_RAM) {
                            screenModel.onSelectTransferRamResourcePaymentOption()
                        }
                        uiState.encodedSignRequest?.let {
                            composeUIWrapper.QRCodeImage(it)
                        }
                    }
                    Button(
                        onClick = { screenModel.onCreateAccount() },
                        enabled = uiState.accountCreationPaymentType != null
                    ) {
                        Text(MR.strings.message_create_account_screen_create_account.desc().localized())
                    }
                }

                is CreateAccountScreenUiState.AccountCreated -> {
                    Text(MR.strings.message_create_account_screen_account_created.format(uiState.accountName).localized())

                    composeUIWrapper.QRCodeImage(uiState.encodedSyncAccountRequest)
                }
            }
        }
    }

    @Composable
    fun RadioButtonOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
        MaxWidthRow(Modifier.selectable(selected = isSelected, onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isSelected, onClick = onClick)
            Text(text)
        }
    }

    private fun validationColor(isValid: Boolean): Color {
        return if (isValid) Color.Green else Color.Red
    }
}