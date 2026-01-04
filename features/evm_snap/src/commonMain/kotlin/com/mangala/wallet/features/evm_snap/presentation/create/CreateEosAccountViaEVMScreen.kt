package com.mangala.wallet.features.evm_snap.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.evm_snap.presentation.EosAccountLinkedEvmWalletChannelData
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.receiveAsFlow

class CreateEosAccountViaEVMScreen(
    private val accountName: String,
    private val accountNameSuffix: String?,
    private val accountNameType: AccountNameType
) : BaseScreen<CreateEosAccountViaEVMScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_VIA_EVM
    override val screenClassName: String = CreateEosAccountViaEVMScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): CreateEosAccountViaEVMScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: CreateEosAccountViaEVMScreenModel) {
        val localNavigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        val homeScreen = rememberScreen(SharedScreen.HomeScreen())

        var eosAccountLinkedEvmWalletChannelData by remember {
            mutableStateOf<EosAccountLinkedEvmWalletChannelData?>(
                null
            )
        }

        LaunchedEffect(true) {
            screenModel.eosAccountLinkedEvmWalletChannel.receiveAsFlow().collect { data ->
                eosAccountLinkedEvmWalletChannelData = data
            }
        }

        when (uiState) {
            is CreateEosAccountViaEvmUIState.Loading -> {
                TextNormal(
                    modifier = Modifier.fillMaxSize()
                        .padding(
                            vertical = Dimensions.Padding.default,
                            horizontal = Dimensions.Padding.small
                        ),
                    text = MR.strings.all_loading.desc().localized(),
                    color = Colors.darkDarkGray,
                    fontSize = FontType.SMALL
                )
            }

            is CreateEosAccountViaEvmUIState.Initial -> {
                CreateEosAccountViaEVMScreenContent(
                    wallets = uiState.wallets,
                    screenModel = screenModel,
                    eosAccountLinkedEvmWalletChannelData = eosAccountLinkedEvmWalletChannelData,
                    onBackClicked = { localNavigator.replaceAll(homeScreen) },
                    onSelected = { eosAccountLinkedEvmWalletChannelData = null }
                )
            }

            is CreateEosAccountViaEvmUIState.Success -> {
                localNavigator.push(
                    ScreenRegistry.get(
                        SharedScreen.Step3CreateAccountPaymentScreen(
                            initialAccountName = accountName,
                            initialAccountSuffix = accountNameSuffix,
                            initialAccountType = accountNameType,
                            eosOwnerPrivateKey = uiState.eosOwnerPrivateKey,
                            eosActivePrivateKey = uiState.eosActivePrivateKey,
                        )
                    )
                )
            }
        }
    }

    @Composable
    fun CreateEosAccountViaEVMScreenContent(
        wallets: List<WalletModel>,
        screenModel: CreateEosAccountViaEVMScreenModel,
        eosAccountLinkedEvmWalletChannelData: EosAccountLinkedEvmWalletChannelData?,
        onBackClicked: () -> Unit,
        onSelected: () -> Unit
    ) {
        var selectedWallet by remember { mutableStateOf<WalletModel?>(null) }
        val isEnabledContinueButton = selectedWallet != null
                && eosAccountLinkedEvmWalletChannelData != null
                && !eosAccountLinkedEvmWalletChannelData.isLinkedEosAccount
        MaxWidthColumn(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight()
                .background(Colors.appleBg).windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(
                    start = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                    top = Dimensions.Padding.small,
                )
        ) {
            MangalaWalletTopBar(
                text = MR.strings.title_create_from_evm.desc().localized(),
                onBackClicked = { onBackClicked() },
                color = Colors.darkDarkGray,
                fontSize = FontType.REGULAR,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = Dimensions.Padding.default)
            )
            TextNormal(
                text = MR.strings.title_create_eos_account_from_evm_wallet.desc().localized(),
                fontWeight = FontWeight.Medium,
                color = Colors.darkGray,
                modifier = Modifier.padding(bottom = Dimensions.Padding.half),
                fontSize = FontType.LARGE_24
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    val formattedDescription = StringDesc.ResourceFormatted(
                        MR.strings.label_create_eos_account_from_evm_wallet_description,
                        accountName
                    ).localized()
                    TextNormal(
                        text = buildAnnotatedString {
                            val placeholderStart = formattedDescription.indexOf(accountName)
                            val placeholderEnd = placeholderStart + accountName.length
                            append(formattedDescription)
                            addStyle(
                                style = SpanStyle(fontFamily = getSfProFamilyFont(FontWeight.SemiBold)),
                                start = placeholderStart,
                                end = placeholderEnd
                            )
                        },
                        fontWeight = FontWeight.Normal,
                        color = Colors.darkDarkGray,
                        fontSize = FontType.SMALL,
                        modifier = Modifier.padding(bottom = Dimensions.Padding.small)
                    )
                    if (wallets.isEmpty()) {
                        TextNormal(
                            text = MR.strings.label_not_have_any_evm_wallet.desc().localized(),
                            fontWeight = FontWeight.Normal,
                            color = Colors.red,
                            fontSize = FontType.SMALL
                        )
                    }
                }
                items(wallets) { wallet ->
                    WalletCard(
                        title = wallet.name,
                        wallet = wallet,
                        selectedWallet = selectedWallet,
                        eosAccountLinkedEvmWalletChannelData = eosAccountLinkedEvmWalletChannelData,
                        onSelected = {
                            onSelected()
                            selectedWallet = wallet
                            screenModel.isEvmLinkedWithEosAccount(wallet)
                        }
                    )
                }
            }
            VerticalSpacer(Spacing.SMALL)
            ButtonNormal(
                modifier = Modifier.fillMaxWidth(),
                text = MR.strings.all_continue.desc().localized(),
                fontSize = FontType.REGULAR,
                textColor = if (isEnabledContinueButton) Colors.white else Colors.mistGray,
                onClick = {
                    selectedWallet?.let {
                        screenModel.getEosPrivateKeyFromEvmWallet(it)
                    }
                },
                buttonModifier = Modifier.fillMaxWidth(),
                buttonMinSizeDefault = 44.dp,
                backgroundColor = Colors.darkDarkGray,
                disabledBackgroundColor = Colors.lightLightGrayWhite,
                enabled = isEnabledContinueButton,
                shape = RoundedCornerShape(CornerRadius.Tiny)
            )
        }
    }

    @Composable
    fun WalletCard(
        title: String,
        wallet: WalletModel,
        selectedWallet: WalletModel?,
        onSelected: () -> Unit,
        eosAccountLinkedEvmWalletChannelData: EosAccountLinkedEvmWalletChannelData?
    ) {
        val isInValidWallet = eosAccountLinkedEvmWalletChannelData?.let {
            it.walletModelId == wallet.id && it.isLinkedEosAccount
        } ?: false

        Column(
            modifier = Modifier.fillMaxWidth().padding(
                bottom = Dimensions.Padding.small
            )
        ) {
            MaxWidthRow(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .background(Colors.white, shape = RoundedCornerShape(CornerRadius.Small))
                    .border(
                        width = 1.dp,
                        color = if (isInValidWallet) Colors.brightRed else Colors.darkDarkGray,
                        shape = RoundedCornerShape(CornerRadius.Small)
                    )
                    .clickable { onSelected() }
                    .padding(
                        horizontal = Dimensions.Padding.default,
                        vertical = Dimensions.Padding.medium
                    )
            ) {
                TextDescription2(
                    text = title,
                    color = Colors.darkDarkGray,
                    fontWeight = FontWeight.Normal
                )
                if (!isInValidWallet) {
                    Spacer(modifier = Modifier.weight(1f))
                    selectedWallet?.let {
                        if (it == wallet) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Dropdown",
                                tint = Colors.darkDarkGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
            if (isInValidWallet) {
                TextNormal(
                    text = MR.strings.error_evm_wallet_link_eos_account.desc().localized(),
                    fontWeight = FontWeight.Normal,
                    color = Colors.red,
                    fontSize = FontType.TINY,
                    modifier = Modifier.padding(
                        top = Dimensions.Padding.half,
                    )
                )
            }
        }
    }
}