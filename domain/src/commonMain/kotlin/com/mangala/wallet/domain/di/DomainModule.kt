package com.mangala.wallet.domain.di

import com.mangala.wallet.domain.account.repository.AccountRepository
import com.mangala.wallet.domain.account.repository.AccountRepositoryImpl
import com.mangala.wallet.domain.account.usecases.CreateWalletAccountUseCase
import com.mangala.wallet.domain.account.usecases.GetAccountBalanceUseCase
import com.mangala.wallet.domain.account.usecases.GetAccountBalancesInEvmAccountUseCase
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.account.usecases.GetAllAccountBalancesInEvmWalletUseCase
import com.mangala.wallet.domain.portfolio.usecases.GetAllWalletsPortfolioUseCase
import com.mangala.wallet.domain.account.usecases.SetHiddenAccountUseCase
import com.mangala.wallet.domain.account.usecases.UpdateAccountUseCase
import com.mangala.wallet.domain.account.usecases.UpdateAccountsUseCase
import com.mangala.wallet.domain.blockchain.repository.BlockchainRepository
import com.mangala.wallet.domain.blockchain.repository.BlockchainRepositoryImpl
import com.mangala.wallet.domain.blockchain.usecases.CountBlockchainUseCase
import com.mangala.wallet.domain.blockchain.usecases.CreateBlockchainUseCase
import com.mangala.wallet.domain.blockchain.usecases.DeleteBlockchainUseCase
import com.mangala.wallet.domain.blockchain.usecases.GetAllBlockchainUseCase
import com.mangala.wallet.domain.blockchain.usecases.GetBlockchainByUidUseCase
import com.mangala.wallet.domain.blockchain.usecases.GetBlockchainExplorerLinkUseCase
import com.mangala.wallet.domain.coin.repository.CoinRepository
import com.mangala.wallet.domain.coin.repository.CoinRepositoryImpl
import com.mangala.wallet.domain.coin.usecases.CreateCoinUseCase
import com.mangala.wallet.domain.coin.usecases.DeleteCoinUidUseCase
import com.mangala.wallet.domain.coin.usecases.DeleteCoinUseCase
import com.mangala.wallet.domain.coin.usecases.GetAllCoinUseCase
import com.mangala.wallet.domain.coin.usecases.GetCoinByUidUseCase
import com.mangala.wallet.domain.currency.repository.CurrencyRepository
import com.mangala.wallet.domain.currency.repository.CurrencyRepositoryImpl
import com.mangala.wallet.domain.currency.usecases.ChangeCurrencyUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.dapp.repository.DAppRepository
import com.mangala.wallet.domain.dapp.repository.DAppRepositoryImpl
import com.mangala.wallet.domain.dapp.usecase.DeleteDAppUseCase
import com.mangala.wallet.domain.dapp.usecase.GetDAppFlowUseCase
import com.mangala.wallet.domain.dapp.usecase.GetDAppUseCase
import com.mangala.wallet.domain.dapp.usecase.GetDAppsByCategoriesUseCase
import com.mangala.wallet.domain.dapp.usecase.GetDAppsBySingleCategoryUseCase
import com.mangala.wallet.domain.dapp.usecase.GetDappsJsonUseCase
import com.mangala.wallet.domain.dapp.usecase.GetListDAppFlowUseCase
import com.mangala.wallet.domain.dapp.usecase.GetListDAppUseCase
import com.mangala.wallet.domain.dapp.usecase.GetListOfCategoriesUseCase
import com.mangala.wallet.domain.dapp.usecase.SaveDAppUseCase
import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import com.mangala.wallet.domain.datastore.repository.DataStoreRepositoryImpl
import com.mangala.wallet.domain.datastore.usecases.CheckInitialDatabaseUseCase
import com.mangala.wallet.domain.datastore.usecases.CheckOnboardingCompletedUseCase
import com.mangala.wallet.domain.datastore.usecases.CompleteOnboardingUseCase
import com.mangala.wallet.domain.datastore.usecases.GetBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveInitialDatabaseUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveSelectedNetworkUseCase
import com.mangala.wallet.domain.language.repository.LanguageRepository
import com.mangala.wallet.domain.language.repository.LanguageRepositoryImpl
import com.mangala.wallet.domain.language.usecase.ChangeLanguageUseCase
import com.mangala.wallet.domain.language.usecase.GetAllSupportedLanguageUseCase
import com.mangala.wallet.domain.language.usecase.GetCurrentLanguageUseCase
import com.mangala.wallet.domain.provider.coingecko.repository.CoingeckoRepository
import com.mangala.wallet.domain.provider.coingecko.repository.CoingeckoRepositoryImpl
import com.mangala.wallet.domain.provider.coingecko.usecases.GetMarketTickerUseCase
import com.mangala.wallet.domain.reset.usecases.ClearAccountsUseCase
import com.mangala.wallet.domain.reset.usecases.ClearConversationHistoryUseCase
import com.mangala.wallet.domain.reset.usecases.ClearDataStoreUseCase
import com.mangala.wallet.domain.reset.usecases.ClearSecureStorageUseCase
import com.mangala.wallet.domain.reset.usecases.ClearTokenBalancesUseCase
import com.mangala.wallet.domain.reset.usecases.ClearEVMTransactionHistoryUseCase
import com.mangala.wallet.domain.reset.usecases.ClearWalletsUseCase
import com.mangala.wallet.domain.reset.usecases.ResetWalletUseCase
import com.mangala.wallet.domain.token.balance.usecases.CreateTokenBalanceUseCase
import com.mangala.wallet.domain.token.balance.usecases.DeleteTokenBalanceUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.balance.usecases.UpdateTokenBalanceUseCase
import com.mangala.wallet.domain.token.historicalprice.repository.TokenHistoricalPriceRepository
import com.mangala.wallet.domain.token.historicalprice.repository.TokenHistoricalPriceRepositoryImpl
import com.mangala.wallet.domain.token.historicalprice.usecases.FetchHistoricalTokenPriceUseCase
import com.mangala.wallet.domain.token.price.repository.TokenPriceRepository
import com.mangala.wallet.domain.token.price.repository.TokenPriceRepositoryImpl
import com.mangala.wallet.domain.token.price.usecases.CreateTokenPriceUseCase
import com.mangala.wallet.domain.token.price.usecases.DeleteTokenPriceUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.price.usecases.GetTokenPriceByCoinIdUseCase
import com.mangala.wallet.domain.token.price.usecases.UpdateTokenPriceUseCase
import com.mangala.wallet.domain.token.repository.TokenRepository
import com.mangala.wallet.domain.token.repository.TokenRepositoryImpl
import com.mangala.wallet.domain.token.usecases.CreateTokenUseCase
import com.mangala.wallet.domain.token.usecases.DeleteTokenByIdUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.token.usecases.GetTokenByBlockchainUidUseCase
import com.mangala.wallet.domain.token.usecases.GetTokenByIdAndBlockchainIdUseCase
import com.mangala.wallet.domain.token.usecases.GetTokenByIdUseCase
import com.mangala.wallet.domain.token.usecases.GetTokenByReferenceUseCase
import com.mangala.wallet.domain.token.usecases.ScanTokenByChainNetworkUseCase
import com.mangala.wallet.domain.transaction.history.repository.TransactionRepository
import com.mangala.wallet.domain.transaction.history.repository.TransactionRepositoryImpl
import com.mangala.wallet.domain.transaction.history.usecases.GetTransactionByTxHashUseCase
import com.mangala.wallet.domain.transaction.history.usecases.GetTransactionHistoryUseCase
import com.mangala.wallet.domain.transaction.history.usecases.SaveTransactionHistoryUseCase
import com.mangala.wallet.domain.portfolio.repository.PortfolioRepository
import com.mangala.wallet.domain.portfolio.repository.PortfolioRepositoryImpl
import com.mangala.wallet.domain.portfolio.usecases.AddAddressToPortfolioUseCase
import com.mangala.wallet.domain.portfolio.usecases.CreatePortfolioUseCase
import com.mangala.wallet.domain.portfolio.usecases.EnsureAccountInPortfolioUseCase
import com.mangala.wallet.domain.portfolio.usecases.GetPortfolioBalanceUseCase
import com.mangala.wallet.domain.portfolio.usecases.SyncPortfolioDataUseCase
import com.mangala.wallet.domain.portfolio.usecases.GetPortfolioByAccountUseCase
import com.mangala.wallet.domain.portfolio.usecases.PortfolioAccountCreator
import com.mangala.wallet.domain.portfolio.error.PortfolioErrorHandler
import com.mangala.wallet.domain.portfolio.mapper.PortfolioNetworkMapper
import com.mangala.wallet.domain.wallet.usecases.account.AccountCreator
import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.domain.wallet.repository.WalletRepositoryImpl
import com.mangala.wallet.domain.wallet.usecases.BaseGetWalletAccountsUseCase
import com.mangala.wallet.domain.wallet.usecases.CreateWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.DeletedWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.GetAllWalletsUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletAccountsUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletByIdUseCase
import com.mangala.wallet.domain.wallet.usecases.MapAccountToAccountBlockchainUseCase
import com.mangala.wallet.domain.wallet.usecases.RestoreWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.SaveWalletNameUseCase
import com.mangala.wallet.domain.wallet.usecases.SelectWalletUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun domainModule() = module {
    factoryOf(::WalletRepositoryImpl) bind WalletRepository::class
    factoryOf(::AccountRepositoryImpl) bind AccountRepository::class

    factory { CreateWalletAccountUseCase(get(), get(), get(), getAll()) }
    factoryOf(::UpdateAccountsUseCase)
    factoryOf(::UpdateAccountUseCase)

    singleOf(::DataStoreRepositoryImpl) bind DataStoreRepository::class

    factoryOf(::CheckInitialDatabaseUseCase)
    factoryOf(::SaveInitialDatabaseUseCase)
    factoryOf(::GetBalanceVisibleStatusUseCase)
    factoryOf(::SaveBalanceVisibleStatusUseCase)
    factoryOf(::GetSelectedNetworkUseCase)
    factoryOf(::SaveSelectedNetworkUseCase)
    factoryOf(::CheckOnboardingCompletedUseCase)
    factoryOf(::CompleteOnboardingUseCase)

    singleOf(::CoingeckoRepositoryImpl) bind CoingeckoRepository::class
    singleOf(::BlockchainRepositoryImpl) bind BlockchainRepository::class
    singleOf(::CoinRepositoryImpl) bind CoinRepository::class
    singleOf(::TokenRepositoryImpl) bind TokenRepository::class
    singleOf(::TokenPriceRepositoryImpl) bind TokenPriceRepository::class
    singleOf(::TokenHistoricalPriceRepositoryImpl) bind TokenHistoricalPriceRepository::class
    singleOf(::LanguageRepositoryImpl) bind LanguageRepository::class
    singleOf(::CurrencyRepositoryImpl) bind CurrencyRepository::class
    singleOf(::DAppRepositoryImpl) bind DAppRepository::class
    singleOf(::TransactionRepositoryImpl) bind TransactionRepository::class

    factory {
        CreateWalletUseCase(get(), get(), get(), getAll())
    }
    factoryOf(::GetAllWalletsUseCase)
    factory {
        RestoreWalletUseCase(get(), get(), get(), getAll())
    }
    factoryOf(::GetSelectedWalletAccountsUseCase)
    factoryOf(::MapAccountToAccountBlockchainUseCase)

    factoryOf(::GetMarketTickerUseCase)
//    factory { GetEtherBalanceUseCase(get()) }

    factoryOf(::DeleteBlockchainUseCase)
    factoryOf(::CreateBlockchainUseCase)
    factoryOf(::GetAllBlockchainUseCase)
    factoryOf(::GetBlockchainByUidUseCase)
    factoryOf(::CountBlockchainUseCase)
    factoryOf(::GetBlockchainExplorerLinkUseCase)

    factoryOf(::DeleteCoinUseCase)
    factoryOf(::DeleteCoinUidUseCase)
    factoryOf(::GetAllCoinUseCase)
    factoryOf(::GetCoinByUidUseCase)
    factoryOf(::CreateCoinUseCase)

    factoryOf(::FetchHistoricalTokenPriceUseCase)
    factoryOf(::DeleteTokenByIdUseCase)
    factoryOf(::GetTokenByIdUseCase)
    factoryOf(::GetTokenByIdAndBlockchainIdUseCase)
    factoryOf(::GetTokenByBlockchainUidUseCase)
    factoryOf(::CreateTokenUseCase)
    factoryOf(::GetTokenByReferenceUseCase)

    factoryOf(::DeleteTokenBalanceUseCase)
    factoryOf(::GetTokenBalanceByTokenIdUseCase)
    factoryOf(::CreateTokenBalanceUseCase)
    factoryOf(::UpdateTokenBalanceUseCase)

    factoryOf(::DeleteTokenPriceUseCase)
    factoryOf(::GetNativeCoinUseCase)
    factoryOf(::GetTokenPriceByCoinIdUseCase)
    factoryOf(::CreateTokenPriceUseCase)
    factoryOf(::UpdateTokenPriceUseCase)

    factoryOf(::GetAccountByIdUseCase)
    factoryOf(::GetAllAccountBalancesInEvmWalletUseCase)
    factoryOf(::GetAccountBalancesInEvmAccountUseCase)
    factoryOf(::GetAccountBalanceUseCase)
    factoryOf(::GetAllWalletsPortfolioUseCase)
    factoryOf(::FetchTokenPriceUseCase)
    factoryOf(::ScanTokenByChainNetworkUseCase)

    factoryOf(::GetSelectedWalletUseCase)
    factoryOf(::SelectWalletUseCase)
    factoryOf(::DeletedWalletUseCase)
    factoryOf(::SaveWalletNameUseCase)
    factoryOf(::GetWalletByIdUseCase)
    factoryOf(::GetWalletAccountsUseCase)
    factoryOf(::SetHiddenAccountUseCase)
    factoryOf(::BaseGetWalletAccountsUseCase)

    factoryOf(::ClearSecureStorageUseCase)
    factoryOf(::ClearDataStoreUseCase)
    factoryOf(::ClearWalletsUseCase)
    factoryOf(::ClearAccountsUseCase)
    factoryOf(::ClearEVMTransactionHistoryUseCase)
    factoryOf(::ClearTokenBalancesUseCase)
    factoryOf(::ResetWalletUseCase)

    factoryOf(::GetAllSupportedLanguageUseCase)
    factoryOf(::GetCurrentLanguageUseCase)
    factoryOf(::ChangeLanguageUseCase)
    factoryOf(::ChangeCurrencyUseCase)
    factoryOf(::GetCurrentCurrencyCodeUseCase)
    factoryOf(::GetDAppsByCategoriesUseCase)
    factoryOf(::DeleteDAppUseCase)
    factoryOf(::GetDAppFlowUseCase)
    factoryOf(::GetDAppsBySingleCategoryUseCase)
    factoryOf(::GetDAppUseCase)
    factoryOf(::GetListDAppFlowUseCase)
    factoryOf(::GetListDAppUseCase)
    factoryOf(::GetListOfCategoriesUseCase)
    factoryOf(::SaveDAppUseCase)
    factoryOf(::GetDappsJsonUseCase)

    factoryOf(::GetTransactionByTxHashUseCase)
    factoryOf(::GetTransactionHistoryUseCase)
    factoryOf(::SaveTransactionHistoryUseCase)
    
    singleOf(::PortfolioRepositoryImpl) bind PortfolioRepository::class

    factory { CreatePortfolioUseCase(get(), getAll()) }
    factoryOf(::AddAddressToPortfolioUseCase)
    factoryOf(::EnsureAccountInPortfolioUseCase)
    factoryOf(::GetPortfolioBalanceUseCase)
    factoryOf(::SyncPortfolioDataUseCase)
    factoryOf(::GetPortfolioByAccountUseCase)
    
    factory<AccountCreator> { PortfolioAccountCreator(get(), get()) }
    
    // Portfolio Error Handler
    singleOf(::PortfolioErrorHandler)

    single { PortfolioNetworkMapper }
    factory<AccountCreator> { PortfolioAccountCreator(get(), get()) }
}