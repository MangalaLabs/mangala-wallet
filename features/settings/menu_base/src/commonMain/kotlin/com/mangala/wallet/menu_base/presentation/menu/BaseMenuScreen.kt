package com.mangala.wallet.menu_base.presentation.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.AboutUs
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ConnectWithUs
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HelpCenter
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Navigate
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Network
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Notification
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Security
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Setting
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Share
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Wallet
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

abstract class BaseMenuScreen : BaseScreen<BaseMenuScreenModel>() {


    @Composable
    override fun createScreenModel(): BaseMenuScreenModel = getScreenModel()
}

@Composable
fun BaseMenuScreenContent(
    screenModel: BaseMenuScreenModel,
    onBackPressed: () -> Unit,
    secondGroupAdditionalContent: @Composable (uiModel: MenuScreenUiModel) -> Unit,
) {
    val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value
    val navigator = LocalNavigator.currentOrThrow

    val networkScreen = rememberScreen(SharedScreen.NetworkScreen)
    val walletScreen = rememberScreen(SharedScreen.WalletScreen)
    val antelopeAccountScreen = rememberScreen(SharedScreen.ManageAntelopeAccountScreen)
    val notificationsAndAlertsScreen = rememberScreen(SharedScreen.NotificationsScreen)
    val securityScreen = rememberScreen(SharedScreen.SecurityScreen)
    val helpCenterScreen = rememberScreen(SharedScreen.HelpCenterScreen)
    val preferencesScreen = rememberScreen(SharedScreen.PreferencesScreen)
    val connectWithUsScreen = rememberScreen(SharedScreen.ConnectWithUsScreen)
    val shareAppScreen = rememberScreen(SharedScreen.ShareAppScreen)
    val aboutUsScreen = rememberScreen(SharedScreen.AboutUsScreen)

    val walletName = screenModel.walletName.collectAsStateMultiplatform().value

    MaxSizeColumn(
        modifier = Modifier.background(MaterialTheme.mangalaColors.bg)
    ) {
        MangalaWalletTopBarCenteredTitle(
            title = MR.strings.all_menu.desc().localized(),
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
            navigationIcon = {
                // Empty box to maintain consistent height
                Spacer(modifier = Modifier.size(48.dp))
            }
        )
        
        Column(
            modifier = Modifier
                .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.default)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (screenModel.uiModel.value.network?.blockchainType?.networkType == NetworkType.ANTELOPE) {
                NetworkAndBackupAntelopeAccountSection(
                    selectedNetworkName = screenModel.uiModel.value.network?.name.orEmpty(),
                    selectedNetworkImageResource = screenModel.uiModel.value.network?.localImage,
                    onClickNetwork = { navigator.push(networkScreen) },
                    onClickManageAccount = { navigator.push(antelopeAccountScreen) }
                )
            } else {
                NetworkAndWalletSection(
                    selectedNetworkName = screenModel.uiModel.value.network?.name.orEmpty(),
                    selectedNetworkImageResource = screenModel.uiModel.value.network?.localImage,
                    onClickNetwork = { navigator.push(networkScreen) },
                    selectedWalletName = walletName,
                    onClickWallet = { navigator.push(walletScreen) }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.BASE))

            Column(
                modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small)),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (screenModel.isDevelopmentEnvironment()) {
                    MenuRow(
                        title = MR.strings.all_preferences.desc().localized(),
                        onClickNavigate = { navigator.push(preferencesScreen) },
                        iconRepresent = MangalaWalletPack.Setting
                    )

                    MenuRow(
                        title = MR.strings.all_notifications.desc().localized(),
                        onClickNavigate = { navigator.push(notificationsAndAlertsScreen) },
                        iconRepresent = MangalaWalletPack.Notification
                    )
                }

                MenuRow(
                    title = MR.strings.all_security.desc().localized(),
                    onClickNavigate = { navigator.push(securityScreen) },
                    iconRepresent = MangalaWalletPack.Security
                )

                secondGroupAdditionalContent(uiModel)
            }

            if (screenModel.isDevelopmentEnvironment()) {
                Spacer(modifier = Modifier.height(24.dp))

                AppAboutSection(
                    onClickHelpCenter = { navigator.push(helpCenterScreen) },
                    onClickAboutUs = { navigator.push(aboutUsScreen) },
                    onClickShareApp = { navigator.push(shareAppScreen) }
                )

                Spacer(modifier = Modifier.height(Spacing.BASE))

                ConnectWithUsSection(onClick = { navigator.push(connectWithUsScreen) })
            }

            DebugMenuAndVersionName(
                navigator,
                screenModel.isDevelopmentEnvironment(),
                screenModel.appVersionUtils.getAppVersion()
            )
        }
    }
}

@Composable
private fun ConnectWithUsSection(onClick: () -> Unit) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small))
    ) {
        MenuRow(
            title = MR.strings.all_connect_with_us.desc().localized(),
            onClickNavigate = onClick,
            iconRepresent = MangalaWalletPack.ConnectWithUs
        )
    }
}

@Composable
private fun AppAboutSection(
    onClickHelpCenter: () -> Unit,
    onClickAboutUs: () -> Unit,
    onClickShareApp: () -> Unit
) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small)),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        MenuRow(
            title = MR.strings.all_help_center.desc().localized(),
            onClickNavigate = onClickHelpCenter,
            iconRepresent = MangalaWalletPack.HelpCenter
        )

        MenuRow(
            title = MR.strings.all_about_us.desc().localized(),
            onClickNavigate = onClickAboutUs,
            iconRepresent = MangalaWalletPack.AboutUs
        )

        MenuRow(
            title = MR.strings.all_share_app.desc().localized(),
            onClickNavigate = onClickShareApp,
            iconRepresent = MangalaWalletPack.Share
        )
    }
}

