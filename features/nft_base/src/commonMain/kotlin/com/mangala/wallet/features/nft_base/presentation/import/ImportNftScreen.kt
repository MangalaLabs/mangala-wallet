package com.mangala.wallet.features.nft_base.presentation.import

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics

class ImportNftScreen: BaseScreen<ImportNftScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_IMPORT_NFT
    override val screenClassName: String = ImportNftScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ImportNftScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: ImportNftScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

        ImportNftScreen(
            uiModel,
            onChangeContractAddress = {
                screenModel.onChangeContractAddress(it)
            },
            onChangeTokenId = {
                screenModel.onChangeTokenId(it)
            },
            onClickImport = {
                screenModel.onClickImport()
            }
        )
    }

    @Composable
    private fun ImportNftScreen(
        uiModel: ImportNftScreenUiModel,
        onChangeContractAddress: (String) -> Unit,
        onChangeTokenId: (String) -> Unit,
        onClickImport: () -> Unit
    ) {
        MaxSizeColumn(Modifier.verticalScroll(rememberScrollState())) {
            TextField(
                value = uiModel.contractAddress,
                onValueChange = onChangeContractAddress,
                label = { Text("Contract address") }
            )
            TextField(
                value = uiModel.tokenId,
                onValueChange = onChangeTokenId,
                label = { Text("Token ID") }
            )
            Button(onClickImport) {
                Text("Import")
            }
        }
    }
}