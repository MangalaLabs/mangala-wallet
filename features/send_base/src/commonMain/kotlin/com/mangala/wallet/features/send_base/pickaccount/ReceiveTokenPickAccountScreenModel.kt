package com.mangala.wallet.features.send_base.pickaccount

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.features.wallet.presentation.AntelopeAccountItemUiModel
import com.mangala.features.wallet.presentation.AntelopeAssetsUiModel.TokenBalanceUiModel
import com.mangala.features.wallet.presentation.toImageSource
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.domain.account.usecases.GetAccountBalanceUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountWithBalanceInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamPriceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.GetRexBalanceInNativeCoinUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.token.GetAntelopeAccountTokenBalanceUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.ext.bytesToKilobytes
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.toBigDecimalOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReceiveTokenPickAccountScreenModel(
    private val networkType: NetworkType,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase,
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase,
    private val getRamPriceUseCase: GetRamPriceUseCase,
    private val getAccountWithBalanceInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val getRexBalanceInNativeCoinUseCase: GetRexBalanceInNativeCoinUseCase,
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    private val getAntelopeAccountTokenBalanceUseCase: GetAntelopeAccountTokenBalanceUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAntelopeAccountsUseCase: GetAccountsUseCase
) : BaseScreenModel() {

    private val _uiState =
        MutableStateFlow<ReceiveTokenPickAccountScreenUiState>(ReceiveTokenPickAccountScreenUiState.Loading)
    val uiState: StateFlow<ReceiveTokenPickAccountScreenUiState> get() = _uiState

    override fun doOnComposableStarted() {
        lifecycleScope.launch { loadAccounts() }
    }

    private suspend fun loadAccounts() {
        val networkSelected = getSelectedNetworkUseCase()
        _uiState.value = ReceiveTokenPickAccountScreenUiState.Loading
        val result = getSelectedWalletAccountsUseCase()
        val currencyCode = getCurrentCurrencyCodeUseCase()
        val currencySymbol = Currency.valueOf(currencyCode).symbol

        when (networkType) {
            NetworkType.EVM -> {
                result?.map { accounts ->
                    AccountUiModel(
                        account = accounts,
                        balance = getAccountBalanceUseCase(
                            forceReload = false,
                            accounts.bip44Address,
                            accounts.account.id,
                            sparkline = false
                        ),
                        currencySymbol = currencySymbol
                    )
                }?.let { accountItemUiModel ->
                    _uiState.update {
                        (it as? ReceiveTokenPickAccountScreenUiState.Success)?.copy(
                            uiModel = it.uiModel.copy(
                                items = accountItemUiModel.map { account ->
                                    AccountUiModelPickAccount.Evm(account)
                                }
                            )
                        ) ?: ReceiveTokenPickAccountScreenUiState.Success(
                            ReceiveTokenPickAccountScreenUiModel(
                                items = accountItemUiModel.map { account ->
                                    AccountUiModelPickAccount.Evm(account)
                                }
                            )
                        )
                    }
                }
            }
            NetworkType.ANTELOPE -> {
                checkAndLoadAntelopeSelectedWalletAccounts(currencySymbol)
                loadAntelopeBalance(
                    lifecycleScope,
                    false,
                    networkSelected.blockchainType,
                    currencySymbol
                )
            }

            else -> TODO()
        }
    }

    private suspend fun checkAndLoadAntelopeSelectedWalletAccounts(fiatCurrencySymbol: String): Boolean {
        val updatedAccounts = getAntelopeAccountsUseCase().map {
            it.toAntelopeAccountItemUiModel(fiatCurrencySymbol)
        }

        val currentAccounts = (_uiState.value as? ReceiveTokenPickAccountScreenUiState.Success)?.uiModel?.items
        val currentAccountNames = currentAccounts?.map { it.key }?.toSet() ?: emptySet()

        val updatedAccountNames = updatedAccounts.map { it.account.accountName }.toSet()
        if (updatedAccountNames == currentAccountNames) return false

        _uiState.update {
            ReceiveTokenPickAccountScreenUiState.Success(
                uiModel = ReceiveTokenPickAccountScreenUiModel(
                    items = updatedAccounts.map { account ->
                        AccountUiModelPickAccount.Antelope(account,fiatCurrencySymbol)
                    }
                )
            )
        }
        return true
    }

    // Extension function to simplify the AntelopeAccountItemUiModel mapping
    private fun AntelopeAccount.toAntelopeAccountItemUiModel(fiatCurrencySymbol: String): AntelopeAccountItemUiModel {
        return AntelopeAccountItemUiModel(
            account = this,
            isBalanceVisible = true,
            currencySymbol = fiatCurrencySymbol,
            coreBalanceValue = null,
            coreBalanceSymbol = null,
            cpuUsagePercentage = null,
            netUsagePercentage = null,
            ramBalanceBytes = null,
            ramPriceKilobytes = null,
            stakedCpu = null,
            stakedNet = null,
            stakedInRex = null,
            nativeCoinPnl = null,
            nativeCoinPrice = null,
            totalValueInNativeCoin = null,
            assets = null,
            coinGeckoExchangeRate = null,
            isLoading = true
        )
    }


    private suspend fun loadAntelopeBalance(
        coroutineScope: CoroutineScope,
        forceReload: Boolean,
        blockchainType: BlockchainType,
        fiatCurrencySymbol: String
    ) {
        val antelopeData = _uiState.value as? ReceiveTokenPickAccountScreenUiState.Success
            ?: return

        val antelopeAccounts = antelopeData.uiModel.items.mapNotNull {
            (it as? AccountUiModelPickAccount.Antelope)?.account
        }

        val newAccountDataListDeferred = antelopeAccounts.map { accountData ->
            coroutineScope.async {
                getAccountWithBalanceInfoUseCase(
                    accountData.account.accountName,
                    forceReload
                )
            }
        }
        val ramPriceAsync = coroutineScope.async {
            getRamPriceUseCase(blockchainType, forceReload)
        }
        val nativeCoin = getNativeCoinUseCase(blockchainType.uid)
        val nativeTokenPriceAsync = coroutineScope.async {
            fetchTokenPriceUseCase(
                forceReload = forceReload,
                tokenUid = nativeCoin.coinUid,
                sparkline = true
            )
        }
        val newAccountDataList =
            newAccountDataListDeferred.awaitAll().map { it.getOrNull() }
        val ramPrice = ramPriceAsync.await()
        val nativeTokenPrice = nativeTokenPriceAsync.await()
        val balancesModel = antelopeAccounts.mapIndexed { index, accountData ->
            val newAccountData = newAccountDataList[index]
            val coreBalance = newAccountData?.safeCoreBalance
            val cpuWeight =
                BalanceFormatter.deserializeOrNull(newAccountData?.selfDelegatedBandwidthCpuWeight.orEmpty())
            val netWeight =
                BalanceFormatter.deserializeOrNull(newAccountData?.selfDelegatedBandwidthNetWeight.orEmpty())
            val accountName = accountData.account.accountName
            val rexBalanceDouble = newAccountData?.rexBalance?.let {
                if (it.isBlank()) return@let 0.0

                val accountRexBalance = BalanceFormatter.deserialize(it)
                getRexBalanceInNativeCoinUseCase(
                    accountName,
                    accountRexBalance,
                    forceReload
                )// TODO: Handle error cannot load REX data to calculate value
            } ?: 0.0
            val tokenBalanceAsync = coroutineScope.async {
                getAntelopeAccountTokenBalanceUseCase(
                    accountName,
                    blockchainType,
                    forceReload
                )
            }
            val tokenBalance =
                tokenBalanceAsync.await()
            val nativeCoinPrice = nativeTokenPrice?.currentPrice?.toBigDecimalOrNull()
            val tokenBalances = tokenBalance.getOrNull()?.map { token ->
                TokenBalanceUiModel(
                    key = token.key,
                    name = token.metadata.name,
                    symbol = token.symbol,
                    balance = token.amount.toBigDecimal(),
                    priceInNativeCoin = token.exchanges.firstOrNull()?.price?.toBigDecimal()
                        ?: BigDecimal.ZERO,
                    nativeCoinPrice = nativeCoinPrice ?: BigDecimal.ZERO,
                    iconResource = token.metadata.toImageSource(),
                    fiatSymbol = fiatCurrencySymbol,
                    sparkline = null,
                    priceChangePercentage24h = null
                )
            } ?: emptyList()
            val totalValueInNativeCoin = getAntelopeTotalAccountValue(
                newAccountData?.ramQuota,
                ramPrice?.price,
                coreBalance?.amount?.toBigDecimal(),
                cpuWeight?.amount?.toBigDecimal(),
                netWeight?.amount?.toBigDecimal(),
                rexBalanceDouble.toBigDecimal(),
                tokenBalances.map { it.priceInNativeCoin?.times(it.balance.orZero()).orZero() }
                    .reduceOrNull { acc, value -> acc.plus(value) }
            )
            accountData.copy(
                coreBalanceValue = coreBalance?.amount?.toBigDecimal() ?: BigDecimal.ZERO,
                coreBalanceSymbol = coreBalance?.symbol.orEmpty(),
                cpuUsagePercentage = 0.0,
                netUsagePercentage = 0.0,
                ramBalanceBytes = 0L,
                ramPriceKilobytes = BigDecimal.ZERO,
                stakedCpu = BigDecimal.ZERO,
                stakedNet = BigDecimal.ZERO,
                stakedInRex = BigDecimal.ZERO,
                nativeCoinPnl = BigDecimal.ZERO,
                nativeCoinPrice = nativeCoinPrice,
                totalValueInNativeCoin = totalValueInNativeCoin,
                assets = listOf()
            )
        }
        _uiState.update {
            if (it !is ReceiveTokenPickAccountScreenUiState.Success) return@update it
            it.copy(uiModel =
            ReceiveTokenPickAccountScreenUiModel(
                items = balancesModel.map { account ->
                    AccountUiModelPickAccount.Antelope(account, fiatCurrencySymbol)
                }
            ))
        }
    }


    private fun getAntelopeTotalAccountValue(
        ramBalanceBytes: Long?,
        ramPriceKilobytes: BigDecimal?,
        coreBalanceValue: BigDecimal?,
        stakedCpu: BigDecimal?,
        stakedNet: BigDecimal?,
        stakedInRex: BigDecimal?,
        tokenBalance: BigDecimal?
    ): BigDecimal? {
        val ramBalanceInNativeCoin = if (ramBalanceBytes == null || ramPriceKilobytes == null) {
            null
        } else {
            ramBalanceBytes.bytesToKilobytes().toBigDecimal().times(ramPriceKilobytes)
        }
        val result = coreBalanceValue
            ?.plus(stakedCpu ?: BigDecimal.ZERO)
            ?.plus(stakedNet ?: BigDecimal.ZERO)
            ?.plus(stakedInRex ?: BigDecimal.ZERO)
            ?.plus(ramBalanceInNativeCoin ?: BigDecimal.ZERO)
            ?.plus(tokenBalance ?: BigDecimal.ZERO)

        return result
    }
}