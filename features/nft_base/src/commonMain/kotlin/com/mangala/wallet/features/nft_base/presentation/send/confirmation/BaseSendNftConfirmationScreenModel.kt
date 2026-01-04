package com.mangala.wallet.features.nft_base.presentation.send.confirmation

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.transaction.fee.TransactionFeeType
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetNonceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.features.chains.ui.SendTransactionScreenModel
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.features.nft_base.domain.usecases.GetNftByTokenIdUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.SendNftUseCase
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.Constants.TRANSACTION_FEE_REFRESH_INTERVAL
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// Prevents this class from being instantiated by making it abstract
abstract class BaseSendNftConfirmationScreenModel(
    blockchainUid: String,
    accountId: String,
    private val toAddress: String, // use this only to calculate recipientAddress
    collectionContractAddress: String,
    tokenId: String,
    contactId: Long?,
    protected val sendNftUseCase: SendNftUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getNftByTokenIdUseCase: GetNftByTokenIdUseCase,
    private val getContactByIdUseCase: GetContactByIdUseCase,
    getRecommendedGasPriceUseCase: GetRecommendedGasPriceUseCase,
    getTransactionFeeOptionsUseCase: GetTransactionFeeOptionsUseCase,
    fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    getNativeCoinUseCase: GetNativeCoinUseCase,
    getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getLatestBlockUseCase: GetLatestBlockUseCase,
    private val getNonceUseCase: GetNonceUseCase
): SendTransactionScreenModel(
    fetchTokenPriceUseCase,
    getNativeCoinUseCase,
    getTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase,
    getTransactionFeeOptionsUseCase,
    getCurrentCurrencyCodeUseCase,
    getLatestBlockUseCase
) {

    private var refreshJob: Job? = null
    private var gasLimit: Long? = null
    private var nftCollection: NftCollection? = null
    protected lateinit var recipientAddress: String
        private set
    protected lateinit var currentAccount: AccountModel
        private set
    protected var nonce: Long? = null
        private set

    protected val _uiState = MutableStateFlow<SendNftConfirmationScreenUiState>(
        SendNftConfirmationScreenUiState.Loading
    )
    val uiState = _uiState.asStateFlow()

    init {
        this.accountId = accountId
        this.blockchainType = BlockchainType.fromUid(blockchainUid)

        screenModelScope.launch {
            currentAccount = getAccountByIdUseCase(accountId)
            val contact = contactId?.let {
                getContactByIdUseCase(it)
            }
            val fromAddress = currentAccount.bip44Address

            val response = getNftByTokenIdUseCase(
                accountId,
                collectionContractAddress,
                tokenId
            )

            recipientAddress = contact?.address ?: toAddress

            response?.let { nft ->
                nftCollection = nft

                _uiState.update {
                    SendNftConfirmationScreenUiState.Data(
                        currentAccount,
                        contact,
                        nft,
                        blockchainType,
                        null,
                        null,
                        null,
                        emptyList(),
                        null,
                        estimateGasErrorVisible = false,
                        recipientAddress
                    )
                }

                calculateTransactionFee(nft, fromAddress)
            }
        }
    }

    fun onAuthenticationSuccess() {
        sendNft()
    }

    fun onTransactionFeeSelected(transactionFeeOption: EvmFeeOptionUiModel) {
        _uiState.update {
            (it as? SendNftConfirmationScreenUiState.Data)?.copy(
                selectedTransactionFee = transactionFeeOption,
                transactionFeeOptions = it.transactionFeeOptions.map { feeOption ->
                    val isSelected = feeOption.transactionFee.transactionFeeType == transactionFeeOption.transactionFee.transactionFeeType
                    feeOption.copy(isSelected = isSelected)
                }
            )
                ?: it
        }
    }

    fun getTxHash(): String? {
        return (_uiState.value as? SendNftConfirmationScreenUiState.Data)?.txHash
    }

    fun onConsumeTxHash() {
        _uiState.update { (it as? SendNftConfirmationScreenUiState.Data)?.copy(txHash = null) ?: it }
        stopRefreshJob()
    }

    private fun stopRefreshJob() {
        refreshJob?.cancel()
    }

    private fun sendNft() {
        (uiState.value as? SendNftConfirmationScreenUiState.Data)?.let {
            screenModelScope.launch {
                val blockchainType = it.blockchainType
                val rpcUrl = blockchainType.getRpcUrl().first()
                val selectedTransactionFee = it.selectedTransactionFee?.transactionFee

                val preferredGasPrice = getPreferredGasPrice(selectedTransactionFee)

                nftCollection?.let {
                    val txHash = sendNftUseCase(
                        accountId,
                        blockchainType,
                        AddressType.Bip44,
                        recipientAddress,
                        preferredGasPrice,
                        rpcUrl,
                        gasLimit,
                        it
                    )
                    _uiState.update {
                        (it as? SendNftConfirmationScreenUiState.Data)?.copy(txHash = txHash) ?: it
                    }
                }
            }
        }
    }

    private fun calculateTransactionFee(nftCollection: NftCollection, fromAddress: String) {
        refreshJob?.cancel()
        refreshJob = screenModelScope.launch {
            while (isActive) {
                _uiState.update {
                    val oldUiState = (it as? SendNftConfirmationScreenUiState.Data)
                    val oldSelectedTransactionFee = oldUiState?.selectedTransactionFee
                    val transactionFeeOptions = getTransactionFeeOptions()

                    gasLimit = sendNftUseCase.estimateGas(
                        rpcUrl = rpcUrl,
                        nftCollection = nftCollection,
                        fromAddress = fromAddress,
                        toAddress = recipientAddress,
                        preferredGasPrice = getPreferredGasPrice(transactionFeeOptions?.find { it.transactionFeeType == TransactionFeeType.REGULAR })
                    )?.getBufferedGasLimit() ?: kotlin.run {
                        _uiState.update {
                            (it as? SendNftConfirmationScreenUiState.Data)?.copy(
                                estimateGasErrorVisible = true
                            ) ?: it
                        }
                        return@launch
                    }
                    val gasLimitRawValue = BigDecimal.parseString(gasLimit.toString())
                    gasLimitInWei = gasLimitRawValue

                    val feeOptions = getTransactionFeeOptions() ?: return@launch
                    val transactionFeeType =
                        oldSelectedTransactionFee?.transactionFee?.transactionFeeType
                            ?: TransactionFeeType.REGULAR
                    val transactionFeeOptionUiModels = getTransactionFeeOptionUiModels(
                        feeOptions,
                        transactionFeeType,
                        gasLimitInWei
                    )
                    // TODO: Move this nonce logic elsewhere
                    nonce = getNonceUseCase.getNonceLong(
                        rpcUrl,
                        currentId.getAndIncrement(),
                        Address(currentAccount.bip44Address),
                        DefaultBlockParameter.Pending
                    )
                    val gasFeeOption = transactionFeeOptionUiModels?.let { it1 ->
                        getCurrentFeeOptionUiModel(
                            it1,
                            transactionFeeType,
                        )
                    }

                    if (gasLimit == null || gasFeeOption == null) {
                        _uiState.update {
                            oldUiState?.copy(estimateGasErrorVisible = true) ?: it
                        }
                        return@launch
                    }

                    oldUiState?.copy(
                        estimatedGasLimit = gasLimit,
                        gasPrice = this@BaseSendNftConfirmationScreenModel.gasPrice,
                        transactionFeeOptions = transactionFeeOptionUiModels,
                        selectedTransactionFee = gasFeeOption,
                        estimateGasErrorVisible = false
                    ) ?: it
                }
                delay(TRANSACTION_FEE_REFRESH_INTERVAL)
            }
        }
    }

    // TODO: Should only be for UI variant
    fun stopGasRefreshJob() {
        refreshJob?.cancel()
    }

    fun restartGasRefreshJob() {
        nftCollection?.let {
            calculateTransactionFee(
                it,
                currentAccount.bip44Address,
            )
        }
    }
}