@Composable
private fun ColumnScope.DebugMenuAndVersionName(
    navigator: Navigator,
    isDevelopmentEnvironment: Boolean,
    appVersion: String
) {
    if (isDevelopmentEnvironment) {
        // Debug menu
        Spacer(modifier = Modifier.height(Spacing.BASE))
        Column(
            modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small))
        ) {
            MenuRow(
                title = "Icons in App",
                onClickNavigate = {
                    val iconsInAppScreen = ScreenRegistry.get(SharedScreen.IconsInAppScreen)

                    navigator.push(iconsInAppScreen)
                },
                iconRepresent = MangalaWalletPack.ConnectWithUs
            )
            MenuRow(
                title = "Antelope Multisig & Permission",
                onClickNavigate = {
                    val antelopeMultisigScreen =
                        ScreenRegistry.get(SharedScreen.AntelopeMulitsigScreen)

                    navigator.push(antelopeMultisigScreen)
                },
                iconRepresent = MangalaWalletPack.Setting
            )
            MenuRow(
                title = "Dev Menu",
                onClickNavigate = {
                    val devMenuScreen =
                        ScreenRegistry.get(SharedScreen.DevMenuScreen)

                    navigator.push(devMenuScreen)
                },
                iconRepresent = MangalaWalletPack.Setting
            )
        }
    }
    Spacer(modifier = Modifier.height(44.dp))
    TextDescription2(
        text = MR.strings.all_version.format(
            appVersion
        ).localized(), modifier = Modifier.align(
            Alignment.CenterHorizontally
        ), color = MaterialTheme.mangalaColors.textSecondary
    )
    Spacer(modifier = Modifier.height(74.dp))
}

@Composable
fun NetworkAndWalletSection(
    selectedNetworkName: String,
    selectedNetworkImageResource: ImageResource?,
    onClickNetwork: () -> Unit,
    selectedWalletName: String,
    onClickWallet: () -> Unit
) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small)),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        MenuRow(
            title = MR.strings.all_network.desc().localized(),
            showSelectedName = selectedNetworkName,
            onClickNavigate = onClickNetwork,
            iconRepresent = MangalaWalletPack.Network,
            localImage = selectedNetworkImageResource
        )

        MenuRow(
            title = MR.strings.all_wallets.desc().localized(),
            showSelectedName = selectedWalletName,
            onClickNavigate = onClickWallet,
            iconRepresent = MangalaWalletPack.Wallet
        )
    }
}

@Composable
fun NetworkAndBackupAntelopeAccountSection(
    selectedNetworkName: String,
    selectedNetworkImageResource: ImageResource?,
    onClickNetwork: () -> Unit,
    onClickManageAccount: () -> Unit
) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small)),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        MenuRow(
            title = MR.strings.all_network.desc().localized(),
            showSelectedName = selectedNetworkName,
            onClickNavigate = onClickNetwork,
            iconRepresent = MangalaWalletPack.Network,
            localImage = selectedNetworkImageResource
        )

        MenuRow(
            title = MR.strings.all_manage_accounts.desc().localized(),
            onClickNavigate = onClickManageAccount,
            iconRepresent = MangalaWalletPack.Wallet
        )
    }
}

@Composable
fun MenuRow(
    title: String,
    showSelectedName: String = "",
    onClickNavigate: () -> Unit,
    iconSelectedRepresent: ImageVector? = null,
    iconRepresent: ImageVector,
    localImage: ImageResource? = null
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.mangalaColors.bgInnerCard)
                .clickable(onClick = onClickNavigate).padding(
                    start = Dimensions.Padding.default,
                    top = Dimensions.Padding.small,
                    bottom = Dimensions.Padding.small,
                    end = Dimensions.Padding.default
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = iconRepresent,
                    contentDescription = null,
                    modifier = Modifier
                        .width(Dimensions.IconButtonSize)
                        .height(Dimensions.IconButtonSize),
                    tint = MaterialTheme.mangalaColors.iconPrimary
                )
                Spacer(modifier = Modifier.width(Dimensions.Padding.half))
                TextNormal(text = title, color = MaterialTheme.mangalaColors.textPrimary)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (showSelectedName != "") {
                    TextDescription2(
                        text = showSelectedName,
                        color = MaterialTheme.mangalaColors.textSecondary
                    )
                    Spacer(modifier = Modifier.width(Dimensions.Padding.half))
                }
                if (iconSelectedRepresent != null) {
                    Icon(
                        imageVector = iconSelectedRepresent,
                        contentDescription = null,
                        modifier = Modifier.width(20.dp).height(20.dp),
                        tint = MaterialTheme.mangalaColors.iconPrimary
                    )
                    Spacer(modifier = Modifier.width(Dimensions.Padding.small))
                }
                if (localImage != null) {
                    LocalImage(
                        modifier = Modifier.size(Dimensions.IconButtonSize),
                        imageResource = localImage,
                    )
                    Spacer(modifier = Modifier.width(Dimensions.Padding.small))
                } else {
                    Spacer(modifier = Modifier.width(Dimensions.Padding.quarter))
                }

                Icon(
                    imageVector = MangalaWalletPack.Navigate,
                    contentDescription = "Navigate",
                    modifier = Modifier.width(20.dp).height(20.dp),
                    tint = MaterialTheme.mangalaColors.iconPrimary
                )
            }
        }
    }
}