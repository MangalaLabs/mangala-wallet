package com.mangala.wallet.features.nft_base.presentation.send

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.ui.NewRecipientInfo
import com.mangala.wallet.features.chains.ui.SaveRecipientSwitch
import com.mangala.wallet.ui.SelectAddress
import com.mangala.wallet.features.chains.ui.SendToContactButton
import com.mangala.wallet.features.nft_base.presentation.ui.NftImage
import com.mangala.wallet.features.nft_base.presentation.ui.NftImageType
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class SendNftScreen(
    private val accountId: String,
    private val collectionContractAddress: String,
    private val tokenId: String
) : BaseScreen<SendNftScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.EVM_SEND_NFT
    override val screenClassName: String = SendNftScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean = false

    @delegate:Transient
    private val scanQRCode: ScanQRCode by inject()

    @Composable
    override fun createScreenModel(): SendNftScreenModel {
        return getScreenModel(
            parameters = {
                parametersOf(
                    accountId,
                    collectionContractAddress,
                    tokenId
                )
            }
        )
    }

    @Composable
    override fun ScreenContent(screenModel: SendNftScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val clipboardManager = LocalClipboardManager.current
        val focusManager = LocalFocusManager.current

        val importNftScreen = rememberScreen(SharedScreen.ImportNftScreen)

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        SendNftScreen(
            uiState,
            onClickBack = {
                navigator.pop()
            },
            onClickContinue = {
                screenModel.onClickContinue()
            },
            onContactFilterChange = screenModel::onContactFilterChange,
            onContactSelected = {
                val screen = ScreenRegistry.get(
                    SharedScreen.SendNftConfirmationScreen(
                        blockchainUid = screenModel.blockchainUid,
                        contactId = it.id,
                        accountId = accountId,
                        recipientAddress = (uiState as? SendNftScreenUiState.Success)?.uiModel?.address.orEmpty(),
                        collectionContractAddress = collectionContractAddress,
                        tokenId = tokenId
                    )
                )
                navigator.push(screen)
            },
            onAddressChange = screenModel::onAddressChange,
            onClickScanQrCode = {
                scanQRCode.scanQRCode(object : ScanQRCodeListener {
                    override fun onScanQRCodeResult(result: String) {
                        screenModel.onAddressChange(result)
                        focusManager.clearFocus()
                    }
                })
            },
            onDoneAddress = {
                screenModel.setDoneSelectAddress(it)
                if (it) {
                    focusManager.clearFocus()
                }
            },
            onClickPaste = {
                val clipboardText = clipboardManager.getText()?.text.orEmpty()
                screenModel.onAddressChange(clipboardText)
                focusManager.clearFocus()
            },
            onToggleSaveRecipient = {
                screenModel.onToggleSaveRecipient(it)
            },
            onNewRecipientNameChange = {
                screenModel.onNewRecipientNameChange(it)
            },
            onDoneEnterInfo = {
                val uiModel = (uiState as? SendNftScreenUiState.Success)?.uiModel
                val screen = ScreenRegistry.get(
                    SharedScreen.SendNftConfirmationScreen(
                        blockchainUid = screenModel.blockchainUid,
                        contactId = uiModel?.contactId,
                        accountId = accountId,
                        recipientAddress = uiModel?.address.orEmpty(),
                        collectionContractAddress = collectionContractAddress,
                        tokenId = tokenId,
                    )
                )
                navigator.push(screen)
                screenModel.onConsumeIsDoneEnterInfo()
            },
            onClickSendToContact = {
                val screen = ScreenRegistry.get(SharedScreen.ContactsScreen(
                    blockchainUid = screenModel.blockchainUid,
                    onSelectContact = {
                        screenModel.onSelectContact(it)
                        navigator.pop()
                    }
                ))
                navigator.push(screen)
            },
            onDoneSelectRecipientName = {
                screenModel.onDoneSelectRecipientName(it)
            }
        )
    }

    @Composable
    fun SendNftScreen(
        uiState: SendNftScreenUiState,
        onContactFilterChange: (String) -> Unit,
        onContactSelected: (ContactEntity) -> Unit,
        onAddressChange: (String) -> Unit,
        onDoneAddress: (Boolean) -> Unit,
        onClickScanQrCode: () -> Unit,
        onClickPaste: () -> Unit,
        onToggleSaveRecipient: (Boolean) -> Unit,
        onNewRecipientNameChange: (String) -> Unit,
        onDoneSelectRecipientName: (Boolean) -> Unit,
        onDoneEnterInfo: (Boolean) -> Unit,
        onClickSendToContact: () -> Unit,
        onClickBack: () -> Unit,
        onClickContinue: () -> Unit
    ) {
        val focusManager = LocalFocusManager.current

        val addressFocusRequester = remember { FocusRequester() }
        val nameFocusRequester = remember { FocusRequester() }
        val isAddressFocus = remember { mutableStateOf(false) }

        MaxSizeColumn(Modifier.background(MaterialTheme.colors.background).windowInsetsPadding(WindowInsets.safeDrawing)) {
            MangalaWalletTopBar(
                text = MR.strings.title_nft_send.desc().localized(),
                onBackClicked = onClickBack
            )
            VerticalSpacer(Spacing.BASE)
            Column(Modifier.weight(1f)) {
                if (uiState is SendNftScreenUiState.Success) {
                    val uiModel = uiState.uiModel

                    LaunchedEffect(uiModel.isDoneEnterInfo) {
                        if (uiModel.isDoneEnterInfo) {
                            onDoneEnterInfo(true)
                        }
                    }

                    LazyColumn(
                        Modifier.fillMaxSize()
                    ) {
                        item(ITEM_NFT_IMAGE_KEY, contentType = ITEM_NFT_IMAGE_KEY) {
                            MaxWidthRow(horizontalArrangement = Arrangement.Center) {
                                NftImage(
                                    nftCollectionName = uiModel.nftCollection.contractName,
                                    nft = uiState.uiModel.nftCollection.nft.first(),
                                    nftImageType = NftImageType.SMALL
                                )
                            }
                        }
                        item(ITEM_MESSAGE_KEY, contentType = ITEM_MESSAGE_KEY) {
                            VerticalSpacer(Spacing.BASE)
                            TextNormal(
                                modifier = Modifier.padding(horizontal = Dimensions.ScreenSendNftPadding),
                                text = MR.strings.message_nft_send_you_want_to_transfer.desc()
                                    .localized(),
                                fontWeight = FontWeight.W500,
                                color = Colors.darkDarkGray
                            )
                        }
                        item(ITEM_ADDRESS_KEY, contentType = ITEM_ADDRESS_KEY) {
                            SelectAddress(
                                modifier = Modifier.padding(horizontal = Dimensions.ScreenSendNftPadding),
                                address = if (uiModel.address.isBlank()) "" else try { Address(uiModel.address).eip55 } catch (e: Exception) { uiModel.address },
                                isValidAddress = uiModel.isValidAddress,
                                isAddressFocus = isAddressFocus.value,
                                addressFocusRequester = addressFocusRequester,
                                onAddressChange = onAddressChange,
                                onFocusChanged = { isFocus ->
                                    if (isFocus) {
                                        isAddressFocus.value = true
                                        if (uiModel.isDoneSelectAddress) {
                                            onDoneAddress(false)
                                        }
                                    } else {
                                        isAddressFocus.value = false
                                    }
                                },
                                onDoneAddress = {
                                    onDoneAddress(true)
                                },
                                onClickScanQRCode = onClickScanQrCode,
                                onClickPaste = onClickPaste,
                                networkType = uiState.uiModel.blockchainType.networkType
                            )
                        }
                        if (uiModel.address.isBlank()) {
                            item(ITEM_SEND_TO_CONTACTS_KEY, contentType = ITEM_SEND_TO_CONTACTS_KEY) {
                                Column {
                                    VerticalSpacer(Spacing.SMALL)
                                    SendToContactButton(onClickSendToContact, modifier = Modifier.padding(horizontal = Dimensions.ScreenSendNftPadding))
                                }
                            }
                        }
                        if (uiModel.saveRecipientSwitchEnabled) {
                            item(ITEM_SAVE_RECIPIENT_KEY, contentType = ITEM_SAVE_RECIPIENT_KEY) {
                                SaveRecipientSwitch(
                                    modifier = Modifier.padding(Dimensions.ScreenSendNftHalfPadding),
                                    isChecked = uiModel.isSaveRecipientEnabled,
                                    onCheckedChange = onToggleSaveRecipient
                                )
                            }
                            if (uiModel.isSaveRecipientEnabled) {
                                item(ITEM_NEW_RECIPIENT_KEY, contentType = ITEM_NEW_RECIPIENT_KEY) {
                                    NewRecipientInfo(
                                        selectedName = uiModel.newRecipientName,
                                        onNewRecipientNameChange = onNewRecipientNameChange,
                                        onDoneSelectName = onDoneSelectRecipientName,
                                        focusManager = focusManager,
                                        doneSelectName = uiModel.isDoneEnterRecipientName,
                                        nameFocusRequester = nameFocusRequester,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Footer(uiState, onClickContinue)
        }
    }

    @Composable
    private fun Footer(
        uiState: SendNftScreenUiState,
        onClickContinue: () -> Unit
    ) {
        ButtonNormal(
            MR.strings.all_continue.desc().localized(),
            enabled = uiState is SendNftScreenUiState.Success && uiState.uiModel.continueButtonEnabled,
            buttonModifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = Dimensions.Padding.default,
                    horizontal = Dimensions.ScreenSendNftPadding
                )
        ) {
            onClickContinue()
        }
    }

    companion object {
        const val ITEM_NFT_IMAGE_KEY = "nft-image"
        const val ITEM_MESSAGE_KEY = "message"
        const val ITEM_ADDRESS_KEY = "address"
        const val ITEM_SEND_TO_CONTACTS_KEY = "send-to-contacts"
        const val ITEM_SAVE_RECIPIENT_KEY = "save-recipient"
        const val ITEM_NEW_RECIPIENT_KEY = "new-recipient"
    }
}