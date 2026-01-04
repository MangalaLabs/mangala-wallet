package com.mangala.wallet.features.swap_base.presentation

import cafe.adriel.voyager.core.concurrent.AtomicInt32
import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.domain.account.usecases.GetAccountBalanceUseCase
import com.mangala.wallet.domain.coin.usecases.GetAllCoinUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.usecases.GetTokenByBlockchainUidUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletAccountsUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.model.coin.Coin
import com.mangala.wallet.model.token.TokenEntity
import com.mangala.wallet.model.token.domain.formattedBalanceForHuman
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.uniswap.TokenFactory
import com.mangala.wallet.uniswap.TradeError
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.Price
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.uniswap.domain.models.TradeOptions
import com.mangala.wallet.uniswap.domain.models.TradeType
import com.mangala.wallet.uniswap.domain.usecase.GetSwapTradeDataUseCase
import com.mangala.wallet.utils.ext.removeTrailingZeroes
import com.mangala.wallet.utils.toBigDecimalOrNull
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SwapTokenScreenModel(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getTokenByBlockchainUidUseCase: GetTokenByBlockchainUidUseCase,
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase,
    private val getWalletAccountsUseCase: GetWalletAccountsUseCase,
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val getSwapTradeDataUseCase: GetSwapTradeDataUseCase,
    private val getAllCoinUseCase: GetAllCoinUseCase
) : BaseScreenModel() {

    lateinit var selectedNetwork: BlockchainNetworkData
        private set

    val listDex = listOf(Dex.Biswap, Dex.PancakeSwap, Dex.UniSwap)
    private var job: Job? = null
    var tradeData: TradeData? = null
        private set
    private var tradeType: TradeType? = null // needed to fetch new TradeData when refresh
    private val currentId = AtomicInt32(Random.nextInt(100))

    private val _uiState = MutableStateFlow<SwapTokenScreenUiState>(SwapTokenScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _listToken = MutableStateFlow(listOf<TokenEntity>())
    private val listToken = _listToken.asStateFlow()

    private val _listCoin = MutableStateFlow(listOf<Coin>())
    private val listCoin = _listCoin.asStateFlow()

    private val _listAccount = MutableStateFlow(listOf<AccountBlockchainModel>())
    val listAccount = _listAccount.asStateFlow()

    private val _fromTokenValue = MutableStateFlow("")
    val fromTokenValue = _fromTokenValue.asStateFlow()

    private val _toTokenValue = MutableStateFlow("")
    val toTokenValue = _toTokenValue.asStateFlow()

    init {
        screenModelScope.launch {
            getSelectedNetwork()
            getSupportedToken()
            getAllCoins()
            getAccounts()
        }
    }

    fun onFromTokenValueChange(value: String) {
        _fromTokenValue.value = value
        val uiStateSuccess = uiState.value as? SwapTokenScreenUiState.Success ?: return
        job?.cancel()
        if (value.isEmpty()) {
            _toTokenValue.value = ""
            _uiState.value = SwapTokenScreenUiState.Success(
                uiStateSuccess.swapTokenScreenUiModel.copy(
                    price = "",
                    isInsufficientAmount = false
                )
            )
            return
        }

        val uiStateAfterCheckAmountIn =
            if (value.toBigDecimal() > uiStateSuccess.swapTokenScreenUiModel.selectedFromToken.balance.toBigDecimal())
                SwapTokenScreenUiState.Success(
                    uiStateSuccess.swapTokenScreenUiModel.copy(isInsufficientAmount = true)
                )
            else SwapTokenScreenUiState.Success(
                uiStateSuccess.swapTokenScreenUiModel.copy(isInsufficientAmount = false)
            )
        _uiState.value = uiStateAfterCheckAmountIn

        job = jobSycnSwapTradeDataExactIn(uiStateAfterCheckAmountIn)
    }

    fun onToTokenValueChange(value: String) {
        _toTokenValue.value = value
        val uiStateSuccess = uiState.value as? SwapTokenScreenUiState.Success ?: return
        job?.cancel()
        if (value.isEmpty()) {
            _fromTokenValue.value = ""
            _uiState.value = SwapTokenScreenUiState.Success(
                uiStateSuccess.swapTokenScreenUiModel.copy(
                    price = "",
                    isInsufficientAmount = false
                )
            )
            return
        }
        job = jobSyncSwapTradeDataExactOut(uiStateSuccess)
    }

    fun onSelectDex(dex: Dex) {
        val update = { uiState: SwapTokenScreenUiState ->
            (uiState as? SwapTokenScreenUiState.Success)?.let {
                val uiStateAfterUpdateSelectedDex = SwapTokenScreenUiState.Success(
                    it.swapTokenScreenUiModel.copy(selectedDex = dex)
                )

                job?.cancel()
                job = when {
                    fromTokenValue.value.isNotEmpty() -> jobSycnSwapTradeDataExactIn(
                        uiStateAfterUpdateSelectedDex
                    )

                    toTokenValue.value.isNotEmpty() -> jobSyncSwapTradeDataExactOut(
                        uiStateAfterUpdateSelectedDex
                    )

                    else -> null
                }

                uiStateAfterUpdateSelectedDex
            } ?: uiState
        }
        _uiState.value = update(uiState.value)
    }

    fun onSelectFromToken(selectedToken: SwapTokenUiModel.TokenUiModel) {
        val update = { uiState: SwapTokenScreenUiState ->
            (uiState as? SwapTokenScreenUiState.Success)?.let {
                val uiStateAfterUpdateSelectedToken =
                    if (selectedToken == it.swapTokenScreenUiModel.selectedToToken)
                        swapFromAndToTokenInUiModel(it)
                    else SwapTokenScreenUiState.Success(
                        it.swapTokenScreenUiModel.copy(selectedFromToken = selectedToken)
                    )
                job?.cancel()
                job = when {
                    fromTokenValue.value.isNotEmpty() -> jobSycnSwapTradeDataExactIn(
                        uiStateAfterUpdateSelectedToken
                    )

                    toTokenValue.value.isNotEmpty() -> jobSyncSwapTradeDataExactOut(
                        uiStateAfterUpdateSelectedToken
                    )

                    else -> null
                }

                uiStateAfterUpdateSelectedToken
            } ?: uiState
        }
        _uiState.value = update(uiState.value)

    }

    fun onSelectToToken(selectedToken: SwapTokenUiModel.TokenUiModel) {
        val update = { uiState: SwapTokenScreenUiState ->
            (uiState as? SwapTokenScreenUiState.Success)?.let {
                val uiStateAfterUpdateSelectedToken =
                    if (selectedToken == it.swapTokenScreenUiModel.selectedFromToken)
                        swapFromAndToTokenInUiModel(it)
                    else SwapTokenScreenUiState.Success(
                        it.swapTokenScreenUiModel.copy(selectedToToken = selectedToken)
                    )
                job?.cancel()
                job = when {
                    toTokenValue.value.isNotEmpty() -> jobSyncSwapTradeDataExactOut(
                        uiStateAfterUpdateSelectedToken
                    )

                    fromTokenValue.value.isNotEmpty() -> jobSycnSwapTradeDataExactIn(
                        uiStateAfterUpdateSelectedToken
                    )

                    else -> null
                }

                uiStateAfterUpdateSelectedToken
            } ?: uiState
        }
        _uiState.value = update(uiState.value)
    }

    fun reverseFromAndToToken() {
        val update = { uiState: SwapTokenScreenUiState ->
            (uiState as? SwapTokenScreenUiState.Success)?.let {
                val uiStateAfterUpdateSelectedToken = swapFromAndToTokenInUiModel(it)
                job?.cancel()
                job = when {
                    fromTokenValue.value.isNotEmpty() -> jobSycnSwapTradeDataExactIn(
                        uiStateAfterUpdateSelectedToken
                    )

                    toTokenValue.value.isNotEmpty() -> jobSyncSwapTradeDataExactOut(
                        uiStateAfterUpdateSelectedToken
                    )

                    else -> null
                }

                uiStateAfterUpdateSelectedToken
            } ?: uiState
        }
        _uiState.value = update(uiState.value)

    }

    private fun swapFromAndToTokenInUiModel(uiState: SwapTokenScreenUiState.Success): SwapTokenScreenUiState.Success {
        val selectedFromToken = uiState.swapTokenScreenUiModel.selectedFromToken
        val selectedToToken = uiState.swapTokenScreenUiModel.selectedToToken
        return SwapTokenScreenUiState.Success(
            uiState.swapTokenScreenUiModel.copy(
                selectedFromToken = selectedToToken,
                selectedToToken = selectedFromToken
            )
        )
    }

    fun onSelectAccount(account: AccountBlockchainModel) {
        _uiState.value = SwapTokenScreenUiState.Loading

        getAccountInfo(account)
    }

    private suspend fun getSelectedNetwork() {
        selectedNetwork = getSelectedNetworkUseCase()
    }

    private suspend fun getSupportedToken() {
        _listToken.value = getTokenByBlockchainUidUseCase.getFirst2Token(selectedNetwork.blockChainUid)
    }

    private fun getAllCoins() {
        screenModelScope.launch {
            _listCoin.value = getAllCoinUseCase()
        }
    }

    private suspend fun getAccounts() {
        _listAccount.value = getWalletAccountsUseCase(
            filterHiddenAccounts = true,
            walletId = getSelectedWalletUseCase()?.id ?: ""
        ) ?: listOf()
        if (listAccount.value.isEmpty()) {
            _uiState.value = SwapTokenScreenUiState.Error(WrappedStringResource.StringRes(MR.strings.message_swap_token_screen_no_account_found))
            return
        }
        onSelectAccount(listAccount.value.first())
    }

    private fun getAccountInfo(account: AccountBlockchainModel) {
        screenModelScope.launch {
            val result = getAccountBalanceUseCase(
                forceReload = false,
                // TODO: Pass in correct type of address if needed
                address = account.bip44Address,
                accountId = account.account.id,
                sparkline = true
            )

            val listCoin = listCoin.value

            val listTokens = listToken.value.filter {
                it.decimals != null
            }.map { tokenEntity ->
                SwapTokenUiModel.TokenUiModel(
                    tokenCode = result.find { it.tokenId == tokenEntity.id }?.contractSymbol
                        ?: listCoin.find { it.uid == tokenEntity.coinUid }?.code ?: "Error",
                    logoUrl = result.find { it.tokenId == tokenEntity.id }?.logoUrl ?: "",
                    balance = result.find { it.tokenId == tokenEntity.id }
                        ?.formattedBalanceForHuman() ?: "0.0000",
                    address = tokenEntity.reference ?: "",
                    decimal = tokenEntity.decimals!!,
                    isNative = tokenEntity.type == "native"
                )
            }

            Napier.d(
                tag = "SwapVM",
                message = "listTokens: ${listToken.value}, result: $listTokens"
            )
            _uiState.value =
                SwapTokenScreenUiState.Success(
                    SwapTokenUiModel(
                        selectedAccount = account,
                        listTokens = listTokens,
                        selectedFromToken = listTokens.first(),
                        selectedToToken = listTokens.last(),
                        price = "",
                        selectedDex = listDex.first()
                    )
                )
        }
    }

    private fun getSwapRate(
        tradeData: TradeData,
        fromTokenSymbol: String,
        toTokenSymbol: String
    ): String {
        val rate = Price(
            baseTokenAmount = tradeData.trade.tokenAmountOut,
            quoteTokenAmount = tradeData.trade.tokenAmountIn
        ).decimalValue?.scale(8)?.toPlainString()
        return rate?.let { "1 $fromTokenSymbol ≈ $it $toTokenSymbol" } ?: ""
    }

    private fun jobSycnSwapTradeDataExactIn(
        uiStateSuccess: SwapTokenScreenUiState.Success,
    ) = screenModelScope.launch {
        val value = fromTokenValue.value
        try {
            val blockchainType = BlockchainType.fromUid(selectedNetwork.blockChainUid)
            val tokenFactory = TokenFactory(Chain.fromBlockchainType(blockchainType))
            val dex = uiStateSuccess.swapTokenScreenUiModel.selectedDex
            val tokenFrom = if (uiStateSuccess.swapTokenScreenUiModel.selectedFromToken.isNative) {
                tokenFactory.etherToken()
            } else {
                tokenFactory.token(
                    contractAddress = Address(uiStateSuccess.swapTokenScreenUiModel.selectedFromToken.address),
                    decimals = uiStateSuccess.swapTokenScreenUiModel.selectedFromToken.decimal.toInt()
                )
            }
            val tokenTo = if (uiStateSuccess.swapTokenScreenUiModel.selectedToToken.isNative) {
                tokenFactory.etherToken()
            } else {
                tokenFactory.token(
                    contractAddress = Address(uiStateSuccess.swapTokenScreenUiModel.selectedToToken.address),
                    decimals = uiStateSuccess.swapTokenScreenUiModel.selectedToToken.decimal.toInt()
                )
            }
            val tradeType = TradeType.ExactIn
            this@SwapTokenScreenModel.tradeType = tradeType
            val data = getSwapTradeDataUseCase(
                blockchainType = blockchainType,
                dex = dex,
                id = currentId.getAndIncrement(),
                address = Address(uiStateSuccess.swapTokenScreenUiModel.selectedAccount.bip44Address), // TODO: Handle network with different address format
                tradeType = tradeType,
                tokenFrom = tokenFrom,
                tokenTo = tokenTo,
                amount = value.toBigDecimalOrNull(),
                tradeOptions = TradeOptions(),
                forceRefresh = true
            )
            tradeData = data

            data?.let {
                _toTokenValue.value = data.amountOut?.removeTrailingZeroes()?.toPlainString() ?: ""
                val success = SwapTokenScreenUiState.Success(
                    uiStateSuccess.swapTokenScreenUiModel.copy(
                        price = getSwapRate(
                            data,
                            uiStateSuccess.swapTokenScreenUiModel.selectedFromToken.tokenCode,
                            uiStateSuccess.swapTokenScreenUiModel.selectedToToken.tokenCode
                        ),
                        tradeError = null,
                    )
                )
                _uiState.value = success
            }

        } catch (e: TradeError) {
            // Will handle TradeError later
            Napier.d(tag = "SwapTokenVM", message = "TradeError From Token: $e")
            _toTokenValue.value = ""
            _uiState.value =
                SwapTokenScreenUiState.Success(
                    uiStateSuccess.swapTokenScreenUiModel.copy(
                        price = "",
                        tradeError = e
                    )
                )

        }
        delay(TRANSACTION_FEE_REFRESH_INTERVAL_MILLIS)
    }

    private fun jobSyncSwapTradeDataExactOut(
        uiStateSuccess: SwapTokenScreenUiState.Success,
    ) = screenModelScope.launch {
        val value = toTokenValue.value
        try {
            val blockchainType = BlockchainType.fromUid(selectedNetwork.blockChainUid)
            val tokenFactory = TokenFactory(Chain.fromBlockchainType(blockchainType))
            val dex = uiStateSuccess.swapTokenScreenUiModel.selectedDex
            val tokenFrom = if (uiStateSuccess.swapTokenScreenUiModel.selectedFromToken.isNative) {
                tokenFactory.etherToken()
            } else {
                tokenFactory.token(
                    contractAddress = Address(uiStateSuccess.swapTokenScreenUiModel.selectedFromToken.address),
                    decimals = uiStateSuccess.swapTokenScreenUiModel.selectedFromToken.decimal.toInt()
                )
            }
            val tokenTo = if (uiStateSuccess.swapTokenScreenUiModel.selectedToToken.isNative) {
                tokenFactory.etherToken()
            } else {
                tokenFactory.token(
                    contractAddress = Address(uiStateSuccess.swapTokenScreenUiModel.selectedToToken.address),
                    decimals = uiStateSuccess.swapTokenScreenUiModel.selectedToToken.decimal.toInt()
                )
            }
            val tradeType = TradeType.ExactOut
            this@SwapTokenScreenModel.tradeType = tradeType
            val data = getSwapTradeDataUseCase(
                blockchainType = blockchainType,
                dex = dex,
                id = currentId.getAndIncrement(),
                address = Address(uiStateSuccess.swapTokenScreenUiModel.selectedAccount.bip44Address), // TODO: Handle network with different address format
                tradeType = tradeType,
                tokenFrom = tokenFrom,
                tokenTo = tokenTo,
                amount = value.toBigDecimalOrNull(),
                tradeOptions = TradeOptions(),
                forceRefresh = true
            )
            tradeData = data

            data?.let {
                _fromTokenValue.value = data.amountIn?.removeTrailingZeroes()?.toPlainString() ?: ""
                val isInsufficientAmount = data.amountIn?.let {
                    it > uiStateSuccess.swapTokenScreenUiModel.selectedFromToken.balance.toBigDecimal()
                } ?: false
                val success = SwapTokenScreenUiState.Success(
                    uiStateSuccess.swapTokenScreenUiModel.copy(
                        isInsufficientAmount = isInsufficientAmount,
                        tradeError = null,
                        price = getSwapRate(
                            tradeData = data,
                            fromTokenSymbol = uiStateSuccess.swapTokenScreenUiModel.selectedFromToken.tokenCode,
                            toTokenSymbol = uiStateSuccess.swapTokenScreenUiModel.selectedToToken.tokenCode
                        )
                    )
                )
                _uiState.value = success
            }


        } catch (e: TradeError) {
            // Will handle TradeError later
            Napier.d(tag = "SwapTokenVM", message = "TradeError To Token: $e")
            _fromTokenValue.value = ""
            _uiState.value =
                SwapTokenScreenUiState.Success(
                    uiStateSuccess.swapTokenScreenUiModel.copy(
                        price = "",
                        tradeError = e
                    )
                )
        }
        delay(TRANSACTION_FEE_REFRESH_INTERVAL_MILLIS)
    }

    companion object {
        private const val TRANSACTION_FEE_REFRESH_INTERVAL_MILLIS = 30_000L
    }
}