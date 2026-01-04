package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.jvm.Transient

class ChangeNetworkForPaymentScreen(
    private val accountName: String,
    private val accountNameType: AccountNameType,
    private val eosOwnerPrivateKey: String? = null,
    private val eosActivePrivateKey: String? = null
) : BaseScreen<ChangeNetworkForPaymentScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.PAY_WITH_CRYPTO_CHANGE_NETWORK_FOR_PAYMENT
    override val screenClassName: String = ChangeNetworkForPaymentScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun createScreenModel(): ChangeNetworkForPaymentScreenModel =
        getScreenModel<ChangeNetworkForPaymentScreenModel>()

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: ChangeNetworkForPaymentScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            MaxSizeColumn(Modifier.background(Colors.appleBg).windowInsetsPadding(WindowInsets.safeDrawing)) {
                val bottomSheetNavigator = LocalBottomSheetNavigator.current
                when (val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
                    is ChangeNetworkForPaymentUiState.Loading -> {
                        TextNormal(
                            text = MR.strings.all_loading.desc().localized(),
                            color = Colors.darkDarkGray
                        )
                    }

                is ChangeNetworkForPaymentUiState.Success -> {
                    ChoseNetworkAccountPaymentUI(
                        screenModel = screenModel,
                        supportedNetworks = uiState.supportedNetworks,
                        navigator = bottomSheetNavigator,
                        localNavigator = navigator,
                        wallets = uiState.wallets,
                        accounts = uiState.accounts,
                        accountBlockchainType = uiState.accountBlockchainType
                    )
                }

                    is ChangeNetworkForPaymentUiState.Error -> {
                        navigator.push(
                            ScreenRegistry.get(
                                SharedScreen.CryptoPaymentErrorScreen(
                                    error = uiState.message.resolve()
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ChoseNetworkAccountPaymentUI(
        screenModel: ChangeNetworkForPaymentScreenModel,
        supportedNetworks: List<BlockchainType>,
        navigator: BottomSheetNavigator,
        localNavigator: Navigator,
        wallets: List<WalletModel>,
        accounts: List<AccountBlockchainModel>,
        accountBlockchainType: BlockchainType
    ) {
        var selectedNetwork by remember { mutableStateOf<BlockchainType?>(null) }
        var selectedWallet by remember { mutableStateOf<WalletModel?>(null) }
        var selectedAccount by remember { mutableStateOf<AccountBlockchainModel?>(null) }
        var isAccountHasAmountOfNativeToken by remember { mutableStateOf<Boolean?>(null) }
        var nativeToken by remember { mutableStateOf<String?>(null) }

        val isValidData = selectedNetwork != null && selectedWallet != null && selectedAccount != null && isAccountHasAmountOfNativeToken != null && isAccountHasAmountOfNativeToken!!

        LaunchedEffect(true) {
            launch {
                val flow = screenModel.checkAccountBalanceChannel.receiveAsFlow()
                flow.collect { isNativeTokenAmountGreaterThanZero ->
                    isAccountHasAmountOfNativeToken = isNativeTokenAmountGreaterThanZero
                }
            }
            launch {
                val flow = screenModel.getNativeTokenChannel.receiveAsFlow()
                flow.collect { token ->
                    nativeToken = token
                }
            }
        }

        LaunchedEffect(selectedNetwork, selectedAccount) {
            selectedNetwork?.let { network ->
                selectedAccount?.let { account ->
                    screenModel.checkAccountBalance(network, account)
                }
            }
        }


        MaxWidthColumn(
            Modifier.fillMaxSize()
                .background(Colors.appleBg), verticalArrangement = Arrangement.SpaceBetween
        ) {

            MaxWidthColumn {
                MangalaWalletTopBar(
                    text = MR.strings.title_create_eos_account_paid_with_crypto.desc().localized(),
                    onBackClicked = { localNavigator.pop() }
                )

                MaxWidthColumn(
                    modifier = Modifier.padding(Dimensions.Padding.default),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    TextSubTitle(
                        text = MR.strings.title_paying_with_crypto_content.desc().localized(),
                        fontWeight = FontWeight.Medium,
                        color = Colors.darkDarkGray
                    )
                    VerticalSpacer(Spacing.TINY)
                    Text(
                        text = buildAnnotatedString {
                            append(
                                "${
                                    MR.strings.label_paying_with_crypto_description_first.desc()
                                        .localized()
                                } "
                            )
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(accountName)
                            }
                            append(
                                MR.strings.label_paying_with_crypto_description_second.desc()
                                    .localized()
                            )
                        },
                        color = Colors.caption,
                        fontSize = FontType.SMALL,
                        maxLines = Int.MAX_VALUE,
                        overflow = TextOverflow.Clip,
                        fontStyle = FontStyle.Normal
                    )
                    VerticalSpacer(Spacing.BASE)
                    NetworkSelection(
                        networks = supportedNetworks,
                        label = MR.strings.label_paying_with_crypto_network.desc().localized(),
                        selectedNetwork = selectedNetwork,
                        getDisplayName = { it.name },
                        onNetworkSelected = { selectedNetwork = it },
                        navigator = navigator
                    )
                    VerticalSpacer(Spacing.SMALL)
                    WalletSelection(
                        wallets = wallets,
                        label = MR.strings.label_paying_with_crypto_wallet.desc().localized(),
                        selectedWallet = selectedWallet,
                        getDisplayName = { it.name },
                        onWalletSelected = { selectedWallet = it },
                        navigator = navigator
                    )
                    VerticalSpacer(Spacing.SMALL)
                    AccountSelection(
                        accounts = accounts,
                        label = MR.strings.label_paying_with_crypto_account.desc().localized(),
                        selectedAccount = selectedAccount,
                        getDisplayName = { it.account.name },
                        onAccountSelected = { selectedAccount = it },
                        navigator = navigator
                    )
                    isAccountHasAmountOfNativeToken?.let {
                        if (!it) {
                            VerticalSpacer(Spacing.STINY)
                            val hasAmountOfNativeTokenError = StringDesc.ResourceFormatted(
                                MR.strings.error_pay_with_crypto_account_do_not_have_enough_native_token,
                                nativeToken ?: "native token"
                            ).localized()
                            TextNormal(
                                text = hasAmountOfNativeTokenError,
                                fontWeight = FontWeight.Normal,
                                color = Colors.red,
                                fontSize = FontType.TINY
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    ButtonNormal(
                        text = MR.strings.all_continue.desc().localized(),
                        fontSize = FontType.REGULAR,
                        textColor = Colors.white,
                        onClick = {
                            selectedNetwork?.let { network ->
                                selectedWallet?.let { wallet ->
                                    selectedAccount?.let { account ->
                                        screenModel.chooseNetworkAndWalletToPay(network, wallet)
                                        localNavigator.push(
                                            ScreenRegistry.get(
                                                SharedScreen.PayWithCryptoScreen(
                                                    accountName = accountName,
                                                    paidAccountId = account.account.id,
                                                    accountBlockchainTypeUid = accountBlockchainType.uid,
                                                    accountNameType = accountNameType,
                                                    eosOwnerPrivateKey = eosOwnerPrivateKey,
                                                    eosActivePrivateKey = eosActivePrivateKey
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        buttonModifier = Modifier.fillMaxWidth(),
                        buttonMinSizeDefault = 44.dp,
                        backgroundColor = Colors.darkDarkGray,
                        disabledBackgroundColor = Colors.grayWhite,
                        enabled = isValidData,
                        shape = RoundedCornerShape(CornerRadius.Tiny)
                    )
                    VerticalSpacer(Spacing.SMALL)
                }
            }
        }
    }

    @Composable
    fun NetworkSelection(
        networks: List<BlockchainType>,
        label: String,
        selectedNetwork: BlockchainType?,
        getDisplayName: (BlockchainType) -> String,
        onNetworkSelected: (BlockchainType?) -> Unit,
        navigator: BottomSheetNavigator
    ) {
        Text(text = label, style = MaterialTheme.typography.body1, color = Color.Black)
        VerticalSpacer(Spacing.STINY)

        OptionSelectingGeneric(
            selectedOption = selectedNetwork,
            getDisplayName = getDisplayName,
            label = label,
            onSelected = {
                navigator.show(
                    ScreenRegistry.get(
                        SharedScreen.SelectNetworkBottomSheetScreen(
                            onContinue = {
                                onNetworkSelected(it)
                                navigator.hide()
                            },
                            networks = networks,
                            selectedNetwork = selectedNetwork,
                            onDismiss = { navigator.hide() }
                        )
                    )
                )
            }
        )
    }

    @Composable
    fun WalletSelection(
        wallets: List<WalletModel>,
        label: String,
        selectedWallet: WalletModel?,
        getDisplayName: (WalletModel) -> String,
        onWalletSelected: (WalletModel?) -> Unit,
        navigator: BottomSheetNavigator,
    ) {
        Text(text = label, style = MaterialTheme.typography.body1, color = Color.Black)
        VerticalSpacer(Spacing.STINY)

        OptionSelectingGeneric(
            selectedOption = selectedWallet,
            getDisplayName = getDisplayName,
            label = label,
            onSelected = {
                navigator.show(
                    ScreenRegistry.get(
                        SharedScreen.SelectWalletBottomSheetScreen(
                            onContinue = {
                                onWalletSelected(it)
                                navigator.hide()
                            },
                            networks = wallets,
                            selectedNetwork = selectedWallet,
                            onDismiss = { navigator.hide() }
                        )
                    )
                )
            }
        )
    }

    @Composable
    fun AccountSelection(
        accounts: List<AccountBlockchainModel>,
        label: String,
        selectedAccount: AccountBlockchainModel?,
        getDisplayName: (AccountBlockchainModel) -> String,
        onAccountSelected: (AccountBlockchainModel?) -> Unit,
        navigator: BottomSheetNavigator
    ) {
        Text(text = label, style = MaterialTheme.typography.body1, color = Color.Black)
        VerticalSpacer(Spacing.STINY)

        OptionSelectingGeneric(
            selectedOption = selectedAccount,
            getDisplayName = getDisplayName,
            label = label,
            onSelected = {
                navigator.show(
                    ScreenRegistry.get(
                        SharedScreen.SelectAccountBottomSheetScreen(
                            onContinue = {
                                onAccountSelected(it)
                                navigator.hide()
                            },
                            accounts = accounts,
                            selectedAccount = selectedAccount,
                            onDismiss = { navigator.hide() }
                        )
                    )
                )
            }
        )
    }

    @Composable
    fun <T> OptionSelectingGeneric(
        selectedOption: T?,
        getDisplayName: (T) -> String,
        label: String,
        onSelected: () -> Unit,
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        Box {
            TextField(
                readOnly = true,
                value = selectedOption?.let { getDisplayName(it) } ?: "Select $label",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.Small))
                    .border(width = 1.dp, color = Colors.grayPalate10)
                    .background(Colors.white, shape = RoundedCornerShape(CornerRadius.Small)),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.Black)
                }
            )
            Box(
                modifier = Modifier.matchParentSize()
                    .background(Color.Transparent, shape = RoundedCornerShape(CornerRadius.Small))
                    .clickable(interactionSource = interactionSource, indication = null, onClick = onSelected)
            )
        }
    }
}