package com.mangala.wallet.features.swap_base.presentation.selecttoken

import app.cash.paging.PagingData
import app.cash.paging.filter
import app.cash.paging.map
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.account.usecases.GetAccountBalanceUseCase
import com.mangala.wallet.domain.coin.usecases.GetAllCoinUseCase
import com.mangala.wallet.domain.token.usecases.GetTokenByBlockchainUidUseCase
import com.mangala.wallet.features.swap_base.presentation.SwapTokenUiModel
import com.mangala.wallet.model.token.TokenEntity
import com.mangala.wallet.model.token.domain.formattedBalanceForHuman
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SelectTokenScreenModel(
    private val accountAddress: String,
    private val accountId: String,
    private val blockChainUid: String,
    private val getTokenByBlockchainUidUseCase: GetTokenByBlockchainUidUseCase,
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase,
    private val getAllCoinUseCase: GetAllCoinUseCase
) : BaseScreenModel() {

    init {
        getSupportedToken()
    }

    lateinit var listSupportedToken: Flow<PagingData<SwapTokenUiModel.TokenUiModel>>
        private set

    private val _isInitListSupportedToken = MutableStateFlow(false)
    val isInitListSupportedTokenFlow = _isInitListSupportedToken.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
    }

    private fun getSupportedToken() {
        screenModelScope.launch {
            val result = getAccountBalanceUseCase(
                forceReload = false,
                // TODO: Pass in correct type of address if needed
                address = accountAddress,
                accountId = accountId,
                sparkline = true
            )

            val listCoin = getAllCoinUseCase()

            listSupportedToken =
                getTokenByBlockchainUidUseCase.invokePaging(blockChainUid).map { pagingData ->
                    pagingData.filter {
                        it.decimals != null
                    }.map { tokenEntity: TokenEntity ->
                        SwapTokenUiModel.TokenUiModel(
                            tokenCode = result.find { it.tokenId == tokenEntity.id }?.contractSymbol
                                ?: listCoin.find { it.uid == tokenEntity.coinUid }?.code
                                ?: "Error",
                            logoUrl = result.find { it.tokenId == tokenEntity.id }?.logoUrl
                                ?: "",
                            balance = result.find { it.tokenId == tokenEntity.id }
                                ?.formattedBalanceForHuman() ?: "0.0000",
                            address = tokenEntity.reference ?: "",
                            decimal = tokenEntity.decimals!!,
                            isNative = tokenEntity.type == "native"
                        )
                    }
                }
            delay(200)
            _isInitListSupportedToken.value = true
        }

    }
}