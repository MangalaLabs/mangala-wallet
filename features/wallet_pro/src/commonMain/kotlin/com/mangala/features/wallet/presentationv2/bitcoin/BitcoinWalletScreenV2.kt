package com.mangala.features.wallet.presentationv2.bitcoin

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.features.wallet.presentationv2.core.base.BaseWalletScreenV2
import com.mangala.features.wallet.presentationv2.core.base.BaseWalletViewModel
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.utils.analytics.MangalaAnalytics

class BitcoinWalletViewModel : BaseWalletViewModel() {
    override val networkType = NetworkType.BITCOIN
    
    override fun onRefresh() {
        // TODO: Implement Bitcoin refresh logic
    }
    
    override fun onCopyAddress() {
        // TODO: Implement copy to clipboard
    }
    
    override fun onShareAddress() {
        // TODO: Implement share functionality
    }
}

class BitcoinWalletScreenV2 : BaseWalletScreenV2<BitcoinWalletViewModel>() {
    
    override val networkType = NetworkType.BITCOIN
    override val screenName = MangalaAnalytics.Screens.WALLET
    override val screenClassName = BitcoinWalletScreenV2::class.simpleName.orEmpty()
    
    @Composable
    override fun createScreenModel(): BitcoinWalletViewModel {
        return getScreenModel()
    }
    
    @Composable
    override fun ScreenContent(screenModel: BitcoinWalletViewModel) {
        OnboardingGradientBackground(
            circleBackgroundEnabled = true,
            afterBackgroundModifier = Modifier.navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(WalletThemeV2.Dimensions.spacingMedium)
                ) {
                    Text(
                        text = "🪙",
                        fontSize = WalletThemeV2.Typography.fontSizeBalance
                    )
                    
                    Text(
                        text = "Bitcoin Wallet",
                        fontSize = WalletThemeV2.Typography.fontSizeHeader,
                        fontWeight = FontWeight.Bold,
                        color = WalletThemeV2.Colors.primaryText
                    )
                    
                    Text(
                        text = "Coming Soon",
                        fontSize = WalletThemeV2.Typography.fontSizeMedium,
                        color = WalletThemeV2.Colors.secondaryText
                    )
                }
            }
        }
    }
    
    override fun onNavigateToSend() {
        // TODO: Implement navigation
    }
    
    override fun onNavigateToReceive() {
        // TODO: Implement navigation
    }
    
    override fun onNavigateToHistory() {
        // TODO: Implement navigation
    }

    override fun onHandleQrCodeResult(
        result: QrCodeData?,
        navigator: Navigator,
        globalNavigator: Navigator,
        screenModel: BitcoinWalletViewModel
    ) {
        TODO("Not yet implemented")
    }

    override val isBottomBarVisible = true
}