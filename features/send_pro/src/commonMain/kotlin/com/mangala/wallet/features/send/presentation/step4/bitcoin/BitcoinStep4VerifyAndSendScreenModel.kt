package com.mangala.wallet.features.send.presentation.step4.bitcoin

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.transaction.fee.TransactionFeeType
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.model.fee.FeeRatePriority
import com.mangala.wallet.features.chains.bitcoin.domain.model.fee.FeeratePerVbyte
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetAccountBalancesInBitcoinAccountUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetBitcoinAccountUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.fee.GetBitcoinFeeRatesUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.transaction.SendBitcoinTransactionUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.utxo.GetBitcoinWalletUtxosUseCase
import com.mangala.wallet.features.chains.ui.BitcoinFeeOptionUiModel
import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.Constants
import com.mangala.wallet.utils.ext.formatFiat
import fr.acinq.bitcoin.Satoshi
import fr.acinq.bitcoin.sat
import fr.acinq.lightning.blockchain.fee.FeeratePerByte
import fr.acinq.lightning.blockchain.fee.FeeratePerKw
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class BitcoinStep4VerifyAndSendScreenModel(
    private val contactId: Long?,
    private val blockchainUid: String,
    private val tokenId: String,
    private val recipientAddress: String,
    private val amount: String,
    private val accountId: String,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getContactByIdUseCase: GetContactByIdUseCase,
    private val getBitcoinAccountUseCase: GetBitcoinAccountUseCase,
    private val getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val getBitcoinWalletUtxosUseCase: GetBitcoinWalletUtxosUseCase,
    private val sendBitcoinTransactionUseCase: SendBitcoinTransactionUseCase,
    private val getBitcoinFeeRatesUseCase: GetBitcoinFeeRatesUseCase,
    private val getAccountBalancesInBitcoinAccountUseCase: GetAccountBalancesInBitcoinAccountUseCase
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow<BitcoinStep4VerifyAndSendScreenUiState>(BitcoinStep4VerifyAndSendScreenUiState.Loading)
    val uiState: StateFlow<BitcoinStep4VerifyAndSendScreenUiState> = _uiState.asStateFlow()

    private var txHash: String? = null
    private lateinit var blockchainType: BlockchainType
    private lateinit var currencySymbol: String
    private lateinit var token: TokenBalanceEntity
    private var refreshJob: Job? = null

    init {
        blockchainType = BlockchainType.fromUid(blockchainUid)
        
        screenModelScope.launch {
            loadInitialData()
        }
    }

    private suspend fun loadInitialData() {
        try {
            val blockchainType = BlockchainType.fromUid(blockchainUid)
            val blockchainNetworkData = BlockchainNetworkData.getBlockchainByUid(blockchainUid, true)
                ?: BlockchainNetworkData.getBlockchainByType(blockchainType, true)
                ?: throw IllegalStateException("Blockchain not found")
            
            getAccountBalancesInBitcoinAccountUseCase(
                accountId = accountId,
                forceReload = false,
                blockchainNetworkData = blockchainNetworkData
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val bitcoinAccountWithBalance = resource.data
                        val balanceInSatoshis = bitcoinAccountWithBalance?.balanceInSatoshis
                        
                        if (balanceInSatoshis is Resource.Success && balanceInSatoshis.data != null) {
                            val tokenBalanceModel = balanceInSatoshis.data
                            
                            val token = TokenBalanceEntity(
                                tokenId = tokenId.toLong(),
                                accountId = accountId,
                                blockchainUid = blockchainUid,
                                balance = tokenBalanceModel?.balance ?: "0",
                                balance24h = tokenBalanceModel?.balance24h ?: "0",
                                balanceLocked = "0",
                                orderNumber = 0,
                                contractDecimals = 8, // Bitcoin uses 8 decimals (satoshis)
                                contractName = "Bitcoin",
                                contractSymbol = "BTC",
                                contractAddress = "",
                                logoUrl = "",
                                lastUpdated = Clock.System.now().toEpochMilliseconds()
                            )
                            
                            this.token = token
                            
                            val contact = contactId?.let { getContactById(it) }
                            
                            val currencyCode = getCurrentCurrencyCodeUseCase()
                            val currentAccount = getAccountByIdUseCase.invokeSuspend(accountId)
                            currencySymbol = Currency.valueOf(currencyCode).symbol
                            
                            _uiState.update {
                                BitcoinStep4VerifyAndSendScreenUiState.Data(
                                    contact = contact,
                                    recipientAddress = recipientAddress,
                                    account = currentAccount,
                                    selectedToken = token,
                                    txHash = null,
                                    transactionFeeOptions = emptyList(),
                                    selectedTransactionFee = null,
                                    tokenFiatValue = "",
                                    totalTransactionFiatValue = ""
                                )
                            }
                            
                            calculateTransactionFee()
                        } else {
                            _uiState.update { BitcoinStep4VerifyAndSendScreenUiState.Error }
                        }
                    }
                    
                    is Resource.Error -> {
                        _uiState.update { BitcoinStep4VerifyAndSendScreenUiState.Error }
                    }
                    
                    is Resource.Loading -> {
                        _uiState.update { BitcoinStep4VerifyAndSendScreenUiState.Loading }
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update { BitcoinStep4VerifyAndSendScreenUiState.Error }
        }
    }

    private suspend fun getContactById(id: Long): ContactEntity? {
        return getContactByIdUseCase(id)
    }

    private fun calculateTransactionFee() {
        refreshJob?.cancel()
        refreshJob = screenModelScope.launch {
            while (isActive) {
                try {
                    getBitcoinWalletUtxosUseCase(accountId, blockchainType, confirmedOnly = true).collect { utxoList ->
                        if (utxoList.isNotEmpty()) {
                            val feeOptions = createTransactionFeeOptions(
                                utxoList.size,
                                2
                            )

                            val tokenFiatPrice = getTokenFiatPrice(token)
                            val amountInSats = try {
                                (amount.toBigDecimal() * BigDecimal.TEN.pow(8)).longValue(
                                    exactRequired = false
                                )
                            } catch (e: Exception) {
                                0L
                            }

                            val rawTokenFiatValue =
                                tokenFiatPrice?.multiply(BigDecimal.parseString(amount))
                            val tokenFiatValue =
                                rawTokenFiatValue?.formatFiat(currencySymbol).orEmpty()

                            val defaultFeeOption =
                                feeOptions.firstOrNull { it.transactionFee.transactionFeeType == TransactionFeeType.REGULAR }

                            _uiState.update { currentState ->
                                if (currentState is BitcoinStep4VerifyAndSendScreenUiState.Data) {
                                    val totalFiatValue =
                                        if (defaultFeeOption != null && rawTokenFiatValue != null) {
                                            // Add fee to token value for total
                                            val totalValue =
                                                rawTokenFiatValue.plus(defaultFeeOption.transactionFeeFiatValue)
                                            totalValue.formatFiat(currencySymbol)
                                        } else {
                                            tokenFiatValue
                                        }

                                    currentState.copy(
                                        transactionFeeOptions = feeOptions,
                                        selectedTransactionFee = defaultFeeOption,
                                        tokenFiatValue = tokenFiatValue,
                                        totalTransactionFiatValue = totalFiatValue
                                    )
                                } else {
                                    currentState
                                }
                            }
                        }
                    }

                    // Refresh fee rates periodically
                    delay(Constants.TRANSACTION_FEE_REFRESH_INTERVAL)
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    private suspend fun createTransactionFeeOptions(numInputs: Int, numOutputs: Int): List<BitcoinFeeOptionUiModel> {
        val estimatedSize = estimateTransactionSize(numInputs, numOutputs)
        
        val feeRatesResult = getBitcoinFeeRatesUseCase(blockchainType).first()

        val feeRates = feeRatesResult.getOrNull() ?: mapOf(
            FeeRatePriority.FASTEST to FeeratePerVbyte.PRIORITY,  // 20 sat/vB
            FeeRatePriority.MEDIUM to FeeratePerVbyte.NORMAL,     // 10 sat/vB
            FeeRatePriority.SLOW to FeeratePerVbyte.ECONOMIC      // 5 sat/vB
        )
        
        // Ensure we're using rates that will meet minimum relay fee requirements
        val fastFeeRate = feeRates[FeeRatePriority.FASTEST] ?: FeeratePerVbyte.PRIORITY
        val mediumFeeRate = feeRates[FeeRatePriority.MEDIUM] ?: FeeratePerVbyte.NORMAL
        val slowFeeRate = feeRates[FeeRatePriority.SLOW] ?: FeeratePerVbyte.ECONOMIC
        
        return listOf(
            createFeeOption(
                id = 1,
                label = "Fast",
                description = "Estimated confirmation: 10-20 minutes",
                feeRate = fastFeeRate,
                estimatedSize = estimatedSize,
                gasOption = EvmFeeOptionUiModel.GasOption.HIGH,
                networkCurrencySymbol = "BTC",
                estimatedTimeInSeconds = 900 // ~15 minutes
            ),
            createFeeOption(
                id = 2,
                label = "Medium",
                description = "Estimated confirmation: 20-60 minutes",
                feeRate = mediumFeeRate,
                estimatedSize = estimatedSize,
                gasOption = EvmFeeOptionUiModel.GasOption.MEDIUM,
                networkCurrencySymbol = "BTC",
                estimatedTimeInSeconds = 1800 // ~30 minutes
            ),
            createFeeOption(
                id = 3,
                label = "Slow", 
                description = "Estimated confirmation: 1-3 hours",
                feeRate = slowFeeRate,
                estimatedSize = estimatedSize,
                gasOption = EvmFeeOptionUiModel.GasOption.LOW,
                networkCurrencySymbol = "BTC",
                estimatedTimeInSeconds = 7200 // ~2 hours
            )
        )
    }

    private fun createFeeOption(
        id: Int,
        label: String,
        description: String,
        feeRate: FeeratePerVbyte,
        estimatedSize: Int,
        gasOption: EvmFeeOptionUiModel.GasOption,
        networkCurrencySymbol: String,
        estimatedTimeInSeconds: Int
    ): BitcoinFeeOptionUiModel {
        // Calculate fee based on estimated size and fee rate
        val feeSatoshis = feeRate.toFeeSatoshi(estimatedSize.toLong())
        
        // Ensure fee is at least the minimum required fee (1000 satoshis is a safe minimum)
        val adjustedFeeSatoshis = maxOf(feeSatoshis.sat, 1000)
        
        // Convert to BTC for display
        val feeInBtc = adjustedFeeSatoshis.toDouble() / 100_000_000.0
        
        // Get current BTC price for fiat conversion if available, otherwise use placeholder
        // TODO: Get actual BTC price for fiat conversion
        val btcPriceInFiat = 50000.0 // Placeholder
        val feeInFiat = feeInBtc * btcPriceInFiat
        
        // Update description to include the fee rate
        val updatedDescription = "$description (${feeRate.sat} sat/vB)"
        
        return BitcoinFeeOptionUiModel(
            id = id,
            label = label,
            description = updatedDescription,
            gasOption = gasOption,
            feeSatPerVByte = feeRate.sat,
            networkCurrencySymbol = networkCurrencySymbol,
            feeAmount = feeInBtc.toString(),
            feeAmountInFiat = feeInFiat.toString(),
            estimatedTimeInSeconds = estimatedTimeInSeconds,
            isSelected = gasOption == EvmFeeOptionUiModel.GasOption.MEDIUM,
            fiatCurrencySymbol = currencySymbol
        )
    }

    /**
     * Estimate transaction size in virtual bytes
     * This is a more accurate estimation for SegWit transactions
     */
    private fun estimateTransactionSize(numInputs: Int, numOutputs: Int): Int {
        // More accurate formula for SegWit transaction size
        val baseSize = 11    // Version (4) + locktime (4) + segwit marker and flag (2) + varint (1)
        val inputSize = 68   // P2WPKH input virtual size (including witness data)
        val outputSize = 31  // P2WPKH output size
        
        // Add a small buffer to ensure we don't underestimate
        val estimatedSize = baseSize + (numInputs * inputSize) + (numOutputs * outputSize)
        return (estimatedSize * 1.05).toInt() // Add 5% buffer
    }

    private suspend fun getTokenFiatPrice(crypto: TokenBalanceEntity): BigDecimal? {
        val nativeCoin = getNativeCoinUseCase(blockchainType.uid)
        
        nativeCoin.let {
            val tokenPrice = fetchTokenPriceUseCase(
                false,
                mapOf(it.coinUid to crypto),
                false
            ).firstOrNull() ?: return null
            
            val currentPrice = tokenPrice.currentPrice ?: "0"
            return BigDecimal.parseString(currentPrice)
        }
    }

    fun onTransactionFeeSelected(feeOption: BitcoinFeeOptionUiModel) {

        _uiState.update { currentState ->
            if (currentState is BitcoinStep4VerifyAndSendScreenUiState.Data) {
                // Get current token fiat value
                val tokenFiatValue = currentState.tokenFiatValue
                
                // Update total transaction value with new fee
                val totalFiatValue = if (tokenFiatValue.isNotEmpty()) {
                    // This is a simplified approach, in a real implementation you'd calculate
                    // the actual fee impact on the total transaction value
                    val feeAmount = feeOption.feeAmountInFiat
                    val tokenValue = currentState.tokenFiatValue.replace(currencySymbol, "").toDoubleOrNull() ?: 0.0
                    val feeValue = feeAmount.replace("$", "").toDoubleOrNull() ?: 0.0
                    
                    currencySymbol + (tokenValue + feeValue)
                } else {
                    ""
                }
                
                currentState.copy(
                    selectedTransactionFee = feeOption,
                    totalTransactionFiatValue = totalFiatValue,
                    transactionFeeOptions = currentState.transactionFeeOptions.map { option ->
                        option.copy(isSelected = option.id == feeOption.id)
                    }
                )
            } else {
                currentState
            }
        }
    }

    fun onAuthenticationSuccess() {
        val currentState = _uiState.value as? BitcoinStep4VerifyAndSendScreenUiState.Data ?: return
        val selectedFee = currentState.selectedTransactionFee ?: return
        
        screenModelScope.launch {
            try {
                val amountInSatoshis = convertBtcToSatoshis(amount)
                
                val feeRate = FeeratePerKw(FeeratePerByte(selectedFee.feeSatPerVByte.sat()))

                val currentAccount = getBitcoinAccountUseCase(blockchainType, accountId) ?: return@launch
                
                val utxoList = getBitcoinWalletUtxosUseCase.invokeSuspend(accountId, blockchainType, confirmedOnly = true)
                
                if (utxoList.isNotEmpty()) {
                    val result = sendBitcoinTransactionUseCase(
                        accountId = accountId,
                        recipientAddress = recipientAddress,
                        amount = Satoshi(amountInSatoshis),
                        feeRate = feeRate,
                        utxos = utxoList,
                        changeAddress = currentAccount.bip84Address,
                        blockchainType = blockchainType
                    )
                    
                    result.fold(
                        { error ->
                            _uiState.update { currentState.copy(txHash = null) }
                        },
                        { txHashResult ->
                            txHash = txHashResult
                            _uiState.update { currentState.copy(txHash = txHashResult) }
                        }
                    )
                } else {
                    _uiState.update { currentState.copy(txHash = null) }
                }
            } catch (e: Exception) {
                _uiState.update { currentState.copy(txHash = null) }
            }
        }
    }

    private fun convertBtcToSatoshis(amount: String): Long {
        return try {
            val btcAmount = amount.toBigDecimal()
            val satoshis = btcAmount * BigDecimal.TEN.pow(8)
            satoshis.longValue(false)
        } catch (e: Exception) {
            0L
        }
    }

    fun getTxHash(): String? = txHash

    fun onConsumeTxHash() {
        txHash = null
        _uiState.update { (it as? BitcoinStep4VerifyAndSendScreenUiState.Data)?.copy(txHash = null) ?: it }
    }
}