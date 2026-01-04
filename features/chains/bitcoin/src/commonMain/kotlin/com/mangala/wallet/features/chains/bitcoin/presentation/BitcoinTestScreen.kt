package com.mangala.wallet.features.chains.bitcoin.presentation

import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen

class BitcoinTestScreen: BaseScreen<BitcoinTestScreenModel>() {

    @Composable
    override fun createScreenModel(): BitcoinTestScreenModel {
        return getScreenModel()
    }

    override val screenName: String = "BitcoinTestScreen"
    override val screenClassName: String = "BitcoinTestScreen"

    @Composable
    override fun ScreenContent(screenModel: BitcoinTestScreenModel) {
        MaxWidthColumn {
            Text("Hello Bitcoin Test Screen")
            Button(
                onClick = screenModel::restoreTestWallet
            ) {
                Text("Restore test wallet")
            }
            Button(
                onClick = screenModel::getUtxos
            ) {
                Text("Get UTXOs")
            }
            Button(
                onClick = screenModel::sendTransaction
            ) {
                Text("Send transaction")
            }
        }
    }
}