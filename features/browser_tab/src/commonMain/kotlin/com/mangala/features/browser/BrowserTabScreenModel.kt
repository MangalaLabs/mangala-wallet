package com.mangala.features.browser

import cafe.adriel.voyager.core.model.coroutineScope
import com.mangala.wallet.domain.dapp.usecase.GetDAppsByCategoriesUseCase
import com.mangala.wallet.domain.dapp.usecase.GetDappsJsonUseCase
import com.mangala.wallet.domain.dapp.usecase.GetListDAppUseCase
import com.mangala.wallet.domain.dapp.usecase.GetListOfCategoriesUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.category_dapp.remote.CategoryDApp
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrowserTabScreenModel(
    private val walletRepository: WalletRepository,
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase,
    private val getListOfCategoriesUseCase: GetListOfCategoriesUseCase,
    private val getDAppsByCategoriesUseCase: GetDAppsByCategoriesUseCase,
    private val getDappsJson: GetDappsJsonUseCase,
    private val getListDAppUseCase: GetListDAppUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
): BaseScreenModel() {

    private val _uiModel = MutableStateFlow(BrowserAccountsUiModel())
    val uiModel: StateFlow<BrowserAccountsUiModel> get() = _uiModel

    private val blockchainType: BlockchainType = BlockchainType.BinanceSmartChain

    private val _categoriesDApps = MutableStateFlow<List<CategoryDApp>>(emptyList())
    val categories: StateFlow<List<CategoryDApp>> get() = _categoriesDApps


    override fun doOnComposableStarted() {
        lifecycleScope.launch { getAccounts() }
        coroutineScope.launch { fetchInitialData() }
    }

    private suspend fun fetchInitialData() {
//        val allCategories = getListOfCategoriesUseCase.invoke().map { it.uuid }
        val firstCategoryDApps = getDappsJson.invoke()
        _categoriesDApps.value = firstCategoryDApps
    }

    private suspend fun getAccounts() {
        val isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()
        val networkSelected = getSelectedNetworkUseCase.invoke()
        val blockchainType = BlockchainType.fromUid(networkSelected.blockChainUid)

        val blockchainData = BlockchainNetworkData.getAllBlockchainNetworkSupported(isDevelopmentEnvironment).find {
            it.blockChainUid == blockchainType.uid
        }
        val result = getSelectedWalletAccountsUseCase()
        result?.let {
            _uiModel.update {
                it.copy(
                    accounts = result,
                    chainId = blockchainData?.chainId ?: 1L,
                    rpcUrl = blockchainType.getRpcUrl().getOrNull(0) ?: ""
                )
            }
        }
    }

}