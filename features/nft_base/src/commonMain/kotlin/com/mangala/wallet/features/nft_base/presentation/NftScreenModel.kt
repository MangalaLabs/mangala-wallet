package com.mangala.wallet.features.nft_base.presentation

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.GetNftBalanceUseCase
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class NftScreenModel(
    private val getNftBalanceUseCase: GetNftBalanceUseCase,
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<NftScreenUiState> =
        MutableStateFlow(NftScreenUiState.Loading(emptyList()))
    val uiState: StateFlow<NftScreenUiState> get() = _uiState.asStateFlow()

    private var fetchBalanceJob: Job? = null
    private var lastSelectedNetwork: BlockchainNetworkData? = null

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun pullToRefresh() {
        screenModelScope.launch {
            _isRefreshing.value = true
            _uiState.value =
                NftScreenUiState.Loading(if (_uiState.value.accounts != emptyList<NftScreenUiModel.AccountUiModel>()) _uiState.value.accounts else emptyList())
            loadData(true)
            _isRefreshing.value = false
        }
    }

    override fun doOnComposableStarted() {
        screenModelScope.launch {
            loadData()
        }
    }

    fun onSelectAccount(selectedAccountIndex: Int) {
        val newState = _uiState.updateAndGet {
            NftScreenUiState.Loading(it.accounts.mapIndexed { index, accountUiModel ->
                accountUiModel.copy(
                    isSelected = index == selectedAccountIndex
                )
            })
        }
        getNftBalanceForSelectedAccountFlow(
            accountId = newState.accounts[selectedAccountIndex].account.account.id,
            walletAddress = newState.accounts[selectedAccountIndex].account.bip44Address,
            forceRefresh = true
        )
    }

    fun onExpandCollection(collectionContractAddress: String) {
        // Saving the state here so we won't lose expanded state when switching to ETicket screen
        _uiState.update { currentState ->
            if (currentState is NftScreenUiState.Success) {
                val newCollections = currentState.collections.map {
                    it.copy(
                        isExpanded = if (it.contractAddress == collectionContractAddress) {
                            it.isExpanded.not()
                        } else {
                            it.isExpanded
                        }
                    )
                }
                currentState.copy(collections = newCollections)
            } else {
                currentState
            }
        }
    }

    suspend fun loadData(
        forceRefresh: Boolean = false
    ) {
        val selectedNetwork = getSelectedNetworkUseCase()
        val accounts = getSelectedWalletAccountsUseCase()

        val previouslySelectedAccountId =
            _uiState.value.selectedAccount?.account?.account?.id
        val currentlySelectedAccount =
            accounts?.find { it.account.id == previouslySelectedAccountId }
                ?: accounts?.firstOrNull()
        val currentlySelectedAccountId = currentlySelectedAccount?.account?.id

        val shouldReloadBalance =
            forceRefresh || currentlySelectedAccountId != previouslySelectedAccountId || selectedNetwork != lastSelectedNetwork
        lastSelectedNetwork = selectedNetwork

        if (shouldReloadBalance) {
            reloadNftBalance(
                accounts,
                currentlySelectedAccountId,
                currentlySelectedAccount,
                forceRefresh
            )
        } else {
            when (val currentState = _uiState.value) {
                is NftScreenUiState.Success -> {
                    if (currentState.accounts.map { it.account } != accounts) {
                        _uiState.update {
                            currentState.copy(
                                accountss = accounts.mapToUiModel(currentlySelectedAccountId)
                            )
                        }
                    }
                }

                is NftScreenUiState.Loading,
                is NftScreenUiState.Error -> {
                    _uiState.update {
                        NftScreenUiState.Success(
                            accountss = accounts.mapToUiModel(currentlySelectedAccountId),
                            collections = emptyList()
                        )
                    }
                }
            }
        }
    }

    private fun reloadNftBalance(
        accounts: List<AccountBlockchainModel>?,
        currentlySelectedAccountId: String?,
        currentlySelectedAccount: AccountBlockchainModel?,
        forceRefresh: Boolean
    ) {
        fetchBalanceJob?.cancel()
        _uiState.update {
            NftScreenUiState.Loading(
                accounts.mapToUiModel(
                    currentlySelectedAccountId
                )
            )
        }
        currentlySelectedAccount?.let {
            getNftBalanceForSelectedAccountFlow(
                accountId = it.account.id,
                walletAddress = it.bip44Address,
                forceRefresh = forceRefresh
            )
        }
    }

    private fun getNftBalanceForSelectedAccountFlow(
        accountId: String,
        walletAddress: String,
        forceRefresh: Boolean
    ) {
        fetchBalanceJob?.cancel()
        fetchBalanceJob = lifecycleScope.launch {
            getNftBalanceUseCase.invokeFlow(
                accountId = accountId,
                walletAddress = walletAddress,
                forceRefresh = forceRefresh
            ).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _uiState.update { currentState ->
                            val favoriteNfts = mutableListOf<NftScreenUiModel.NftItemUiModel>()

                            val collections = it.data?.map { collection ->
                                NftScreenUiModel.NftCollectionUiModel(
                                    collection.contractAddress,
                                    collection.contractName,
                                    collection.nft.map { item ->
                                        val nftItem = NftScreenUiModel.NftItemUiModel(
                                            collectionContractAddress = collection.contractAddress,
                                            tokenId = item.tokenId,
                                            name = item.name,
                                            description = item.description,
                                            imageUrl = item.image
                                        )

                                        if (item.isFavorite) {
                                            favoriteNfts.add(nftItem)
                                        }

                                        nftItem
                                    }
                                )
                            }

                            val favoriteCollection = if (favoriteNfts.isNotEmpty()) {
                                listOf(
                                    NftScreenUiModel.NftCollectionUiModel(
                                        "favorite",
                                        "Favorite",
                                        favoriteNfts,
                                        isFavorite = true
                                    )
                                )
                            } else emptyList()

                            NftScreenUiState.Success(
                                accountss = currentState.accounts,
                                collections = favoriteCollection + (collections ?: emptyList())
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            NftScreenUiState.Error(it.accounts, WrappedStringResource.StringRes(MR.strings.message_nft_screen_model_error_fetching_nft_balance))
                        }
                    }

                    is Resource.Loading -> {

                    }
                }
            }
        }
    }
}