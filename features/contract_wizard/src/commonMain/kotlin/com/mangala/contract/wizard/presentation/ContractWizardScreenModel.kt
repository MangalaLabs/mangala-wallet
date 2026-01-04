package com.mangala.contract.wizard.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.contract.wizard.data.model.ContractWizardRequest
import com.mangala.contract.wizard.data.model.Features
import com.mangala.contract.wizard.data.model.Metadata
import com.mangala.contract.wizard.domain.usecases.CreateContractWizardUseCase
import com.mangala.contract.wizard.domain.usecases.DeployContractWizardUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ContractWizardScreenModel(
    private val createContractWizardUseCase: CreateContractWizardUseCase,
    private val deployContractWizardUseCase: DeployContractWizardUseCase,
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase
): BaseScreenModel() {

    private val _uiState = MutableStateFlow<ContractWizardScreenUiState>(
        ContractWizardScreenUiState.Loading)
    val uiState: StateFlow<ContractWizardScreenUiState> get() = _uiState

    override fun doOnComposableStarted() {
        lifecycleScope.launch { getAccounts() }
    }

    fun createContractWizard() {
        kotlinx.coroutines.GlobalScope.launch {
            val data = createContractWizardUseCase.invoke(
                body = ContractWizardRequest(
                    metadata = Metadata(
                        "Ethan", "ETHR", "1000"
                    ),
                    features = Features(
                        true, true, true, true, true, true
                    ),
                    upgradeable = "transparent"
                )
            )
            val result = deployContractWizardUseCase.invoke(
                data
            )

            _uiState.update {
                (it as? ContractWizardScreenUiState.Data)?.copy(deploy = result?.deployTx ?: "") ?: it
            }
        }
    }

    private val blockchainType: BlockchainType = BlockchainType.PolygonMumbai

    private suspend fun getAccounts() {
        val blockchainData = BlockchainNetworkData.getAllBlockchainNetworkSupported().find { it.blockChainUid == blockchainType.uid }
        val result = getSelectedWalletAccountsUseCase()
        result?.let { list ->
            _uiState.update { ContractWizardScreenUiState.Data(
                accounts = list,
                chainId = blockchainData?.chainId ?: 1L,
                rpcUrl = blockchainType.getRpcUrl().getOrNull(0) ?: "",
                deploy = ""
            ) }
        }
    }
}