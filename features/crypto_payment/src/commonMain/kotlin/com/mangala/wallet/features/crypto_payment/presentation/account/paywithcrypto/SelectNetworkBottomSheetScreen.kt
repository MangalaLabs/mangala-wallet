package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlin.jvm.Transient

class SelectNetworkBottomSheetScreen(
    @Transient private val networks: List<BlockchainType>,
    @Transient private val onContinue: (BlockchainType?) -> Unit,
    @Transient private val selectedNetwork: BlockchainType?,
    @Transient private val onDismiss: () -> Unit
): Screen {
    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.PAY_WITH_CRYPTO_SELECT_NETWORK,
                SelectNetworkBottomSheetScreen::class.simpleName.orEmpty()
            )
        })

        ChangeNetworkOptionSelection(
            optionTitle = MR.strings.label_paying_with_crypto_network.desc().localized(),
            options = networks,
            selectedOption = selectedNetwork,
            getOptionName = { it.name },
            onContinue = onContinue,
            searchPlaceHolder = MR.strings.place_holder_search_network_description.desc().localized(),
            onDismiss = onDismiss
        )
    }
}