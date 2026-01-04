package com.mangala.wallet.features.send_base.step3

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.domain.account.usecases.GetAccountBalanceUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.token.GetAntelopeAccountCryptoBalanceUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetAccountBalancesInBitcoinAccountUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetSelectedWalletBitcoinAccountsUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.utils.BitcoinConstants
import com.mangala.wallet.features.chains.bitcoin.domain.utils.btcToSatoshis
import com.mangala.wallet.features.chains.bitcoin.domain.utils.satoshisToBtc
import com.mangala.wallet.features.chains.evmcompatible.core.amountToBigInt
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.CalBalance
import com.mangala.wallet.utils.formatAmountInput
import com.mangala.wallet.utils.toBigDecimalOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Step3SelectAmountScreenModel(
    private val accountId: String,
    private val contactId: Long?,
    private val recipientAddress: String?,
    private val blockchainUid: String?,
    private val initialAmount: String? = null, // amount passed in from scan QR code
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val getContactUseCase: GetContactByIdUseCase,
    // EVM
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase,
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase,
    // Antelope
    private val getAntelopeAccountsUseCase: GetAccountsUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    private val getAntelopeAccountCryptoBalanceUseCase: GetAntelopeAccountCryptoBalanceUseCase,
    // Bitcoin
    private val getSelectedWalletBitcoinAccountsUseCase: GetSelectedWalletBitcoinAccountsUseCase,
    private val getAccountBalancesInBitcoinAccountUseCase: GetAccountBalancesInBitcoinAccountUseCase
) : BaseScreenModel() {

    private val blockchainType = BlockchainType.fromUid(blockchainUid.orEmpty())
    private val networkType = blockchainType.networkType

    private val _uiState: MutableStateFlow<Step3SelectAmountScreenUiState> = MutableStateFlow(
        Step3SelectAmountScreenUiState.SelectAccount(
            accounts = emptyList(),
            accountsFilter = "",
            selectedAccount = null,
            recipientAddress = recipientAddress,
            networkType = networkType
        )
    )
    val uiState: StateFlow<Step3SelectAmountScreenUiState> = _uiState.asStateFlow()

    private val _sendTokenAmountState: MutableStateFlow<SendTokenAmountState?> =
        MutableStateFlow(null)
    val sendTokenAmountState: StateFlow<SendTokenAmountState?> = _sendTokenAmountState.asStateFlow()

    val selectedAmount = mutableStateOf<String?>(null)

    var selectedContact: ContactEntity? = null
        private set

    init {
        getAccounts()
        // TODO: Handle case scan token has initial amount
        onAmountChange(initialAmount.orEmpty())
        if (contactId != null) {
            screenModelScope.launch {
                getContactById(contactId)
            }
        }
    }

    fun onUpdateTokenQuery(query: String) {
        _uiState.update {
            (it as? Step3SelectAmountScreenUiState.SelectToken)?.copy(tokenInput = query) ?: it
        }
    }

    fun filterAccounts(query: String) {
        _uiState.update {
            (it as? Step3SelectAmountScreenUiState.SelectAccount)?.copy(accountsFilter = query) ?: it
        }
    }

    fun onSelectAccount(accountId: String) {
        getAccountInfo(accountId)
    }

    fun onAmountChange(amount: String) {
        val currentState = _uiState.value as? Step3SelectAmountScreenUiState.SelectAmount
        val maxDecimals = currentState?.selectToken?.selectedToken?.decimals ?: return

        selectedAmount.value = amount.formatAmountInput(
            oldValue = selectedAmount.value.orEmpty(),
            maxDecimals = maxDecimals.toInt()
        )
    }

    fun onResetToSelectAccount() {
        _uiState.update {
            val selectAccountState =
                (it as? Step3SelectAmountScreenUiState.SelectToken)?.selectAccount
                    ?: (it as? Step3SelectAmountScreenUiState.SelectAmount)?.selectToken?.selectAccount
                    ?: (it as? Step3SelectAmountScreenUiState.SelectAccount)
                    ?: return

            selectAccountState.copy(
                accountsFilter = "",
                selectedAccount = null,
                isTransferToSelf = false
            )
        }
    }

    fun onResetToSelectToken() {
        _uiState.update {
            val selectTokenState = (it as? Step3SelectAmountScreenUiState.SelectAmount)?.selectToken
                ?: (it as? Step3SelectAmountScreenUiState.SelectToken)
                ?: return

            selectTokenState.copy(tokenInput = "")
        }
    }

    fun onResetToSelectAmount() {
        _uiState.update {
            val selectAmountState = (it as? Step3SelectAmountScreenUiState.EnterMemo)?.selectAmount
                ?: (it as? Step3SelectAmountScreenUiState.SelectAmount)
                ?: return

            selectAmountState.copy()
        }
    }

    private fun getAccounts() {
        screenModelScope.launch {
            val wrappedAccounts = when (networkType) {
                NetworkType.EVM -> {
                    val accounts = getSelectedWalletAccountsUseCase()
                    accounts?.map { it.wrap() }.orEmpty()
                }

                NetworkType.ANTELOPE -> {
                    val accounts = getAntelopeAccountsUseCase(blockchainType)
                    accounts.map { it.wrap() }
                }

                NetworkType.BITCOIN -> {
                    BlockchainNetworkData.getBlockchainByType(blockchainType, includeDebugNetworks = true)?.let { blockchainNetworkData ->
                        val accounts = getSelectedWalletBitcoinAccountsUseCase.getWithoutBalance(blockchainNetworkData)
                        accounts.map { it.wrap() }
                    } ?: emptyList()
                }

                else -> emptyList()
            }
            
            _uiState.update {
                (it as? Step3SelectAmountScreenUiState.SelectAccount)?.copy(accounts = wrappedAccounts) ?: it
            }
            
            if (wrappedAccounts.size == 1) {
                onSelectAccount(wrappedAccounts.first().accountId)
            }
        }
    }

    private fun getAccountInfo(accountId: String = this.accountId) {
        val allAccounts = (_uiState.value as? Step3SelectAmountScreenUiState.SelectAccount)?.accounts ?: return
        val account = allAccounts.find { it.accountId == accountId } ?: return

        if (blockchainUid.isNullOrEmpty()) return

        screenModelScope.launch {
            val currencyCode = getCurrentCurrencyCodeUseCase()
            val currencySymbol = Currency.valueOf(currencyCode).symbol
            val isTransferToSelf = (contactId?.let { selectedContact?.address } ?: recipientAddress) == account.formattedAddress
            val transferToSelfAllowed = networkType != NetworkType.ANTELOPE

            if (isTransferToSelf && !transferToSelfAllowed) {
                _uiState.update {
                    (it as? Step3SelectAmountScreenUiState.SelectAccount)?.copy(isTransferToSelf = true) ?: it
                }
                return@launch
            }

            _uiState.update {
                (it as? Step3SelectAmountScreenUiState.SelectAccount)?.let {
                    Step3SelectAmountScreenUiState.SelectToken(
                        allTokens = emptyList(),
                        selectAccount = it.copy(
                            accountsFilter = account.accountName,
                            selectedAccount = account
                        ),
                        tokenInput = "",
                        isLoading = true,
                        selectedToken = null,
                        currencySymbol = currencySymbol
                    )
                } ?: it
            }

            when (account) {
                is SelectAmountAccountWrapper.Evm -> {
                    getAccountBalanceUseCase.invokeFlow(
                        forceReload = false,
                        address = account.account.bip44Address, // TODO: Support for different address types
                        accountId = accountId,
                        blockchainType = BlockchainType.fromUid(blockchainUid),
                        sparkline = false
                    ).collectLatest { result ->
                        _uiState.update {
                            // Handle case can't find token in balance response anymore -> prompt user to select again
                            when (it) {
                                is Step3SelectAmountScreenUiState.SelectToken -> {
                                    if (it.selectedToken != null) {
                                        it.copy(selectedToken = null)
                                    } else it
                                }
                                is Step3SelectAmountScreenUiState.SelectAmount -> {
                                    Step3SelectAmountScreenUiState.SelectToken(
                                        currencySymbol = currencySymbol,
                                        selectAccount = it.selectToken.selectAccount,
                                        allTokens = it.selectToken.allTokens,
                                        tokenInput = it.selectToken.tokenInput,
                                        isLoading = it.selectToken.isLoading,
                                        selectedToken = null,
                                    )
                                }
                                else -> it
                            }
                        }
                        _uiState.update {
                            (it as? Step3SelectAmountScreenUiState.SelectToken)?.copy(
                                allTokens = result.map { it.wrapEvm() },
                                isLoading = false
                            ) ?: it
                        }
                    }
                }

                is SelectAmountAccountWrapper.Antelope -> {
                    val nativeCoin = getNativeCoinUseCase(blockchainUid)

                    val nativeCoinPriceAsync = async {
                        fetchTokenPriceUseCase(
                            forceReload = false,
                            tokenUid = nativeCoin.coinUid,
                            sparkline = false
                        )
                    }
                    val antelopeAccountCryptoBalanceAsync = async {
                        getAntelopeAccountCryptoBalanceUseCase(accountId, blockchainType, forceRefresh = false)
                    }

                    val nativeCoinPrice = nativeCoinPriceAsync.await()
                    val allBalances = antelopeAccountCryptoBalanceAsync.await().getOrNull() ?: emptyList()

                    _uiState.update {
                        (it as? Step3SelectAmountScreenUiState.SelectToken)?.copy(
                            allTokens = allBalances.map {
                                it.wrap(
                                    nativeCoinPrice?.currentPrice?.toBigDecimalOrNull()
                                        ?: BigDecimal.ZERO,
                                    blockchainType
                                )
                            },
                            isLoading = false
                        ) ?: it
                    }
                }

                is SelectAmountAccountWrapper.Bitcoin -> {
                    BlockchainNetworkData.getBlockchainByType(blockchainType, includeDebugNetworks = true)?.let { blockchainNetworkData ->
                        getAccountBalancesInBitcoinAccountUseCase(
                            accountId = accountId,
                            blockchainNetworkData = blockchainNetworkData,
                            forceReload = false
                        ).collectLatest { bitcoinToken ->
                            _uiState.update {
                                val balance = bitcoinToken.data?.balanceInSatoshis?.data?.wrapBitcoin()
                                (it as? Step3SelectAmountScreenUiState.SelectToken)?.copy(
                                    allTokens = balance?.let { listOf(it) } ?: emptyList(),
                                    isLoading = bitcoinToken.isLoading()
                                ) ?: it
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getContactById(id: Long) {
        screenModelScope.launch {
            selectedContact = getContactUseCase(id)
        }
    }

    fun onEnterAmountDone() {
        if (networkType == NetworkType.ANTELOPE) {
            _uiState.update {
                val currentState = it as? Step3SelectAmountScreenUiState.SelectAmount ?: return@update it
                Step3SelectAmountScreenUiState.EnterMemo(
                    memo = "",
                    selectAmount = currentState
                )
            }

            return
        }

        clickContinue()
    }

    fun clickContinue() {
        when (networkType) {
            NetworkType.EVM -> {
                val uiState = _uiState.value as? Step3SelectAmountScreenUiState.SelectAmount ?: return

                val account = uiState.selectToken.selectAccount.selectedAccount
                val amount = selectedAmount.value
                val token = uiState.selectToken.selectedToken

                if (token == null || amount.isNullOrEmpty() || account == null) return

                _sendTokenAmountState.value = SendTokenAmountState(
                    contactId = contactId,
                    recipientAddress = if (contactId != null) selectedContact?.address else recipientAddress,
                    blockchainUid = blockchainUid,
                    tokenId = token.tokenId,
                    amount = amount,
                    accountId = account.accountId,
                    memo = ""
                )
            }
            NetworkType.ANTELOPE -> {
                val uiState = _uiState.value as? Step3SelectAmountScreenUiState.EnterMemo ?: return

                val account = uiState.selectAmount.selectToken.selectAccount.selectedAccount
                val amount = selectedAmount.value
                val token = uiState.selectAmount.selectToken.selectedToken

                if (token == null || amount.isNullOrEmpty() || account == null) return

                _sendTokenAmountState.value = SendTokenAmountState(
                    contactId = contactId,
                    recipientAddress = if (contactId != null) selectedContact?.address else recipientAddress,
                    blockchainUid = blockchainUid,
                    tokenId = token.tokenId,
                    amount = amount,
                    accountId = account.accountId,
                    memo = uiState.memo
                )
            }
            NetworkType.BITCOIN -> {
                val uiState = _uiState.value as? Step3SelectAmountScreenUiState.SelectAmount ?: return
                
                val account = uiState.selectToken.selectAccount.selectedAccount
                val amount = selectedAmount.value
                val token = uiState.selectToken.selectedToken

                if (token == null || amount.isNullOrEmpty() || account == null) return

                _sendTokenAmountState.value = SendTokenAmountState(
                    contactId = contactId,
                    recipientAddress = if (contactId != null) selectedContact?.address else recipientAddress,
                    blockchainUid = blockchainUid,
                    tokenId = token.tokenId,
                    amount = amount,
                    accountId = account.accountId,
                    memo = ""
                )
            }
            else -> TODO()
        }
    }

    fun clickMaxToken() {
        val currentState = _uiState.value as? Step3SelectAmountScreenUiState.SelectAmount ?: return
        val selectedToken = currentState.selectToken.selectedToken ?: return

        selectedAmount.value = when (networkType) {
            NetworkType.EVM -> {
                CalBalance.formatBalance(
                    selectedToken.balance,
                    selectedToken.decimals,
                    minOf(selectedToken.decimals, 5).toInt()
                )
            }
            NetworkType.ANTELOPE -> {
                selectedToken.balance
            }
            NetworkType.BITCOIN -> {
                BigDecimal.parseString(selectedToken.balance).satoshisToBtc().toStringExpanded()
            }
            else -> TODO()
        }
        checkValidAmount()
    }

    fun checkValidAmount() {
        val currentState = _uiState.value as? Step3SelectAmountScreenUiState.SelectAmount ?: return

        val selectedToken = currentState.selectToken.selectedToken
        val decimal = selectedToken?.decimals
        if (selectedAmount.value == null || selectedAmount.value.isNullOrEmpty() || selectedToken == null || selectedToken.balance.isEmpty()) {
            _uiState.update {
                currentState.copy(isInsufficientBalance = false)
            }
        } else {
            val validationError = validateAmount(networkType, selectedAmount.value)
            
            val isInsufficientBalance = when (networkType) {
                NetworkType.EVM -> {
                    val balance = BigInteger.parseString(selectedToken.balance)
                    val amount = selectedAmount.value!!.amountToBigInt(decimal ?: 0)
                    balance >= amount
                }
                NetworkType.ANTELOPE -> {
                    val balance = BigDecimal.parseString(selectedToken.balance)
                    val amount = selectedAmount.value!!.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    balance >= amount && amount > BigDecimal.ZERO
                }
                NetworkType.BITCOIN -> {
                    val balanceInSats = BigDecimal.parseString(selectedToken.balance)
                    val amountInSats = selectedAmount.value!!.toBigDecimalOrNull()?.btcToSatoshis() ?: BigDecimal.ZERO
                    balanceInSats >= amountInSats && amountInSats > BigDecimal.ZERO
                }
                else -> TODO()
            }
            
            _uiState.update {
                currentState.copy(
                    isInsufficientBalance = isInsufficientBalance,
                    amountValidationError = validationError
                )
            }
        }
    }

    fun clearState() {
        _sendTokenAmountState.value = null
    }

    fun onSelectToken(token: SelectAmountTokenWrapper) {
        if (initialAmount == null) selectedAmount.value = "" // prevents resetting of amount for amount passed in from QR flow
        _uiState.update {
            val oldState = (it as? Step3SelectAmountScreenUiState.SelectToken) ?: return@update it

            Step3SelectAmountScreenUiState.SelectAmount(
                selectToken = oldState.copy(tokenInput = token.name, selectedToken = token),
                networkType,
                isInsufficientBalance = false
            )
        }
    }

    fun onMemoChange(newMemo: String) {
        _uiState.update {
            (it as? Step3SelectAmountScreenUiState.EnterMemo)?.copy(memo = newMemo) ?: it
        }
    }

    private fun validateAmount(networkType: NetworkType, amount: String?): AmountValidationError? {
        if (amount.isNullOrEmpty()) return null

        val amountValue = amount.toBigDecimalOrNull() ?: return null
        if (amountValue <= BigDecimal.ZERO) return null

        return when (networkType) {
            NetworkType.BITCOIN -> {
                val satoshis = amountValue.btcToSatoshis()

                if (satoshis < BigDecimal.fromLong(BitcoinConstants.DUST_SATS_AMOUNT.toLong())) {
                    AmountValidationError.DUST_AMOUNT
                } else null
            }
            else -> null
        }
    }
}

data class SendTokenAmountState(
    val contactId: Long?,
    val recipientAddress: String?,
    val blockchainUid: String?,
    val tokenId: String,
    val amount: String,
    val accountId: String,
    val memo: String?
)
