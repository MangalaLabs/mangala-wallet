package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlin.jvm.Transient

class SelectAccountBottomSheetScreen(
    @Transient private val accounts: List<AccountBlockchainModel>,
    @Transient private val onContinue: (AccountBlockchainModel?) -> Unit,
    @Transient private val selectedAccount: AccountBlockchainModel?,
    @Transient private val onDismiss: () -> Unit
): Screen {

    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.PAY_WITH_CRYPTO_SELECT_ACCOUNT,
                SelectAccountBottomSheetScreen::class.simpleName.orEmpty()
            )
        })

        ChangeNetworkOptionSelection(
            optionTitle = MR.strings.label_paying_with_crypto_account.desc().localized(),
            options = accounts,
            selectedOption = selectedAccount,
            getOptionName = { it.account.name },
            onContinue = onContinue,
            searchPlaceHolder = MR.strings.place_holder_search_account_description.desc().localized(),
            onDismiss = onDismiss
        )
    }
}