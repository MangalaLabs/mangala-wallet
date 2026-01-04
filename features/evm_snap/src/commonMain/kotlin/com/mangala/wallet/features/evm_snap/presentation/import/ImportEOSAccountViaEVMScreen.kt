package com.mangala.wallet.features.evm_snap.presentation.import

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.evm_snap.presentation.EosAccountLinkedEvmWalletChannelData
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.MangalaWrappedTextButton
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.receiveAsFlow

class ImportEOSAccountViaEVMScreen() : BaseScreen<ImportEOSAccountViaEVMScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_IMPORT_ACCOUNT_VIA_EVM
    override val screenClassName: String = ImportEOSAccountViaEVMScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ImportEOSAccountViaEVMScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: ImportEOSAccountViaEVMScreenModel) {

        LaunchedEffect(Unit) {
            screenModel.fetchEvmWallets()
        }

        val localNavigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        val homeScreen = rememberScreen(SharedScreen.HomeScreen())

        var eosAccountLinkedEvmWalletChannelData by remember { mutableStateOf<EosAccountLinkedEvmWalletChannelData?>(null) }

        LaunchedEffect(true) {
            screenModel.eosAccountLinkedEvmWalletChannel.receiveAsFlow().collect { data ->
                eosAccountLinkedEvmWalletChannelData = data
            }
        }

        MaxWidthColumn(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight()
                .background(Colors.appleBg).windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(
                    start = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.default
                )
        ) {
            MangalaWalletTopBar(
                text = MR.strings.title_import_evm_snap_account.desc().localized(),
                onBackClicked = { localNavigator.replaceAll(homeScreen) },
                color = Colors.darkDarkGray,
                fontSize = FontType.REGULAR,
                fontWeight = FontWeight.SemiBold
            )
            when(uiState) {
                is ImportEOSAccountViaEvmUIState.Loading -> {
                    TextNormal(
                        text = MR.strings.all_loading.desc().localized(),
                    )
                }

                is ImportEOSAccountViaEvmUIState.Success -> {
                    localNavigator.push(
                        ScreenRegistry.get(
                            SharedScreen.ChooseImportedEosAccountScreen(
                                eosOwnerPrivateKey = uiState.eosOwnerPrivateKey,
                                eosActivePrivateKey = uiState.eosActivePrivateKey
                            )
                        )
                    )
                }

                is ImportEOSAccountViaEvmUIState.Initial -> {
                    ImportEOSAccountViaEVM(
                        wallets = uiState.wallets,
                        eosAccountLinkedEvmWalletChannelData = eosAccountLinkedEvmWalletChannelData,
                        screenModel = screenModel,
                        onSelectWallet = {
                            eosAccountLinkedEvmWalletChannelData = null
                        },
                        onImportWithNewEvm = {
                            screenModel.clearState()
                            localNavigator.push(
                                ScreenRegistry.get(
                                    SharedScreen.ImportWalletGuideScreen(
                                        nextScreen = SharedScreen.ScreenType.IMPORT_EOS_VIA_EVM
                                    )
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun ImportEOSAccountViaEVM(
        wallets: List<WalletModel>,
        eosAccountLinkedEvmWalletChannelData: EosAccountLinkedEvmWalletChannelData?,
        screenModel: ImportEOSAccountViaEVMScreenModel,
        onSelectWallet: () -> Unit,
        onImportWithNewEvm: () -> Unit
    ) {
        var selectedWallet by remember { mutableStateOf<WalletModel?>(null) }
        val isEnabledContinueButton = selectedWallet != null
                && eosAccountLinkedEvmWalletChannelData != null
                && eosAccountLinkedEvmWalletChannelData.isLinkedEosAccount
        MaxWidthColumn {
            TextSubTitle(
                text = MR.strings.title_import_from_evm_wallet.desc().localized(),
                fontWeight = FontWeight.Medium,
                color = Colors.darkGray,
                modifier = Modifier.padding(bottom = Dimensions.Padding.half)
            )
            LazyColumn (
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    TextNormal(
                        text = MR.strings.label_import_evm_snap_account_description.desc().localized(),
                        fontWeight = FontWeight.Normal,
                        color = if (wallets.isEmpty()) Colors.brightRed else Colors.darkDarkGray,
                        fontSize = FontType.SMALL
                    )
                    if (wallets.isEmpty()) {
                        VerticalSpacer(Spacing.TINY)
                        TextNormal(
                            text = MR.strings.label_not_have_any_evm_wallet.desc().localized(),
                            fontWeight = FontWeight.Normal,
                            color = Colors.brightRed,
                            fontSize = FontType.SMALL
                        )
                    } else {
                        VerticalSpacer(Spacing.SMALL)
                    }
                }
                items(wallets) { wallet ->
                    WalletCard(
                        title = wallet.name,
                        wallet = wallet,
                        selectedWallet = selectedWallet,
                        eosAccountLinkedEvmWalletChannelData = eosAccountLinkedEvmWalletChannelData,
                        onSelected = {
                            onSelectWallet()
                            selectedWallet = wallet
                            screenModel.isEvmLinkedWithEosAccount(wallet)
                        }
                    )
                }
            }
            VerticalSpacer(Spacing.SMALL)
            ButtonNormal(
                text = MR.strings.all_continue.desc().localized(),
                fontSize = FontType.REGULAR,
                textColor = if (isEnabledContinueButton) Colors.white else Colors.mistGray,
                onClick = {
                    selectedWallet?.let {
                        screenModel.getEosPrivateKeyFromEvmWallet(it)
                    }
                },
                buttonModifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.Padding.default),
                buttonMinSizeDefault = MangalaButtonSize.XMedium.height,
                backgroundColor = Colors.darkDarkGray,
                disabledBackgroundColor = Colors.lightLightGrayWhite,
                enabled = isEnabledContinueButton,
                shape = RoundedCornerShape(CornerRadius.Tiny)
            )
            MangalaWrappedTextButton(
                text = MR.strings.label_import_with_new_evm_wallet.desc().localized(),
                color = Colors.darkDarkGray,
                textAlign = TextAlign.Center,
                onClick = {
                    onImportWithNewEvm()
                },
                fontSize = FontType.SMALL,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    fun WalletCard(
        title: String,
        wallet: WalletModel,
        selectedWallet: WalletModel?,
        eosAccountLinkedEvmWalletChannelData: EosAccountLinkedEvmWalletChannelData?,
        onSelected: () -> Unit,
    ) {
        val isInValidWallet = eosAccountLinkedEvmWalletChannelData?.let {
            it.walletModelId == wallet.id && !it.isLinkedEosAccount
        } ?: false
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.Padding.small)
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
                VerticalSpacer(Spacing.TINY)
                TextNormal(
                    text = MR.strings.error_evm_wallet_not_link_eos_account.desc().localized(),
                    fontWeight = FontWeight.Normal,
                    color = Colors.brightRed,
                    fontSize = FontType.TINY
                )
            }
        }
    }
}