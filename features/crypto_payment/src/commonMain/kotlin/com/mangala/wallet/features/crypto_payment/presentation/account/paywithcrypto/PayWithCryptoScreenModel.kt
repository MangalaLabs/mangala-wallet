package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.antelope_key_manager.domain.usecase.GenerateAccountKeyPairsUseCase
import com.mangala.wallet.domain.account.usecases.GetAccountBalanceUseCase
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveSelectedNetworkUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.token.usecases.GetTokenByIdUseCase
import com.mangala.wallet.domain.transaction.fee.TransactionFeeType
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EstimateGasUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.ui.SendTransactionScreenModel
import com.mangala.wallet.features.crypto_payment.SMART_CONTRACT_NATIVE_COIN_ADDRESS
import com.mangala.wallet.features.crypto_payment.WALLET_NATIVE_COIN_ADDRESS
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.CreateEosAccountRequest
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.SignMessageRequest
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.ApproveAllowanceUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.CreateEosAccountUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.GetTokenAllowanceUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.GetTokenSupportedListUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.PayEosAccountByEvmUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.SendSignMessageCreateAccountUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.GetCryptoPaymentContractAddressUseCase
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.utils.AppLifecycleObserver
import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.mangala.wallet.utils.calculatingDecimalMode
import com.mangala.wallet.utils.ext.ethToWei
import com.mangala.wallet.utils.ext.getByteArray
import com.memtrip.eos.core.crypto.EosPrivateKey.Companion.toEosPrivateKeyOrNull
import com.mmk.kmpnotifier.notification.NotifierManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PayWithCryptoScreenModel(
    private val paidAccountId: String,
    private val accountBlockchainTypeUid: String,
    private val eosOwnerPrivateKey: String?,
    private val eosActivePrivateKey: String?,
    private val getTokenSupportedListUseCase: GetTokenSupportedListUseCase,
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    getTransactionFeeOptionsUseCase: GetTransactionFeeOptionsUseCase,
    getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase: GetRecommendedGasPriceUseCase,
    getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getLatestBlockUseCase: GetLatestBlockUseCase,
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase,
    private val getRamPriceUseCase: GetRamPriceUseCase,
    private val sendSignMessageCreateAccountUseCase: SendSignMessageCreateAccountUseCase,
    private val payEosAccountByEvmUseCase: PayEosAccountByEvmUseCase,
    private val createEosAccountUseCase: CreateEosAccountUseCase,
    private val generateAccountKeyPairsUseCase: GenerateAccountKeyPairsUseCase,
    private val getWalletAccountsUseCase: GetWalletAccountsUseCase,
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val estimateGasUseCase: EstimateGasUseCase,
    private val getTokenByIdUseCase: GetTokenByIdUseCase,
    private val getTokenAllowanceUseCase: GetTokenAllowanceUseCase,
    private val approveAllowanceUseCase: ApproveAllowanceUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val saveSelectedNetworkUseCase: SaveSelectedNetworkUseCase,
    private val appLifecycleObserver: AppLifecycleObserver,
    private val getCryptoPaymentContractAddressUseCase: GetCryptoPaymentContractAddressUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
) : SendTransactionScreenModel(
    fetchTokenPriceUseCase,
    getNativeCoinUseCase,
    getTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase,
    getTransactionFeeOptionsUseCase,
    getCurrentCurrencyCodeUseCase,
    getLatestBlockUseCase
) {

    private var _uiState: MutableStateFlow<CreateAccountByEvmUiState> =
        MutableStateFlow(CreateAccountByEvmUiState.Loading)
    val uiState: StateFlow<CreateAccountByEvmUiState> = _uiState.asStateFlow()

    private var totalWei: String = "0"
    private var tokenPaid: String = ""
    private var newAccountName: String = ""
    private var tokenId: Long = 0

    private val accountBlockchainType = BlockchainType.fromUid(accountBlockchainTypeUid)

    private lateinit var paidAccount: AccountModel

    val checkAllowanceChannel = Channel<CheckAllowanceChannelData>(Channel.BUFFERED)

    private val _totalEvmSharedFlow = MutableSharedFlow<BigDecimal>()
    val totalEvmSharedFlow = _totalEvmSharedFlow.asSharedFlow()

    private var eosAccountKeyPairs: AccountKeyPairs? = null

    init {
        screenModelScope.launch {
            val detachedEosOwnerPrivateKey = eosOwnerPrivateKey?.toEosPrivateKeyOrNull()
            val detachedEosActivePrivateKey = eosActivePrivateKey?.toEosPrivateKeyOrNull()
            if (detachedEosOwnerPrivateKey != null && detachedEosActivePrivateKey != null) {
                eosAccountKeyPairs = AccountKeyPairs(
                    ownerKeyPair = detachedEosOwnerPrivateKey,
                    activeKeyPair = detachedEosActivePrivateKey
                )
            }

            println("account blockchain type: $accountBlockchainType")
            blockchainType = getSelectedNetworkUseCase().blockchainType
            println("pay with crypto blockchain type: $blockchainType")

            paidAccount = getAccountByIdUseCase(paidAccountId)

            loadData()
        }
    }

    private fun loadData() {
        println("paid account id: $paidAccountId")
        println("paidAccount: $paidAccount")
        prepareData()
    }

    private fun prepareData() {
        screenModelScope.launch {
            val currentBlockchainType = blockchainType
            println("before get token supported list")
            val tokenSupportedList =
                getTokenSupportedListUseCase(currentBlockchainType, currentId.getAndIncrement())
            println("token supported list: $tokenSupportedList")
            val accountBalanceList = getAccountBalanceUseCase(
                forceReload = false,
                address = paidAccount.bip44Address,
                blockchainType = currentBlockchainType,
                accountId = paidAccount.id,
                sparkline = false
            )
            println("account balance list: $accountBalanceList")
            val accountSupportedList = accountBalanceList.filter {
                println("it.isCoin: ${it.isCoin}")
                if (it.isCoin) {
                    it.contractAddress == WALLET_NATIVE_COIN_ADDRESS
                } else {
                    tokenSupportedList.contains(Address(it.contractAddress))
                }
            }

            if (accountSupportedList.isEmpty()) {
                _uiState.value =
                    CreateAccountByEvmUiState.Error(
                        WrappedStringResource.StringRes(
                            MR.strings.error_change_network_can_not_get_selected_account
                        )
                    )
                return@launch
            }

            // if account supported list has native token, use native token is used to pay
            // if not, first token is used to pay
            val defaultAccountPay = accountSupportedList.filter { it.isCoin }.getOrNull(0)
                ?: run { accountSupportedList[0] }
            val ramEos = calculateRamEos(MINIMUM_RAM_CREATE_EOS_ACCOUNT)
            val totalEosForCreateAccount = calculateTotalEos(ramEos)

            val uiModel = UiModel(
                cpu = MINIMUM_CPU_CREATE_EOS_ACCOUNT,
                net = MINIMUM_NET_CREATE_EOS_ACCOUNT,
                ram = ramEos,
                serviceFee = SERVICE_FEE_CREATE_EOS_ACCOUNT,
                totalEos = totalEosForCreateAccount,
                currentAccount = defaultAccountPay,
                accountSupportedList = accountSupportedList
            )
            _uiState.value = CreateAccountByEvmUiState.Success(uiModel)
        }
    }

    fun createAccount() {
        screenModelScope.launch {
            _uiState.value = CreateAccountByEvmUiState.Loading
            val chainId = accountBlockchainType.chainId
            val accountName = this@PayWithCryptoScreenModel.newAccountName
            val tokenPaid = this@PayWithCryptoScreenModel.tokenPaid
            val totalWeiString = this@PayWithCryptoScreenModel.totalWei
            println("total wei: $totalWei")
            val keyPairs = getEosKeyPairs()
            val currentAccountAddress = paidAccount.bip44Address
            val signMessageResponse = sendSignMessageCreateAccountUseCase(
                accountBlockchainType,
                SignMessageRequest(
                    newAccountName = accountName,
                    publicActiveKey = keyPairs.activeKeyPair.publicKey.toString(),
                    publicOwnerKey = keyPairs.ownerKeyPair.publicKey.toString(),
                    token = tokenPaid,
                    amount = totalWeiString,
                    evmAddress = currentAccountAddress,
                    chainId = chainId
                )
            ) ?: run {
                _uiState.value = CreateAccountByEvmUiState.Error(
                    WrappedStringResource.StringRes(
                        MR.strings.error_pay_with_crypto_call_send_sign_message_api_failed
                    )
                )
                return@launch
            }
            val signMessagesBytes = signMessageResponse.data.signature.getByteArray()
            println("sign message: ${signMessageResponse.data.signature}")
            val transactionFeeOptions = getTransactionFeeOptions()
            val transactionData = payEosAccountByEvmUseCase.buildTransactionData(
                blockchainType = blockchainType,
                newAccountName = accountName,
                publicActiveKey = keyPairs.activeKeyPair.publicKey.toString(),
                publicOwnerKey = keyPairs.ownerKeyPair.publicKey.toString(),
                token = Address(tokenPaid),
                amount = BigInteger.parseString(totalWeiString),
                signatures = signMessagesBytes,
                nonce = BigInteger.fromLong(signMessageResponse.data.nonce),
                chainId = chainId
            )
            println("transaction data: $transactionData")
            val gasEstimated = estimateGasUseCase(
                url = rpcUrl,
                id = currentId.getAndIncrement(),
                from = Address(currentAccountAddress),
                gasPrice = getPreferredGasPrice(transactionFeeOptions?.find { it.transactionFeeType == TransactionFeeType.REGULAR }),
                transactionData = transactionData,
                amount = transactionData.value,
                to = transactionData.to
            ) ?: run {
                _uiState.value =
                    CreateAccountByEvmUiState.Error(
                        WrappedStringResource.StringRes(
                            MR.strings.error_pay_with_crypto_estimate_gas_failed
                        )
                    )
                return@launch
            }
            println("gas estimated: $gasEstimated")

            val payEosAccountTxHash = payEosAccountByEvmUseCase(
                blockchainType = blockchainType,
                from = Address(currentAccountAddress),
                gasPrice = getPreferredGasPrice(transactionFeeOptions?.find { it.transactionFeeType == TransactionFeeType.REGULAR }),
                gas = gasEstimated,
                transactionData = transactionData
            ) ?: run {
                _uiState.value = CreateAccountByEvmUiState.Error(
                    WrappedStringResource.StringRes(
                        MR.strings.error_pay_with_crypto_sign_payment_transaction_failed
                    )
                )
                return@launch
            }

            val firebaseMessagingToken = try {
                NotifierManager.getPushNotifier().getToken()
            } catch (e: Exception) {
                null
            }

            val deviceId = firebaseMessagingToken ?: run {
                _uiState.value = CreateAccountByEvmUiState.Error(
                    WrappedStringResource.StringRes(
                        MR.strings.error_pay_with_crypto_get_device_id_failed
                    )
                )
                return@launch
            }
            println("device id: $deviceId")
            createEosAccountUseCase(
                accountBlockchainType,
                deviceId,
                CreateEosAccountRequest(
                    newAccountName = accountName,
                    publicActiveKey = keyPairs.activeKeyPair.publicKey.toString(),
                    publicOwnerKey = keyPairs.ownerKeyPair.publicKey.toString(),
                    token = tokenPaid,
                    amount = totalWeiString,
                    txEvmHash = payEosAccountTxHash,
                    chainId = chainId
                )
            ).let {
                if (it.isSuccess) {
                    saveAccountUseCase.invoke(
                        accountName = accountName,
                        activePrivateKey = keyPairs.activeKeyPair,
                        ownerPrivateKey = keyPairs.ownerKeyPair,
                        blockchainType = accountBlockchainType,
                        isTemp = true,
                        createAccountState = AntelopeAccount.CreateAccountState.EVM_CREATE_ACCOUNT_INITIALIZED
                    )
                    _uiState.value = CreateAccountByEvmUiState.AccountCreatingSuccessfully
                } else {
                    _uiState.value = CreateAccountByEvmUiState.Error(
                        WrappedStringResource.StringRes(
                            MR.strings.error_pay_with_crypto_save_account_failed
                        )
                    )
                }
            }

            changeToOriginalNetwork()
        }
    }

    private suspend fun getEosKeyPairs(): AccountKeyPairs {
        return eosAccountKeyPairs ?: generateAccountKeyPairsUseCase.invoke()
    }

    fun checkAllowance(
        accountName: String,
        totalEvm: BigDecimal,
        token: TokenBalanceModel,
    ) {
        screenModelScope.launch {
            this@PayWithCryptoScreenModel.newAccountName = accountName
            this@PayWithCryptoScreenModel.tokenId = token.tokenId
            val tokenDecimal = getTokenByIdUseCase(tokenId).first().decimals ?: run {
                _uiState.value = CreateAccountByEvmUiState.Error(
                    WrappedStringResource.StringRes(
                        MR.strings.error_pay_with_crypto_can_not_get_token_decimals
                    )
                )
                return@launch
            }
            val totalWeiBigDecimal = totalEvm.ethToWei(tokenDecimal.toInt())
            val totalWeiRawString = totalWeiBigDecimal.toPlainString()
            this@PayWithCryptoScreenModel.totalWei = if (totalWeiRawString.contains(".")) {
                totalWeiRawString.substring(0, totalWeiRawString.indexOf("."))
            } else {
                totalWeiRawString
            }

            // check allowance
            val tokenPaid = getTokenPaid(token.contractAddress)
            this@PayWithCryptoScreenModel.tokenPaid = tokenPaid
            if (tokenPaid == SMART_CONTRACT_NATIVE_COIN_ADDRESS) {
                sendCheckAllowanceChannel(true, token, totalEvm)
                return@launch
            }

            val allowance = getTokenAllowanceUseCase(
                blockchainType = blockchainType,
                id = currentId.getAndIncrement(),
                owner = Address(getAccounts()?.bip44Address ?: ""),
                spender = getCryptoContractAddressForPayment(),
                tokenAddress = Address(tokenPaid)
            )
            println("allowance: $allowance")
            println("totalWeiBigDecimal: $totalWeiBigDecimal")

            if (allowance < totalWeiBigDecimal) {
                sendCheckAllowanceChannel(false, token, totalEvm)
                return@launch
            } else {
                sendCheckAllowanceChannel(true, token, totalEvm)
            }
        }
    }

    private fun sendCheckAllowanceChannel(
        isEnoughAllowance: Boolean,
        token: TokenBalanceModel,
        totalEvm: BigDecimal
    ) {
        screenModelScope.launch {
            println("=== send check allowance channel ===")
            checkAllowanceChannel.send(
                CheckAllowanceChannelData(
                    isEnoughAllowance = isEnoughAllowance,
                    token = token,
                    tokenPaid = totalEvm,
                )
            )
        }
    }

    private fun getTokenPaid(token: String): String {
        return if (token == WALLET_NATIVE_COIN_ADDRESS) {
            SMART_CONTRACT_NATIVE_COIN_ADDRESS
        } else {
            token
        }
    }

    fun reCalculateTotalEvm(totalEos: BigDecimal, currentAccount: TokenBalanceModel) {
        screenModelScope.launch {
            val result = calculateTotalEvm(totalEos, currentAccount)
            if (result != null) {
                _totalEvmSharedFlow.emit(result)
            } else {
                _uiState.value = CreateAccountByEvmUiState.Error(
                    WrappedStringResource.StringRes(
                        MR.strings.error_pay_with_crypto_calculate_evm_failed
                    )
                )
            }
        }
    }

    private suspend fun calculateTotalEvm(
        totalEos: BigDecimal, currentAccount: TokenBalanceModel
    ): BigDecimal? {
        val evmTokenPaidPrice = currentAccount.currentPrice ?: run {
            return null
        }

        val eosNativeCoin = getNativeCoinUseCase(accountBlockchainType.uid)
        val eosNativeTokenPrice = fetchTokenPriceUseCase(
            forceReload = false, tokenUid = eosNativeCoin.coinUid, sparkline = true
        )?.currentPrice ?: run {
            return null
        }

        val totalUsd = eosNativeTokenPrice.toBigDecimal().times(totalEos)
        println("Total usd need to pay: $totalUsd")
        println("Evm token paid price: $evmTokenPaidPrice")
        val totalEvm = totalUsd.divide(evmTokenPaidPrice.toBigDecimal(), calculatingDecimalMode)
        println("Total evm need to pay: $totalEvm")
        return totalEvm
    }

    private fun calculateTotalEos(ramEos: BigDecimal): BigDecimal {
        val totalEos =
            ramEos + MINIMUM_CPU_CREATE_EOS_ACCOUNT + MINIMUM_NET_CREATE_EOS_ACCOUNT + SERVICE_FEE_CREATE_EOS_ACCOUNT
        println("Total eos: $totalEos")
        return totalEos
    }

    private suspend fun calculateRamEos(ram: BigDecimal): BigDecimal {
        val ramMarketData = getRamPriceUseCase(accountBlockchainType, true)
        println("ram: ${ramMarketData?.price}")
        val ramPrice = ramMarketData?.price ?: BigDecimal.ZERO
        println("Ram price: $ramPrice")
        return ram.div(1024).times(ramPrice)
    }

    private suspend fun getAccounts(): AccountBlockchainModel? {
        val listAccount = getWalletAccountsUseCase(
            filterHiddenAccounts = true,
            walletId = getSelectedWalletUseCase()?.id ?: ""
        ) ?: listOf()
        if (listAccount.isEmpty()) {
            return null
        }
        return listAccount.first()
    }

    fun changeToOriginalNetwork() {
        screenModelScope.launch {
            val isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()

            val blockchainNetworkData =
                BlockchainNetworkData.getBlockchainByUid(accountBlockchainType.uid, isDevelopmentEnvironment)
                    ?: run {
                        _uiState.value =
                            CreateAccountByEvmUiState.Error(
                                WrappedStringResource.StringRes(
                                    MR.strings.error_pay_with_crypto_change_back_to_original_network_failed
                                )
                            )
                        return@launch
                    }
            saveSelectedNetworkUseCase(blockchainNetworkData)
            println("==== Change to ${blockchainNetworkData.blockchainType.name} successfully ====")
        }
    }

    private fun getCryptoContractAddressForPayment(): Address {
        return getCryptoPaymentContractAddressUseCase.invoke(Chain.fromBlockchainType(blockchainType))
    }

    companion object {
        private val MINIMUM_CPU_CREATE_EOS_ACCOUNT = BigDecimal.fromDouble(0.2)
        private val MINIMUM_NET_CREATE_EOS_ACCOUNT = BigDecimal.fromDouble(0.2)
        private val SERVICE_FEE_CREATE_EOS_ACCOUNT = BigDecimal.fromDouble(0.1)
        private val MINIMUM_RAM_CREATE_EOS_ACCOUNT = BigDecimal.fromInt(1600)
    }
}

sealed class CreateAccountByEvmUiState {
    data object Loading : CreateAccountByEvmUiState()
    data class Success(val uiModel: UiModel) : CreateAccountByEvmUiState()
    data class Error(val message: WrappedStringResource) : CreateAccountByEvmUiState()
    data object AccountCreatingSuccessfully : CreateAccountByEvmUiState()
}

data class UiModel(
    val cpu: BigDecimal,
    val net: BigDecimal,
    val ram: BigDecimal,
    val serviceFee: BigDecimal,
    val totalEos: BigDecimal,
    val currentAccount: TokenBalanceModel,
    val accountSupportedList: List<TokenBalanceModel>
)

data class CheckAllowanceChannelData (
    var isEnoughAllowance: Boolean,
    val token: TokenBalanceModel,
    val tokenPaid: BigDecimal,
)