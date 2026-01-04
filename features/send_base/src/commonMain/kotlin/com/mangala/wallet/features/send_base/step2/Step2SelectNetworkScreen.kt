package com.mangala.wallet.features.send_base.step2

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Search
import com.mangala.wallet.features.chains.ui.NewRecipientInfo
import com.mangala.wallet.features.chains.ui.SaveRecipientSwitch
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.RecipientValidationStatus
import com.mangala.wallet.ui.SelectAddress
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.BasicTextFieldWithHintAndTrailingIcons
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.NetworkList
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class Step2SelectNetworkScreen(
    private val accountId: String,
    private val address: String? = null, // For passing address from QR code flow
    private val networkType: String
) : BaseScreen<Step2SelectNetworkScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.SEND_TOKEN_SELECT_NETWORK
    override val screenClassName: String = Step2SelectNetworkScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    private val scanQRCode: ScanQRCode by inject()

    @Composable
    override fun createScreenModel(): Step2SelectNetworkScreenModel {
        println("Step2SelectNetworkScreen - Creating ScreenModel with address: $address, networkType: $networkType")
        return getScreenModel(
            parameters = {
                parametersOf(
                    address,
                    NetworkType.valueOf(networkType)
                )
            }
        )
    }

    @Composable
    override fun ScreenContent(screenModel: Step2SelectNetworkScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val clipboardManager = LocalClipboardManager.current
        val focusManager = LocalFocusManager.current

        val sendNetworkAddressState by screenModel.sendNetworkAddressState.collectAsStateMultiplatform()
        if (sendNetworkAddressState != null) {
            val step3SelectAmountScreen = rememberScreen(
                SharedScreen.Step3SelectAmountScreen(
                    accountId = accountId,
                    contactId = sendNetworkAddressState?.contactId,
                    address = sendNetworkAddressState?.address,
                    blockchainUid = sendNetworkAddressState?.blockchainUid,
                    amount = null
                )
            )
            navigator.push(step3SelectAmountScreen)
            screenModel.clearState()
        }


        Body(
            screenModel,
            onClickScanQRCode = {
                scanQRCode.scanQRCode(object : ScanQRCodeListener {
                    override fun onScanQRCodeResult(result: String) {
                        screenModel.onPasteAddress(result)
                        focusManager.clearFocus()
                    }
                })
            },
            onClickPaste = {
                val clipboardText = clipboardManager.getText()?.text ?: ""
                screenModel.onPasteAddress(clipboardText)
                focusManager.clearFocus()
            },
            onClickContinue = {
                screenModel.clickContinue()

            },
            onClickBack = {
                navigator.pop()
            }
        )
    }

    @Composable
    fun Body(
        screenModel: Step2SelectNetworkScreenModel,
        onClickScanQRCode: () -> Unit,
        onClickPaste: () -> Unit,
        onClickContinue: () -> Unit,
        onClickBack: () -> Unit,
    ) {
        val selectedNetwork = screenModel.selectedNetwork.value
        val doneSelectNetwork = screenModel.doneSelectNetwork.value
        val showNetworkList = screenModel.showNetworkList.value
        val networkInput = remember { mutableStateOf("") }
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value


        val selectedAddress = screenModel.selectedAddress.value
        val doneSelectAddress = screenModel.doneSelectAddress.value

        val isAddressFocus = remember { mutableStateOf(false) }
        val isNetworkFocus = remember { mutableStateOf(true) }

        val selectedName = screenModel.selectedName.value
        val doneSelectName = screenModel.doneSelectAddress.value

        val networkFocusRequester = remember { FocusRequester() }
        val addressFocusRequester = remember { FocusRequester() }
        val nameFocusRequester = remember { FocusRequester() }

//        val keyboardController: SoftwareKeyboardController? =
//            LocalSoftwareKeyboardController.current

        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val switchState = remember { mutableStateOf(false) }

//        val scrollState = rememberScrollState()

        OnboardingGradientBackground {
            MaxSizeColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    }
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = MR.strings.message_send_token_send_token.desc().localized(),
                    onBackClicked = onClickBack
                )
                MaxWidthColumn(Modifier.weight(1f)) {
                    Spacer(modifier = Modifier.height(Spacing.XLARGE))
                    MaxSizeColumn(
                        modifier = Modifier.padding(bottom = Spacing.SMALL),
                    ) {
                        MaxWidthColumn(Modifier.verticalScroll(rememberScrollState())) {
                            TextNormal(
                                MR.strings.message_send_token_you_want_to_transfer_token.desc()
                                    .localized(),
                                modifier = Modifier.fillMaxWidth()
                                    .padding(horizontal = Spacing.SMALL),
                                color = MaterialTheme.mangalaColors.textPrimary,
                                fontWeight = FontWeight.W500
                            )
                            MaxWidthRow(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = Spacing.SMALL)
                            ) {
                                TextNormal(
                                    MR.strings.message_send_token_on.desc().localized(),
                                    color = MaterialTheme.mangalaColors.textPrimary,
                                    fontWeight = FontWeight.W500
                                )
                                HorizontalSpacer(Spacing.TINY)
                                MaterialTheme(
                                    colorScheme = MaterialTheme.colorScheme.copy(primary = MaterialTheme.mangalaColors.iconPrimary)
                                ) {
                                    NetworkSearchTextField(
                                        networkInput,
                                        screenModel,
                                        doneSelectNetwork,
                                        isNetworkFocus,
                                        networkFocusRequester
                                    )
                                }
                            }
                            LaunchedEffect(selectedNetwork == null) {
                                networkFocusRequester.requestFocus()
//                    keyboardController?.hide()
                            }

                            if (doneSelectNetwork) {
                                var animate by remember { mutableStateOf(false) }

                                SelectAddress(
                                    modifier = Modifier.padding(
                                        start = Spacing.SMALL,
                                        end = Spacing.SMALL
                                    ),
                                    address = selectedAddress,
                                    recipientValidationStatus = uiState.recipientValidationStatus,
                                    isAddressFocus = isAddressFocus.value,
                                    addressFocusRequester = addressFocusRequester,
                                    onAddressChange = {
                                        screenModel.onAddressChange(it)
                                    },
                                    onFocusChanged = {
                                        if (it) {
                                            isAddressFocus.value = true
                                            if (doneSelectAddress) {
                                                screenModel.doneSelectAddress.value = false
                                            }
                                        } else {
                                            isAddressFocus.value = false
                                        }
                                    },
                                    onDoneAddress = {
//                                keyboardController?.hide()
                                        focusManager.clearFocus()
                                        screenModel.onDoneAddress()
                                    },
                                    onClickScanQRCode,
                                    onClickPaste,
                                    networkType = selectedNetwork?.blockchainType?.networkType
                                        ?: NetworkType.EVM
                                )

                                LaunchedEffect(doneSelectNetwork) {
                                    delay(500)
                                    animate = true
                                    addressFocusRequester.requestFocus()
                                    delay(500)
                                    animate = false
                                }
                            }

//                            if (doneSelectNetwork && doneSelectAddress && selectedAddress?.isNotEmpty() == true) {
//                                SaveRecipientSwitch(
//                                    Modifier.padding(start = Spacing.TINY),
//                                    switchState.value
//                                ) {
//                                    switchState.value = it
//                                }
//                            }

                            if (switchState.value && doneSelectNetwork && doneSelectAddress && selectedAddress?.isNotEmpty() == true) {
                                NewRecipientInfo(
                                    selectedName,
                                    onNewRecipientNameChange = {
                                        screenModel.selectedName.value = it
                                    },
                                    onDoneSelectName = {
                                        screenModel.doneSelectName.value = it
                                    },
                                    focusManager,
                                    doneSelectName,
                                    nameFocusRequester
                                )
                                LaunchedEffect(doneSelectNetwork) {
                                    delay(500)
                                    nameFocusRequester.requestFocus()
                                }
                            }
                        }

                        if (showNetworkList) {
                            Spacer(modifier = Modifier.height(Spacing.XXLARGE))
                            Spacer(modifier = Modifier.height(Spacing.TINY))
                            LazyColumn(
                                modifier = Modifier.padding(horizontal = Dimensions.Padding.default),
                                verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
                            ) {
                                NetworkList(
                                    networks = screenModel.networks.value,
                                    onNetworkSelected = { network ->
                                        screenModel.showNetworkList.value = false
                                        screenModel.selectedNetwork.value = network
                                        screenModel.doneSelectNetwork.value = true
                                        networkInput.value = network.name
//                            keyboardController?.hide()
                                    },
                                    filter = networkInput.value,
//                            padding = Spacing.SMALL
                                )
                            }
                        }
                    }
                }
                if (doneSelectNetwork) {
                    MaxWidthRow(Modifier.padding(Dimensions.Padding.default)) {
                        MangalaGradientButton(
                            label = MR.strings.all_continue.desc().localized(),
                            onClick = {
                                if (doneSelectAddress) {
                                    onClickContinue()
                                } else {
                                    screenModel.onDoneAddress()
                                    focusManager.clearFocus()
                                }
                            },
                            enabled = selectedAddress?.isNotEmpty() == true
                                    && ((switchState.value && selectedName?.isNotEmpty() == true) || !switchState.value)
                                    && uiState.recipientValidationStatus == RecipientValidationStatus.Valid,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun NetworkSearchTextField(
        networkInput: MutableState<String>,
        screenModel: Step2SelectNetworkScreenModel,
        doneSelectNetwork: Boolean,
        isNetworkFocus: MutableState<Boolean>,
        networkFocusRequester: FocusRequester
    ) {
        BasicTextFieldWithHintAndTrailingIcons(
            networkInput.value,
            onValueChange = {
                networkInput.value = it
                screenModel.filterNetworks(it)
            },
            placeholder = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextNormal(
                        text = MR.strings.hint_send_token_network.desc().localized(),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            MangalaWalletPack.Search,
                            contentDescription = null,
                            tint = MaterialTheme.mangalaColors.iconPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            textColor = MaterialTheme.mangalaColors.textLink,
            fontWeight = FontWeight.SemiBold,
            fontSize = FontType.REGULAR,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    //                                    keyboardController?.hide()
                }
            ),
            textFieldModifier = Modifier
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        if (doneSelectNetwork) {
                            screenModel.doneSelectNetwork.value = false
                            screenModel.showNetworkList.value = true
                        }
                    }
                    isNetworkFocus.value = focusState.isFocused
                }
                .wrapContentWidth()
                .focusRequester(networkFocusRequester),
            trailingIcon = {
                if (networkInput.value.isNotEmpty()) {
                    if (isNetworkFocus.value) {
                        IconButton(
                            onClick = {
                                networkInput.value = ""
                                screenModel.selectedNetwork.value = null
                                screenModel.filterNetworks("")
                            },
                            modifier = Modifier.size(Dimensions.IconButtonSize)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null,
                                tint = MaterialTheme.mangalaColors.iconPrimary,
                                modifier = Modifier.size(Dimensions.IconSize)
                            )
                        }
                    }
                }
            }
        )
    }
}

