package com.mangala.wallet.features.nft_base.presentation.import

import cafe.adriel.voyager.core.concurrent.AtomicInt32
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.nft_base.domain.usecases.ImportNftUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

class ImportNftScreenModel(
    private val importNftUseCase: ImportNftUseCase
): BaseScreenModel() {

    private val currentId = AtomicInt32(Random.nextInt(100))

    private val _uiModel: MutableStateFlow<ImportNftScreenUiModel> = MutableStateFlow(
        ImportNftScreenUiModel("", "")
    )
    val uiModel: StateFlow<ImportNftScreenUiModel> get() = _uiModel.asStateFlow()

    fun onChangeContractAddress(contractAddress: String) {
        _uiModel.value = _uiModel.value.copy(contractAddress = contractAddress)
    }

    fun onChangeTokenId(tokenId: String) {
        _uiModel.value = _uiModel.value.copy(tokenId = tokenId)
    }

    fun onClickImport() {
        val uiModel = _uiModel.value

        screenModelScope.launch {
            val result = importNftUseCase(
                "e722f16d-345c-4fa4-b301-9336f06e29b9", // TODO: Pass in real data
                uiModel.contractAddress,
                currentId.getAndIncrement(),
                uiModel.tokenId.toBigInteger(),
                "0x7876e46330fAa0c305E50fb4ee6c52819868053f", // TODO: Pass in real data
                DefaultBlockParameter.Latest
            )

        }
    }

}