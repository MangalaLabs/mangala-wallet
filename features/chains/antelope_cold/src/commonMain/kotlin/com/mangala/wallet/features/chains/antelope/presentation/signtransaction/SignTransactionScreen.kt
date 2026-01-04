package com.mangala.wallet.features.chains.antelope.presentation.signtransaction

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.parameter.parametersOf

class SignTransactionScreen(
    private val signTransactionRequest: SignTransactionRequest
): BaseScreen<SignTransactionScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_SIGN_TRANSACTION_SCREEN
    override val screenClassName: String = SignTransactionScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): SignTransactionScreenModel {
        return getScreenModel { parametersOf(signTransactionRequest) }
    }

    @Composable
    override fun ScreenContent(screenModel: SignTransactionScreenModel) {
        Column {
            Text("Sign transaction")
            Text("Transaction type ${signTransactionRequest.signTransactionType}")
            Text("Actions: ${signTransactionRequest.actions}")
//            Button()
        }
    }
}