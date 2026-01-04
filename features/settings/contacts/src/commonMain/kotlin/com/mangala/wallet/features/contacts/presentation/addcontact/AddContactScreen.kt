package com.mangala.wallet.features.contacts.presentation.addcontact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Scan
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.RecipientValidationStatus
import com.mangala.wallet.ui.component.DataInput
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.component.MangalaWalletDropdown
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.MangalaWalletTextField
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import io.github.aakira.napier.Napier
import io.ktor.http.parametersOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class AddContactScreen(
    private val id: Long,
    private val name: String,
    private val blockchainUid: String,
    private val address: String,
    private val isEdit: Boolean,
) : BaseScreen<AddContactScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.ADD_CONTACTS
    override val screenClassName: String = AddContactScreen::class.simpleName.orEmpty()

    private val scanQRCode: ScanQRCode by inject()

    @Composable
    override fun createScreenModel(): AddContactScreenModel = getScreenModel(parameters = {
        parametersOf(name, address, blockchainUid)
    })

    override val isBottomBarVisible: Boolean = false

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: AddContactScreenModel) {
        val parentNavigator = LocalNavigator.currentOrThrow

        BottomSheetNavigator {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            val clipboardManager = LocalClipboardManager.current
            val focusManager = LocalFocusManager.current


            val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

            val topAppBarTitle = if (isEdit) {
                MR.strings.title_edit_contacts_top_app_bar.desc().localized()
            } else MR.strings.all_add_contact.desc().localized()

            AddContactScreen(
                uiModel = uiModel,
                topAppBarTitle = topAppBarTitle,
                onNameChanged = screenModel::onNameChanged,
                onClickSelectNetwork = {
                    val screen = ScreenRegistry.get(
                        SharedScreen.NetworkBottomSheetScreen(
                            selectedNetwork = uiModel.network,
                            onItemSelected = {
                                screenModel.onNetworkSelected(it)
                            }
                        ))
                    bottomSheetNavigator.show(screen)
                },
                onAddressChanged = screenModel::onAddressChanged,
                onClickScanAddress = {
                    scanQRCode.scanQRCode(object : ScanQRCodeListener {
                        override fun onScanQRCodeResult(result: String) {
                            screenModel.onAddressChanged(result)
                        }
                    })
                },
                onClickPasteAddress = {
                    val clipboardText = clipboardManager.getText()?.text ?: ""
                    focusManager.clearFocus()
                    screenModel.onAddressChanged(clipboardText)
                },
                onClickSave = {
                    if (isEdit){
                        screenModel.onUpdateContact(id)
                    } else {
                        screenModel.onSaveContact()
                    }
                    parentNavigator.pop()
                },
                onBackClicked = { parentNavigator.pop() },
                onDoneAddress = {
                    screenModel.onDoneAddress()
                    focusManager.clearFocus()
                }
            )
        }
    }

    @Composable
    private fun AddContactScreen(
        uiModel: AddContactScreenUiModel,
        topAppBarTitle: String,
        onNameChanged: (String) -> Unit,
        onClickSelectNetwork: () -> Unit,
        onAddressChanged: (String) -> Unit,
        onClickScanAddress: () -> Unit,
        onClickPasteAddress: () -> Unit,
        onClickSave: () -> Unit,
        onBackClicked: () -> Unit,
        onDoneAddress: () -> Unit
    ) {
        Column(
            Modifier.fillMaxSize().background(Colors.cloudGray).windowInsetsPadding(WindowInsets.safeDrawing),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            MangalaWalletTopBar(
                text = topAppBarTitle,
                onBackClicked = onBackClicked
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = Dimensions.Padding.default)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(Spacing.SMALL))
                DataInput(
                    label = MR.strings.message_add_contact_name_label.desc().localized(),
                    inputField = {
                        MangalaWalletTextField(
                            value = uiModel.name,
                            hint = MR.strings.message_add_contact_name_hint.desc().localized(),
                            onValueChange = onNameChanged,
                        )
                    }
                )
                Spacer(Modifier.height(Spacing.SMALL))
                DataInput(
                    label = MR.strings.all_select_network.desc().localized(),
                    inputField = {
                        MangalaWalletDropdown(
                            value = uiModel.network?.name.orEmpty(),
                            hint = MR.strings.all_select_network.desc().localized(),
                            fontSize = FontType.REGULAR,
                            hintColor = Colors.stroke,
                            textColor = Colors.main1Text,
                            onClick = onClickSelectNetwork
                        )
                    }
                )
                Spacer(Modifier.height(Spacing.SMALL))
                DataInput(
                    label = MR.strings.all_address.desc().localized(),
                    inputField = {
                        MangalaWalletTextField(
                            value = uiModel.address,
                            hint = MR.strings.message_add_contact_enter_address.desc()
                                .localized(),
                            onValueChange = onAddressChanged,
                            trailingIcon = {
                                Row {
                                    if (uiModel.recipientValidationStatus is RecipientValidationStatus.Validating) {
                                        CircularProgressIndicator(
                                            Modifier.size(Dimensions.IconButtonSize),
                                            color = Colors.caption
                                        )
                                    } else {
                                        if (uiModel.address.isEmpty()) {
                                            MangalaWalletIconButton(
                                                icon = MangalaWalletPack.Scan,
                                                tint = Colors.caption,
                                                onClick = onClickScanAddress,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(Modifier.width(Spacing.SMALL))
                                            MangalaWalletIconButton(
                                                icon = MangalaWalletPack.Copy,
                                                tint = Colors.caption,
                                                onClick = onClickPasteAddress,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else {
                                            MangalaWalletIconButton(
                                                icon = MangalaWalletPack.Clear,
                                                tint = Colors.caption,
                                                onClick = { onAddressChanged("") },
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    onDoneAddress()
                                }
                            )
                        )
                    }
                )
                if (uiModel.isAddressErrorVisible) {
                    Spacer(Modifier.height(Spacing.XTINY))
                    TextTiny(
                        text = MR.strings.message_add_contact_address_error.desc().localized(),
                        color = Colors.main2
                    )
                }
            }

            Row(Modifier.padding(Dimensions.Padding.default)) {
                ButtonNormal(
                    enabled = uiModel.saveButtonEnabled,
                    text = MR.strings.all_save.desc().localized(),
                    onClick = onClickSave,
                    buttonModifier = Modifier.height(44.dp).fillMaxWidth(),
                    modifier = Modifier.background(MaterialTheme.mangalaColors.bg)
                )
            }
        }
    }
}