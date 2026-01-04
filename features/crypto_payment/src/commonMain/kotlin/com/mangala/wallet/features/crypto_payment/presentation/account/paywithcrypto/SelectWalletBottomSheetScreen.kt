package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlin.jvm.Transient

class SelectWalletBottomSheetScreen(
    @Transient private val wallets: List<WalletModel>,
    @Transient private val onContinue: (WalletModel?) -> Unit,
    @Transient private val selectedWallet: WalletModel?,
    @Transient private val onDismiss: () -> Unit
) : Screen {

    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.PAY_WITH_CRYPTO_SELECT_WALLET,
                SelectWalletBottomSheetScreen::class.simpleName.orEmpty()
            )
        })

        ChangeNetworkOptionSelection(
            optionTitle = MR.strings.label_paying_with_crypto_wallet.desc().localized(),
            options = wallets,
            selectedOption = selectedWallet,
            getOptionName = { it.name },
            onContinue = onContinue,
            searchPlaceHolder = MR.strings.place_holder_search_wallet_description.desc().localized(),
            onDismiss = onDismiss
        )
    }
}