package com.mangala.wallet.features.addressbook.presentation.blockchain

import cafe.adriel.voyager.core.model.screenModelScope
import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.TokenInformationEntity
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.AddBlockchainTypeUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.AddTokenInformationUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetAllBlockchainTypesUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetTokensForBlockchainUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.ValidateAddressUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class BlockchainScreenModel(
    private val getAllBlockchainTypesUseCase: GetAllBlockchainTypesUseCase,
    private val getTokensForBlockchainUseCase: GetTokensForBlockchainUseCase,
    private val validateAddressUseCase: ValidateAddressUseCase,
    private val addBlockchainTypeUseCase: AddBlockchainTypeUseCase,
    private val addTokenInformationUseCase: AddTokenInformationUseCase
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(BlockchainUIState())
    val uiState: StateFlow<BlockchainUIState> = _uiState.asStateFlow()

    init {
        loadBlockchainTypes()
    }

    private fun loadBlockchainTypes() {
        screenModelScope.launch {
            try {
//                _uiState.update { it.copy(isLoading = true) }
                val blockchainTypes = getAllBlockchainTypesUseCase()


                println("blockchainTypes = $blockchainTypes")

                _uiState.update {
                    it.copy(
                        blockchainTypes = blockchainTypes,
                        isLoading = false
                    )
                }

                // If blockchain types are loaded and there's at least one, select the first one
                if (blockchainTypes.isNotEmpty()) {
                    selectBlockchainType(blockchainTypes.first().id)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to load blockchain types",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectBlockchainType(blockchainTypeId: String) {
        screenModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Find the selected blockchain type
                val selectedType = _uiState.value.blockchainTypes.find { it.id == blockchainTypeId }

                // Load tokens for selected blockchain
                val tokens = getTokensForBlockchainUseCase(blockchainTypeId)

                _uiState.update {
                    it.copy(
                        selectedBlockchainType = selectedType,
                        tokensForSelectedBlockchain = tokens,
                        walletAddress = it.walletAddress.copy(blockchainTypeId = blockchainTypeId),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to load blockchain details",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateWalletAddress(address: String) {
        _uiState.update {
            it.copy(
                walletAddress = it.walletAddress.copy(address = address),
                isAddressValid = null // Reset validation until manually validated
            )
        }
    }

    fun updateWalletAlias(alias: String) {
        _uiState.update {
            it.copy(
                walletAddress = it.walletAddress.copy(alias = alias)
            )
        }
    }

    fun validateWalletAddress() {
        screenModelScope.launch {
            val address = _uiState.value.walletAddress.address
            val blockchainTypeId = _uiState.value.walletAddress.blockchainTypeId

            if (address.isBlank() || blockchainTypeId.isBlank()) {
                _uiState.update {
                    it.copy(
                        isAddressValid = false,
                        validationError = "Address or blockchain type is empty"
                    )
                }
                return@launch
            }

            try {
                val isValid = validateAddressUseCase(address, blockchainTypeId)
                _uiState.update {
                    it.copy(
                        isAddressValid = isValid,
                        validationError = if (isValid) null else "Invalid address format"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAddressValid = false,
                        validationError = e.message ?: "Validation failed"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetState() {
        _uiState.update { BlockchainUIState() }
        loadBlockchainTypes()
    }


    fun addSampleBlockchains() {
        screenModelScope.launch {
            try {
//                _uiState.update { it.copy(isLoading = true) }

                val sampleBlockchains = getSampleBlockchainTypes()
                sampleBlockchains.forEach { blockchain ->
                    addBlockchainTypeUseCase(blockchain)

                    val tokens = getSampleTokensForBlockchain(blockchain.id)
                    tokens.forEach { token ->
                        addTokenInformationUseCase(token)
                    }
                }

                println("addSampleBlockchains success")

                loadBlockchainTypes()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to add sample blockchain types",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun getSampleBlockchainTypes(): List<BlockchainTypeEntity> {
        val now = Clock.System.now()
        return listOf(
            BlockchainTypeEntity(
                id = "bitcoin-mainnet",
                name = "Bitcoin",
                symbol = "BTC",
                addressFormat = "^(bc1|[13])[a-zA-HJ-NP-Z0-9]{25,62}$",
                validationRegex = "^(bc1|[13])[a-zA-HJ-NP-Z0-9]{25,62}$",
                icon = null,
                color = "F7931A",
                networkType = BlockchainTypeEntity.NETWORK_MAINNET,
                isActive = true,
                createdAt = now,
                updatedAt = now
            ),
            BlockchainTypeEntity(
                id = "ethereum-mainnet",
                name = "Ethereum",
                symbol = "ETH",
                addressFormat = "^0x[a-fA-F0-9]{40}$",
                validationRegex = "^0x[a-fA-F0-9]{40}$",
                icon = null,
                color = "627EEA",
                networkType = BlockchainTypeEntity.NETWORK_MAINNET,
                isActive = true,
                createdAt = now,
                updatedAt = now
            ),
            BlockchainTypeEntity(
                id = "solana-mainnet",
                name = "Solana",
                symbol = "SOL",
                addressFormat = "^[1-9A-HJ-NP-Za-km-z]{32,44}$",
                validationRegex = "^[1-9A-HJ-NP-Za-km-z]{32,44}$",
                icon = null,
                color = "14F195",
                networkType = BlockchainTypeEntity.NETWORK_MAINNET,
                isActive = true,
                createdAt = now,
                updatedAt = now
            ),
            BlockchainTypeEntity(
                id = "binance-smart-chain",
                name = "Binance Smart Chain",
                symbol = "BSC",
                addressFormat = "^0x[a-fA-F0-9]{40}$",
                validationRegex = "^0x[a-fA-F0-9]{40}$",
                icon = null,
                color = "F0B90B",
                networkType = BlockchainTypeEntity.NETWORK_MAINNET,
                isActive = true,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    private fun getSampleTokensForBlockchain(blockchainId: String): List<TokenInformationEntity> {
        val now = Clock.System.now()
        return when (blockchainId) {
            "bitcoin-mainnet" -> listOf(
                TokenInformationEntity(
                    id = uuid4().toString(),
                    blockchainTypeId = blockchainId,
                    tokenName = "Bitcoin",
                    tokenSymbol = "BTC",
                    contractAddress = null,
                    decimals = 8,
                    isNative = true,
                    createdAt = now,
                    updatedAt = now,
                    icon = ""
                )
            )
            "ethereum-mainnet" -> listOf(
                TokenInformationEntity(
                    id = uuid4().toString(),
                    blockchainTypeId = blockchainId,
                    tokenName = "Ethereum",
                    tokenSymbol = "ETH",
                    contractAddress = null,
                    decimals = 18,
                    isNative = true,
                    createdAt = now,
                    updatedAt = now,
                    icon = ""
                ),
                TokenInformationEntity(
                    id = uuid4().toString(),
                    blockchainTypeId = blockchainId,
                    tokenName = "USD Coin",
                    tokenSymbol = "USDC",
                    contractAddress = "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48",
                    decimals = 6,
                    isNative = false,
                    createdAt = now,
                    updatedAt = now,
                    icon = ""
                ),
                TokenInformationEntity(
                    id = uuid4().toString(),
                    blockchainTypeId = blockchainId,
                    tokenName = "Tether USD",
                    tokenSymbol = "USDT",
                    contractAddress = "0xdac17f958d2ee523a2206206994597c13d831ec7",
                    decimals = 6,
                    isNative = false,
                    createdAt = now,
                    updatedAt = now,
                    icon = ""
                )
            )
            "solana-mainnet" -> listOf(
                TokenInformationEntity(
                    id = uuid4().toString(),
                    blockchainTypeId = blockchainId,
                    tokenName = "Solana",
                    tokenSymbol = "SOL",
                    contractAddress = null,
                    decimals = 9,
                    isNative = true,
                    createdAt = now,
                    updatedAt = now,
                    icon = ""
                )
            )
            "binance-smart-chain" -> listOf(
                TokenInformationEntity(
                    id = uuid4().toString(),
                    blockchainTypeId = blockchainId,
                    tokenName = "Binance Coin",
                    tokenSymbol = "BNB",
                    contractAddress = null,
                    decimals = 18,
                    isNative = true,
                    createdAt = now,
                    updatedAt = now,
                    icon = ""
                ),
                TokenInformationEntity(
                    id = uuid4().toString(),
                    blockchainTypeId = blockchainId,
                    tokenName = "BUSD Token",
                    tokenSymbol = "BUSD",
                    contractAddress = "0xe9e7cea3dedca5984780bafc599bd69add087d56",
                    decimals = 18,
                    isNative = false,
                    createdAt = now,
                    updatedAt = now,
                    icon = ""
                )
            )
            else -> emptyList()
        }
    }

}