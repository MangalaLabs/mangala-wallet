package com.mangala.wallet.features.send_base.transactionfee

import com.mangala.wallet.features.chains.ui.BitcoinFeeOptionUiModel
import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.features.chains.ui.FeeOptionUiModel
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TransactionFeeScreenModel(
    private val transactionFeeOptions: List<FeeOptionUiModel>
) : BaseScreenModel() {

    private val _uiModel = MutableStateFlow(TransactionFeeScreenUiModel())
    val uiModel: StateFlow<TransactionFeeScreenUiModel> = _uiModel

    init {
        buildTransactionFeeOptions()
    }

    fun setSelectedFeeOption(feeOption: FeeOptionUiModel) {
        _uiModel.update { uiModel ->
            uiModel.copy(feeOptions = uiModel.feeOptions.map { 
                when {
                    it is EvmFeeOptionUiModel && feeOption is EvmFeeOptionUiModel -> 
                        it.copy(isSelected = it == feeOption)
                    it is BitcoinFeeOptionUiModel && feeOption is BitcoinFeeOptionUiModel -> 
                        it.copy(isSelected = it == feeOption)
                    else -> it
                }
            })
        }
    }

    fun getSelectedFeeOption(): FeeOptionUiModel? {
        return _uiModel.value.feeOptions.find { it.isSelected }
    }

    private fun buildTransactionFeeOptions() {
        _uiModel.update { uiModel ->
            uiModel.copy(feeOptions = transactionFeeOptions)
        }
    }
}