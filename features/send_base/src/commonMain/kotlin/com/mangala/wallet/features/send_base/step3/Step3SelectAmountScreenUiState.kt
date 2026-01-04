package com.mangala.wallet.features.send_base.step3

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.token.AntelopeTokenBalance
import com.mangala.wallet.features.chains.bitcoin.domain.model.account.BitcoinAccount
import com.mangala.wallet.features.chains.bitcoin.domain.utils.satoshisToBtc
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.token.domain.formattedBalance
import com.mangala.wallet.model.token.domain.formattedValue
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.utils.ext.formatFiat
import com.mangala.wallet.utils.toBigDecimalOrNull
import dev.icerock.moko.resources.ImageResource

enum class AmountValidationError(val errorMessage: String) {
    DUST_AMOUNT("Amount is below dust threshold (0.00000546 BTC)"),
}

sealed interface Step3SelectAmountScreenUiState {
    data class SelectAccount(
        val accounts: List<SelectAmountAccountWrapper>,
        val accountsFilter: String = "",
        val selectedAccount: SelectAmountAccountWrapper?,
        val isTransferToSelf: Boolean = false,
        val recipientAddress: String? = null,
        val networkType: NetworkType? = null,
    ) : Step3SelectAmountScreenUiState {
        val filteredAccounts: List<SelectAmountAccountWrapper>
            get() = accounts.filter { account ->
                val matchesQuery = accountsFilter.isBlank() || account.queryMatches(accountsFilter)
                val isNotRecipient = networkType != NetworkType.ANTELOPE ||
                    recipientAddress.isNullOrBlank() ||
                    account.formattedAddress != recipientAddress
                matchesQuery && isNotRecipient
            }

        val isNoImportedAccount by lazy {
            accounts.isEmpty() && accountsFilter.isBlank()
        }
    }
    data class SelectToken(
        val currencySymbol: String,
        val selectAccount: SelectAccount,
        val allTokens: List<SelectAmountTokenWrapper>,
        val tokenInput: String,
        val isLoading: Boolean,
        val selectedToken: SelectAmountTokenWrapper?
    ) : Step3SelectAmountScreenUiState {
        val tokenList: List<SelectAmountTokenWrapper>
            get() = allTokens.filter {
                it.queryMatches(tokenInput)
            }
    }
    data class SelectAmount(
        val selectToken: SelectToken,
        val networkType: NetworkType,
        val isInsufficientBalance: Boolean,
        val amountValidationError: AmountValidationError? = null
    ) : Step3SelectAmountScreenUiState
    data class EnterMemo(
        val memo: String,
        val selectAmount: SelectAmount
    ) : Step3SelectAmountScreenUiState {
        val amountValidationError: AmountValidationError? = selectAmount.amountValidationError
    }

    val accountQuery: String
        get() = when (this) {
            is SelectAccount -> accountsFilter
            is SelectToken -> selectAccount.accountsFilter
            is SelectAmount -> selectToken.selectAccount.accountsFilter
            is EnterMemo -> selectAmount.selectToken.selectAccount.accountsFilter
        }
    val tokenQuery: String
        get() = when (this) {
            is SelectToken -> tokenInput
            is SelectAmount -> selectToken.tokenInput
            is EnterMemo -> selectAmount.selectToken.tokenInput
            else -> ""
        }
    val doneSelectAccount: Boolean
        get() = this is SelectToken || this is SelectAmount || this is EnterMemo
    val doneSelectToken
        get() = this is SelectAmount || this is EnterMemo
    val doneSelectAmount
        get() = this is EnterMemo
    val buttonEnabled
        get() = ((this is SelectAmount && networkType != NetworkType.ANTELOPE && amountValidationError == null) || 
                (this is EnterMemo && selectAmount.amountValidationError == null))
    val isAmountError
        get() = (this is SelectAmount && (isInsufficientBalance || amountValidationError != null)) || 
                (this is EnterMemo && (selectAmount.isInsufficientBalance || selectAmount.amountValidationError != null))
}

sealed interface SelectAmountAccountWrapper {
    fun queryMatches(query: String): Boolean
    val accountId: String
    val accountName: String
    val formattedAddress: String

    data class Evm(val account: AccountBlockchainModel): SelectAmountAccountWrapper {
        override val accountId: String = account.account.id
        override val accountName: String = account.account.name
        override val formattedAddress: String = Address(account.bip44Address).eip55

        override fun queryMatches(query: String): Boolean {
            return account.account.name.contains(query, ignoreCase = true)
        }
    }
    data class Antelope(val account: AntelopeAccount): SelectAmountAccountWrapper {
        override val accountId: String = account.accountName
        override val accountName: String = account.accountName
        override val formattedAddress: String = accountName

        override fun queryMatches(query: String): Boolean {
            return account.accountName.contains(query, ignoreCase = true)
        }
    }
    data class Bitcoin(val account: BitcoinAccount): SelectAmountAccountWrapper {
        override val accountId: String = account.accountId
        override val accountName: String = account.name.orEmpty()
        override val formattedAddress: String = account.bip84Address

