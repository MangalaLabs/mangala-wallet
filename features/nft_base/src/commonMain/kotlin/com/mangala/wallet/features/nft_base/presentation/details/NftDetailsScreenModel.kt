package com.mangala.wallet.features.nft_base.presentation.details

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.GetNftByTokenIdUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.GetNftFavoriteStatusUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.UpdateNftFavoriteStatusUseCase
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NftDetailsScreenModel(
    private val accountId: String,
    private val collectionContractAddress: String,
    private val tokenId: String,
    private val getNftByTokenIdUseCase: GetNftByTokenIdUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getNftFavoriteStatusUseCase: GetNftFavoriteStatusUseCase,
    private val updateNftFavoriteStatusUseCase: UpdateNftFavoriteStatusUseCase
): BaseScreenModel() {

    private val _uiState =
        MutableStateFlow<NftDetailsScreenUiState>(NftDetailsScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private lateinit var selectedBlockchainUid: String

    init {
        screenModelScope.launch {
            val response = getNftByTokenIdUseCase(
                accountId,
                collectionContractAddress,
                tokenId
            )
            _uiState.update {
                response?.let {
                    NftDetailsScreenUiState.Success(NftDetailsScreenUiModel(response))
                } ?: kotlin.run {
                    NftDetailsScreenUiState.Error(WrappedStringResource.StringRes(MR.strings.all_error_no_params))
                }
            }

            // We're not getting selected network as flow because when we changes chain
            // we navigate back and clear this screen anyways
            selectedBlockchainUid = getSelectedNetworkUseCase().blockChainUid

            getNftFavoriteStatusUseCase(
                blockchainUid = selectedBlockchainUid,
                accountId,
                collectionContractAddress,
                tokenId
            ).collectLatest { isFavorite ->
                _uiState.update {
                    (it as? NftDetailsScreenUiState.Success)?.let { successState ->
                        successState.copy(
                            uiModel = successState.uiModel.copy(
                                nftCollection = successState.uiModel.nftCollection.copy(
                                    nft = successState.uiModel.nftCollection.nft.map { nft ->
                                        nft.copy(
                                            isFavorite = isFavorite ?: false
                                        )
                                    }
                                )
                            )
                        )
                    } ?: it
                }
            }
        }
    }

    fun toggleNftFavoriteStatus() {
        val isFavorite = (uiState.value as? NftDetailsScreenUiState.Success)?.uiModel?.nftCollection?.nft?.firstOrNull()?.isFavorite

        screenModelScope.launch {
            updateNftFavoriteStatusUseCase(
                blockchainUid = selectedBlockchainUid,
                accountId,
                collectionContractAddress,
                tokenId,
                isFavorite = isFavorite?.not() ?: false
            )
        }
    }
}