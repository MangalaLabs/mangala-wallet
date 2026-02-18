package com.mangala.wallet.features.menu.presentation.wallet.details

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.biometry.presentation.IBiometryScreenModel
import com.mangala.wallet.domain.account.usecases.SetHiddenAccountUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.usecases.GetTokenByIdUseCase
import com.mangala.wallet.domain.wallet.usecases.DeletedWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.GetAllWalletsUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletAccountsUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletByIdUseCase
import com.mangala.wallet.domain.wallet.usecases.SaveWalletNameUseCase
import com.mangala.wallet.local.token.balance.TokenBalanceLocalDataSource
import com.mangala.wallet.local.token.price.TokenPriceLocalDataSource
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperConstants.PIN_KEY
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.pow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class WalletDetailsScreenModel(
    private val walletId: String,
    private val getWalletByIdUseCase: GetWalletByIdUseCase,
    private val saveWalletNameUseCase: SaveWalletNameUseCase,
    private val getWalletAccountsUseCase: GetWalletAccountsUseCase,
    private val tokenBalanceLocalDataSource: TokenBalanceLocalDataSource,
    private val tokenPriceLocalDataSource: TokenPriceLocalDataSource,
    private val getTokenByIdUseCase: GetTokenByIdUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val setHiddenAccountUseCase: SetHiddenAccountUseCase,
    private val deletedWalletUseCase: DeletedWalletUseCase,
    private val getSelectedWalletUseCase : GetSelectedWalletUseCase,
    private val secureStorageWrapper: SecureStorageWrapper,
    private val getAllWalletsUseCase: GetAllWalletsUseCase
) : BaseScreenModel(), KoinComponent {

    private val biometryScreenModel = get<IBiometryScreenModel>()

    val _uiModel = MutableStateFlow(WalletDetailsScreenUiModel())
    val _walletName: MutableStateFlow<String> = MutableStateFlow("")
    private var accountBalancesJob: Job? = null
    private var observedAccountIds: List<String> = emptyList()

    init {
        screenModelScope.launch {
            getWalletDetails(walletId)
        }
        screenModelScope.launch {
            getAccounts()
        }
    }

    private fun saveWalletName(walletName: String, walletId: String) {
        screenModelScope.launch {
            saveWalletNameUseCase(walletName, walletId)
        }
    }

    private suspend fun getWalletDetails(walletId: String) {
        getWalletByIdUseCase.invokeFlow(walletId).collect { wallet ->
            if (wallet != null) {
                _walletName.value = wallet.name
            }
        }
    }

    fun updateWalletName(newName: String) {
        _walletName.value = newName
        saveWalletName(newName, walletId)
    }

    fun onClickChangeHiddenAccount(accountId: String) {
        screenModelScope.launch {
            setHiddenAccountUseCase.invoke(accountId)
        }
    }

    private suspend fun getAccounts() {
        getWalletAccountsUseCase.invokeFlow(filterHiddenAccounts = false, walletId).collectLatest { accounts ->
            val existingByAccountId = _uiModel.value.accounts.associateBy { it.account.account.id }
            val accountItems = accounts.orEmpty().map { account ->
                val existing = existingByAccountId[account.account.id]
                AccountItemUiModel(
                    account = account,
                    totalValueUsd = existing?.totalValueUsd ?: "0",
                    currencySymbol = existing?.currencySymbol ?: "$",
                    isBalanceLoading = existing?.isBalanceLoading ?: false
                )
            }
            _uiModel.update { it.copy(accounts = accountItems) }

            val newAccountIds = accounts.orEmpty().map { it.account.id }
            if (newAccountIds == observedAccountIds) return@collectLatest
            observedAccountIds = newAccountIds

            accountBalancesJob?.cancel()
            if (accounts.isNullOrEmpty()) return@collectLatest
            accountBalancesJob = screenModelScope.launch {
                observeAccountBalances(accounts)
            }
        }
    }

    private suspend fun observeAccountBalances(accounts: List<AccountBlockchainModel>) {
        val selectedNetwork = getSelectedNetworkUseCase()
        val currencyCode = getCurrentCurrencyCodeUseCase()
        val currencySymbol = runCatching {
            Currency.valueOf(currencyCode).symbol
        }.getOrDefault("$")

        coroutineScope {
            accounts.forEach { account ->
                launch {
                    tokenBalanceLocalDataSource.getTokenBalanceByAccountIdAndBlockchainUidFlow(
                        accountId = account.account.id,
                        blockchainUid = selectedNetwork.blockChainUid
                    ).collect { tokenBalances ->
                        val newState = AccountBalanceState(
                            totalValueUsd = calculateTotalValueUsdFromLocal(
                                tokenBalances = tokenBalances,
                                currencyCode = currencyCode
                            ),
                            isLoading = false,
                            currencySymbol = currencySymbol
                        )
                        _uiModel.update { current ->
                            current.copy(
                                accounts = current.accounts.map { item ->
                                    if (item.account.account.id != account.account.id) return@map item
                                    item.copy(
                                        totalValueUsd = newState.totalValueUsd,
                                        currencySymbol = newState.currencySymbol,
                                        isBalanceLoading = newState.isLoading
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun calculateTotalValueUsdFromLocal(
        tokenBalances: List<TokenBalanceEntity>,
        currencyCode: String
    ): String {
        if (tokenBalances.isEmpty()) return "0"

        val tokenByTokenId = tokenBalances.associate { balance ->
            balance.tokenId to getTokenByIdUseCase(balance.tokenId).firstOrNull()
        }
        val coinUids = tokenByTokenId.values.mapNotNull { it?.coinUid }.distinct()
        val localPrices = tokenPriceLocalDataSource
            .getTokenWithSparklineByCoinUidsAndCurrencyCode(coinUids, currencyCode)
            .associateBy { it.coinUid }

        val totalUsd = tokenBalances.sumOf { tokenBalance ->
            val rawBalance = tokenBalance.balance.toDoubleOrNull() ?: 0.0
            val decimalsDivider = 10.0.pow(tokenBalance.contractDecimals.toInt())
            val normalizedBalance = if (decimalsDivider == 0.0) 0.0 else rawBalance / decimalsDivider
            val coinUid = tokenByTokenId[tokenBalance.tokenId]?.coinUid
            val currentPrice = localPrices[coinUid]?.currentPrice?.toDoubleOrNull() ?: 0.0
            normalizedBalance * currentPrice
        }
        return totalUsd.toString()
    }

    fun onClickDeletedWallet(walletId : String) {
        screenModelScope.launch {
            deletedWalletUseCase(walletId)
        }
    }
}

private data class AccountBalanceState(
    val totalValueUsd: String,
    val isLoading: Boolean,
    val currencySymbol: String
)