        override fun queryMatches(query: String): Boolean {
            return account.name?.contains(query, ignoreCase = true) ?: true
        }
    }
}

internal fun AccountBlockchainModel.wrap(): SelectAmountAccountWrapper {
    return SelectAmountAccountWrapper.Evm(this)
}

internal fun AntelopeAccount.wrap(): SelectAmountAccountWrapper {
    return SelectAmountAccountWrapper.Antelope(this)
}

internal fun BitcoinAccount.wrap(): SelectAmountAccountWrapper {
    return SelectAmountAccountWrapper.Bitcoin(this)
}

sealed interface SelectAmountTokenWrapper {
    fun queryMatches(query: String): Boolean
    val decimals: Long
    val tokenId: String
    val name: String
    val balance: String
    val logoUrl: String
    val localImage: ImageResource?
    fun currentPrice(currencySymbol: String): String
    val formattedBalance: String
    fun formattedValue(currencySymbol: String): String

    data class Evm(val token: TokenBalanceModel): SelectAmountTokenWrapper {
        override val decimals = token.contractDecimals
        override val tokenId: String = token.tokenId.toString()
        override val name: String = token.contractName
        override val balance: String = token.balance
        override val logoUrl: String = token.logoUrl
        override val localImage: ImageResource? = token.localImage
        override fun currentPrice(currencySymbol: String) = currencySymbol + token.currentPrice
        override val formattedBalance: String = token.formattedBalance()
        override fun formattedValue(currencySymbol: String): String {
            return token.formattedValue(currencySymbol)
        }

        override fun queryMatches(query: String): Boolean {
            return token.contractName.contains(query, ignoreCase = true)
        }
    }
    data class Antelope(val token: AntelopeTokenBalance, val nativeCoinPrice: BigDecimal, val blockchainType: com.mangala.wallet.model.blockchain.BlockchainType): SelectAmountTokenWrapper {
        override val decimals = token.decimals.toLong()
        override val tokenId: String = token.symbol
        override val name: String = token.metadata.name
        override val balance: String = token.amount.toString()
        override val logoUrl: String = token.metadata.logo
        override val localImage: ImageResource? = when {
            token.symbol == "A" -> MR.images.vaulta
            token.symbol == "V" && blockchainType.isEosNetwork() -> MR.images.vaultram
            token.symbol == "EOS" -> MR.images.eos_new
            else -> token.metadata.localImage
        }
        override fun currentPrice(currencySymbol: String): String {
            val value = if (token.symbol == "A") {
                nativeCoinPrice
            } else {
                token.exchanges.firstOrNull()?.price?.toBigDecimal()?.times(nativeCoinPrice)
            }

            return value?.formatFiat(currencySymbol).orEmpty()
        }

        override val formattedBalance: String = BalanceFormatter.formatEosBalance(
            Balance(token.amount, token.symbol, token.decimals),
            ignoreLocale = false
        ).trimEnd('0')
        override fun formattedValue(currencySymbol: String): String {
            val value = if (token.symbol == "A") {
                nativeCoinPrice.times(token.amount.toBigDecimal())
            } else {
                token.exchanges.firstOrNull()?.price?.toBigDecimal()?.times(nativeCoinPrice)
                    ?.times(token.amount.toBigDecimal()) ?: BigDecimal.ZERO
            }

            return value.formatFiat(currencySymbol)
        }

        override fun queryMatches(query: String): Boolean {
            return token.metadata.name.contains(query, ignoreCase = true)
        }
    }
    
    data class Bitcoin(val token: TokenBalanceModel): SelectAmountTokenWrapper {
        override val decimals = 8L
        override val tokenId: String = token.tokenId.toString()
        override val name: String = token.contractName
        override val balance: String = token.balance // In satoshis
        override val logoUrl: String = token.logoUrl
        override val localImage: ImageResource? = token.localImage
        override fun currentPrice(currencySymbol: String) = currencySymbol + token.currentPrice?.toBigDecimalOrNull()?.toStringExpanded()
        
        override val formattedBalance: String = try {
            val satoshis = BigDecimal.parseString(token.balance)
            val btc = satoshis.satoshisToBtc()
            "${btc.toPlainString()} BTC"
        } catch (e: Exception) {
            "${token.balance} sats"
        }
        
        override fun formattedValue(currencySymbol: String): String {
            return token.formattedValue(currencySymbol)
        }

        override fun queryMatches(query: String): Boolean {
            return token.contractName.contains(query, ignoreCase = true)
        }
    }
}

internal fun TokenBalanceModel.wrapBitcoin(): SelectAmountTokenWrapper {
    return SelectAmountTokenWrapper.Bitcoin(this)
}

internal fun TokenBalanceModel.wrapEvm(): SelectAmountTokenWrapper {
    return SelectAmountTokenWrapper.Evm(this)
}

internal fun AntelopeTokenBalance.wrap(nativeCoinPrice: BigDecimal, blockchainType: com.mangala.wallet.model.blockchain.BlockchainType): SelectAmountTokenWrapper {
    return SelectAmountTokenWrapper.Antelope(
        this,
        nativeCoinPrice,
        blockchainType
    )
}