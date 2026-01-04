package com.mangala.browser_bridge_base

import cafe.adriel.voyager.core.model.screenModelScope
import com.benasher44.uuid.uuid4
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.core.address.domain.usecases.DeriveEthereumAddressUseCase
import com.mangala.wallet.core.hdwallet.domain.model.HDKey
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.transaction.fee.TransactionFeeType
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.evmcompatible.core.amountToBigInt
import com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura.FeeHistoryModel
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EstimateGasUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetFeeHistoryUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetNonceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SignPersonalMessageUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SignTransactionUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionType
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionDataResponse
import com.mangala.wallet.features.chains.evmcompatible.utils.Numeric
import com.mangala.wallet.features.chains.ui.FeeOptionUiModel
import com.mangala.wallet.features.chains.ui.SendTransactionScreenModel
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.utils.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent

class ConfirmTransactionViewModel(
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getFeeHistoryUseCase: GetFeeHistoryUseCase,
    private val signTransactionUseCase: SignTransactionUseCase,
    private val estimateGasUseCase: EstimateGasUseCase,
    private val generateHDKeyUseCase: GenerateHDKeyUseCase,
    private val deriveAddressUseCase: DeriveEthereumAddressUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    private val getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    private val getRecommendedGasPriceUseCase: GetRecommendedGasPriceUseCase,
    private val getTransactionFeeOptionsUseCase: GetTransactionFeeOptionsUseCase,
    private val signPersonalMessageUseCase: SignPersonalMessageUseCase,
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase,
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val json: Json,
    getLatestBlockUseCase: GetLatestBlockUseCase,
    private val getNonceUseCase: GetNonceUseCase
) : SendTransactionScreenModel(
    fetchTokenPriceUseCase,
    getNativeCoinUseCase,
    getTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase,
    getTransactionFeeOptionsUseCase,
    getCurrentCurrencyCodeUseCase,
    getLatestBlockUseCase
), KoinComponent {

    // TODO: Support switch network

    private var hdKey: HDKey? = null

    private fun getNativeCoinPrice(accountId: String): TokenBalanceEntity {
        return getTokenBalanceByTokenIdUseCase(nativeCoin.id, accountId).first()
    }

    private val _uiState = MutableStateFlow<ConfirmTransactionScreenUiState>(
        ConfirmTransactionScreenUiState.Loading)
    val uiState: StateFlow<ConfirmTransactionScreenUiState> get() = _uiState

    private val _getFeeHistory = MutableStateFlow<FeeHistoryModel?>(null)
    val getFeeHistory = _getFeeHistory

    private var refreshJob: Job? = null

    private var networkJob: Job? = null
    private var latestNonce: Long? = null

    init {
        gasPrice = GasPrice.Legacy(10_000_000_000)
        gasLimitInWei = BigDecimal.ZERO
    }

    fun onTransactionFeeSelected(transactionFeeOption: FeeOptionUiModel) {
        _uiState.update {
            (it as? ConfirmTransactionScreenUiState.Data)?.copy(selectedTransactionFee = transactionFeeOption)
                ?: it
        }
    }
    fun calTransactionFee(
        accountId: String,
        toAddress: String,
        payload: String,
        amount: String,
        coinDecimals: Long,
        isContract: Boolean = false
    ) {
        networkJob?.cancel()
        networkJob = screenModelScope.launch {
            val networkSelected = getSelectedNetworkUseCase.invoke()
            blockchainType = BlockchainType.fromUid(networkSelected.blockChainUid)

            val currentAccount = getAccountByIdUseCase(accountId)
            _uiState.update {
                if (it !is ConfirmTransactionScreenUiState.Data) {
                    ConfirmTransactionScreenUiState.Data(
                        currentAccount,
                        null,
                        "",
                        null,
                        null,
                        null,
                        emptyList(),
                    )
                } else {
                    it
                }
            }
            calculateTransactionFee(
                accountId,
                currentAccount.bip44Address,
                toAddress,
                payload,
                amount,
                coinDecimals,
                isContract
            )
        }
    }

    private fun calculateTransactionFee(
        accountId: String,
        fromAddress: String,
        toAddress: String,
        payload: String,
        amount: String,
        coinDecimals: Long,
        isContract: Boolean = false
    ) {
        this.accountId = accountId
        refreshJob?.cancel()
        refreshJob = screenModelScope.launch {
            while (isActive) {
                _uiState.update {
                    val oldUiState = (it as? ConfirmTransactionScreenUiState.Data)
                    val oldSelectedTransactionFee = oldUiState?.selectedTransactionFee

                    val gasLimit = calculateGasLimit(
                        fromAddress,
                        toAddress,
                        payload,
                        amount,
                        isContract
                    )?.getBufferedGasLimit() ?: return@launch

                    latestNonce = getNonceUseCase.getNonceLong(
                        blockchainType.getRpcUrl().first(),
                        currentId.getAndIncrement(),
                        Address(fromAddress)
                    )

                    val gasLimitRawValue = BigDecimal.parseString(gasLimit.toString())
                    gasLimitInWei = gasLimitRawValue
                    val gasLimitValue = gasLimitRawValue.toPlainString()

                    val feeOptions = getTransactionFeeOptions() ?: return@launch
                    val transactionFeeType =
                        oldSelectedTransactionFee?.transactionFee?.transactionFeeType
                            ?: TransactionFeeType.REGULAR
                    val transactionFeeOptionUiModels = getTransactionFeeOptionUiModels(
                        feeOptions,
                        transactionFeeType,
                        gasLimitInWei
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
                        gasValue = gasLimitValue,
                        gasPrice = this@ConfirmTransactionViewModel.gasPrice,
                        transactionFeeOptions = transactionFeeOptionUiModels,
                        selectedTransactionFee = gasFeeOption,
                        estimateGasErrorVisible = false
                    ) ?: it
                }

                delay(Constants.TRANSACTION_FEE_REFRESH_INTERVAL)
            }
        }
    }

    private suspend fun calculateGasLimit(
        fromAddress: String,
        toAddress: String,
        payload: String,
        amount: String,
        isContract: Boolean = false
    ): Long? {

        val transactionData = etherTransferTransactionData(
            Address(toAddress),
            amount.amountToBigInt(),
            Numeric.hexStringToByteArray(payload)
        )

        val to = if(isContract){
            null
        }else{
            Address(toAddress)
        }

        val selectedTransactionFee = (_uiState.value as? ConfirmTransactionScreenUiState.Data)?.selectedTransactionFee?.transactionFee

        return estimateGasUseCase.invoke(
            rpcUrl,
            currentId.getAndIncrement(),
            Address(fromAddress),
            to,
            amount.amountToBigInt(),
            getPreferredGasPrice(selectedTransactionFee),
            transactionData,
            isContract
        )
    }

    private val _estimateGas = MutableStateFlow<Long?>(null)
    val estimateGas = _estimateGas
//    fun estimateGas(recipient: String, payload: String, amount: String) {
//        DebugLog.log("1991 recipient: $recipient")
//        DebugLog.log("1991 payload: $payload")
//        DebugLog.log("1991 amount: $amount")
//        restrictHDKey()
//        kotlinx.coroutines.GlobalScope.launch {
//            val dataFee = getFeeHistoryUseCase.invoke(rpcUrl, currentId.getAndIncrement())
//            try {
//                val feeHistoryDto = json.decodeFromString(FeeHistoryDto.serializer(), dataFee)
//                val feeHistoryModel = feeHistoryDto?.result?.mapToDomainModel()
//                feeHistoryModel?.let {
//                    handleFeeHistory(it)
//                }
//                _getFeeHistory.value = feeHistoryModel
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            val transactionData = buildPayloadTransactionData(
//                Address(recipient),
//                Numeric.hexStringToByteArray(payload)
//            )
//
//            val data = estimateGasUseCase.invoke(
//                rpcUrl,
//                currentId.getAndIncrement(),
//                getAddress(hdKey!!.publicKey),
//                Address(recipient),
//                amount.amountToBigInt(),
//                gasPrice,
//                transactionData
//            )
//            _estimateGas.value = data
//        }
//    }

    private fun buildPayloadTransactionData(
        contractAddress: Address,
        input: ByteArray
    ): TransactionData {
        return TransactionData(
            to = contractAddress,
            value = BigInteger.ZERO,
            input = input
        )
    }

    private fun etherTransferTransactionData(
        address: Address,
        value: BigInteger,
        input: ByteArray
    ): TransactionData {
        return TransactionData(address, value, input)
    }

    private val _signTransaction = MutableStateFlow<TransactionDataResponse?>(null)
    val signTransactionResult: StateFlow<TransactionDataResponse?> = _signTransaction.asStateFlow()

    // TODO: SignTransactionRequest should only be for the specific ui variant implementation
    fun getSignTransactionRequest(
        recipient: Address?,
        payload: String,
        amount: String,
    ): SignTransactionRequest? {
        (_uiState.value as? ConfirmTransactionScreenUiState.Data)?.let {
            val currentAccount = getAccountByIdUseCase(accountId)
            val wallet = getSelectedWalletUseCase()

            return SignTransactionRequest(
                requestId = uuid4().toString(),
                walletId = wallet?.id ?: return null,
                accountId = accountId,
                fromAddress = currentAccount.bip44Address,
                nonce = latestNonce ?: 0,
                blockchainType = chain.toBlockchainType(),
                transactionData = TransactionData(recipient, amount.amountToBigInt(), Numeric.hexStringToByteArray(payload)),
                gasPrice = getPreferredGasPrice(it.selectedTransactionFee?.transactionFee),
                gasLimit = it.estimatedGasLimit ?: return null,
                gasFiatValue = it.selectedTransactionFee?.transactionFeeFiatValueString.orEmpty(),
                transactionType = SignTransactionType.SignWeb3(
                    url = "", // TODO: #324 fill in data
                    payload = ""
                ),
                contactName = null, // Always null, since we don't use contact system in browser
                contactAddress = null
            )
        } ?: return null
    }

    // TODO: signTransaction should only be for the specific pro variant implementation
    fun signTransaction(
        accountId: String,
        recipient: String,
        isLegacyTransaction: Boolean,
        nonce: Long,
        payload: String,
        amount: String,
        coinDecimals: Long
    ) {
        (_uiState.value as? ConfirmTransactionScreenUiState.Data)?.let {
            val currentAccount = getAccountByIdUseCase(accountId)
            restrictHDKey()
            kotlinx.coroutines.GlobalScope.launch {
                val data = signTransactionUseCase(
                    accountId,
                    hdKey!!,
                    chain,
                    isLegacyTransaction,
                    Address(currentAccount.bip44Address),
                    recipient,
                    amount,
                    Numeric.hexStringToByteArray(payload),
                    getPreferredGasPrice(it.selectedTransactionFee?.transactionFee),
                    it.estimatedGasLimit,
                    rpcUrl,
                    nonce,
                    coinDecimals
                )
//            Timber.d("1991 signTransaction " + data)

                try {
                    if(!data.isNullOrEmpty()){
                        val result = json.decodeFromString(TransactionDataResponse.serializer(), data)
                        _signTransaction.value = result
                        _uiState.update { (it as? ConfirmTransactionScreenUiState.Data)?.copy(txHash = result.result) ?: it }

                    }else{
                        _signTransaction.value = TransactionDataResponse(null, null, null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _signTransaction.value = TransactionDataResponse(null, null, null)
                }
            }
        }
    }

    fun createContract(accountId: String, payload: String, amount: String) {


        (_uiState.value as? ConfirmTransactionScreenUiState.Data)?.let {
            val currentAccount = getAccountByIdUseCase(accountId)
            currentAccount.bip44Address
            restrictHDKey()
            kotlinx.coroutines.GlobalScope.launch {
                val data = signTransactionUseCase.createContract(
                    hdKey!!,
                    chain,
                    Address(currentAccount.bip44Address),
                    Numeric.hexStringToByteArray(payload),
                    gasPrice,
                    it.estimatedGasLimit,
                    rpcUrl,
                    -1L,
                    amount
                )
//            Timber.d("1991 signTransaction " + data)

                try {
                    if(!data.isNullOrEmpty()){
                        val result = json.decodeFromString(TransactionDataResponse.serializer(), data)
                        _signTransaction.value = result
                        _uiState.update { (it as? ConfirmTransactionScreenUiState.Data)?.copy(txHash = result.result) ?: it }

                    }else{
                        _signTransaction.value = TransactionDataResponse(null, null, null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _signTransaction.value = TransactionDataResponse(null, null, null)
                }
            }
        }
    }

    private val _signPersonalMessage = MutableStateFlow<ByteArray?>(null)
    val signPersonalMessage = _signPersonalMessage.asStateFlow()
    fun signPersonalMessage(message: ByteArray){
        restrictHDKey()
        kotlinx.coroutines.GlobalScope.launch {
            val data = signPersonalMessageUseCase.invoke(
                hdKey!!,
                chain,
                message
            )
            _signPersonalMessage.value = data
        }
    }


    private fun restrictHDKey() {
        val wallet = getSelectedWalletUseCase()
        val words = wallet?.words?.split(" ")
        hdKey = generateHDKeyUseCase.invoke(
            words ?: listOf(),
            "",
            Blockchain(blockchainType, blockchainType.uid, ""),
            AddressType.Bip44
        )
    }

    fun getAddress(): Address? {
        hdKey?.let {
            return Address(deriveAddressUseCase.invoke(it.publicKey))
        }
        return null
    }

    fun getAddress(publicKey: ByteArray): Address {
        return Address(deriveAddressUseCase.invoke(publicKey))
    }
}