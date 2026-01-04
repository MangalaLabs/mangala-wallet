package com.mangala.wallet.local.di

import com.mangala.wallet.local.blockchain.BlockchainLocalDataSource
import com.mangala.wallet.local.blockchain.BlockchainLocalDataSourceImpl
import com.mangala.wallet.local.coin.CoinLocalDataSource
import com.mangala.wallet.local.coin.CoinLocalDataSourceImpl
import com.mangala.wallet.local.account.AccountLocalDataSource
import com.mangala.wallet.local.account.AccountLocalDataSourceImpl
import com.mangala.wallet.local.cache.RemoteKeyLocalDataSource
import com.mangala.wallet.local.cache.RemoteKeyLocalDataSourceImpl
import com.mangala.wallet.local.cache.TransactionMetadataLocalDataSource
import com.mangala.wallet.local.cache.TransactionMetadataLocalDataSourceImpl
import com.mangala.wallet.local.currency.CurrencyLocalDataSource
import com.mangala.wallet.local.currency.CurrencyLocalDataSourceImpl
import com.mangala.wallet.local.dapp.DAppLocalDataSource
import com.mangala.wallet.local.dapp.DAppLocalDataSourceImpl
import com.mangala.wallet.local.language.LanguageLocalDataSource
import com.mangala.wallet.local.language.LanguageLocalDataSourceImpl
import com.mangala.wallet.local.wallet.WalletLocalDataSource
import com.mangala.wallet.local.wallet.WalletLocalDataSourceImpl
import com.mangala.wallet.local.token.*
import com.mangala.wallet.local.token.balance.TokenBalanceLocalDataSource
import com.mangala.wallet.local.token.balance.TokenBalanceLocalDataSourceImpl
import com.mangala.wallet.local.token.exchangerate.TokenExchangeRateLocalDataSource
import com.mangala.wallet.local.token.exchangerate.TokenExchangeRateLocalDataSourceImpl
import com.mangala.wallet.local.token.exchangerate.TokenExchangeRateMetadataLocalDataSource
import com.mangala.wallet.local.token.exchangerate.TokenExchangeRateMetadataLocalDataSourceImpl
import com.mangala.wallet.local.token.historicalprice.TokenHistoricalPriceLocalDataSource
import com.mangala.wallet.local.token.historicalprice.TokenHistoricalPriceLocalDataSourceImpl
import com.mangala.wallet.local.token.price.TokenPriceLocalDataSource
import com.mangala.wallet.local.token.price.TokenPriceLocalDataSourceImpl
import com.mangala.wallet.local.transaction.history.TransactionLocalDataSource
import com.mangala.wallet.local.transaction.history.TransactionLocalDataSourceImpl
import com.mangala.wallet.local.portfolio.PortfolioLocalDataSource
import com.mangala.wallet.local.portfolio.PortfolioLocalDataSourceImpl
import com.mangala.wallet.local.portfolio.PortfolioDetailLocalDataSource
import com.mangala.wallet.local.portfolio.PortfolioDetailLocalDataSourceImpl
import org.koin.dsl.module

fun localModule() = module {
    single<WalletLocalDataSource> { WalletLocalDataSourceImpl(get(), get()) }
    single<AccountLocalDataSource> { AccountLocalDataSourceImpl(get()) }
    single<BlockchainLocalDataSource> { BlockchainLocalDataSourceImpl(get()) }
    single<CoinLocalDataSource> { CoinLocalDataSourceImpl(get()) }
    single<TokenLocalDataSource> { TokenLocalDataSourceImpl(get()) }
    single<TokenBalanceLocalDataSource> { TokenBalanceLocalDataSourceImpl(get()) }
    single<TokenPriceLocalDataSource> { TokenPriceLocalDataSourceImpl(get()) }
    single<TokenHistoricalPriceLocalDataSource> { TokenHistoricalPriceLocalDataSourceImpl(get()) }
    single<LanguageLocalDataSource> { LanguageLocalDataSourceImpl(get()) }
    single<CurrencyLocalDataSource> { CurrencyLocalDataSourceImpl(get()) }
    single<DAppLocalDataSource> { DAppLocalDataSourceImpl(get()) }
    single<RemoteKeyLocalDataSource> { RemoteKeyLocalDataSourceImpl(get()) }
    single<TransactionMetadataLocalDataSource> { TransactionMetadataLocalDataSourceImpl(get()) }
    single<TransactionLocalDataSource> { TransactionLocalDataSourceImpl(get()) }
    single<TokenExchangeRateLocalDataSource> { TokenExchangeRateLocalDataSourceImpl(get()) }
    single<TokenExchangeRateMetadataLocalDataSource> { TokenExchangeRateMetadataLocalDataSourceImpl(get()) }
    
    // Portfolio Local Data Sources
    single<PortfolioLocalDataSource> { PortfolioLocalDataSourceImpl(get()) }
    single<PortfolioDetailLocalDataSource> { PortfolioDetailLocalDataSourceImpl(get(), get()) }
}