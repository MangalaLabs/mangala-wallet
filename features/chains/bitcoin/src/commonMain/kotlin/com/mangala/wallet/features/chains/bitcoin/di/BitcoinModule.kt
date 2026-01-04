package com.mangala.wallet.features.chains.bitcoin.di

import org.koin.dsl.module
import cafe.adriel.voyager.core.registry.screenModule
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.LoggerConfig
import co.touchlab.kermit.Severity
import com.mangala.wallet.core.ai.domain.AddressValidator
import com.mangala.wallet.domain.wallet.usecases.account.AccountCreator
import com.mangala.wallet.features.chains.bitcoin.data.local.bitcoinDatabaseModule
import com.mangala.wallet.features.chains.bitcoin.data.local.account.BitcoinAccountLocalDataSource
import com.mangala.wallet.features.chains.bitcoin.data.local.account.BitcoinAccountLocalDataSourceImpl
import com.mangala.wallet.features.chains.bitcoin.data.local.balance.BitcoinBalanceLocalDataSource
import com.mangala.wallet.features.chains.bitcoin.data.local.balance.BitcoinBalanceLocalDataSourceImpl
import com.mangala.wallet.features.chains.bitcoin.data.local.transaction.BitcoinTransactionLocalDataSource
import com.mangala.wallet.features.chains.bitcoin.data.local.transaction.BitcoinTransactionLocalDataSourceImpl
import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.MempoolRemoteDataSource
import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.createMempoolApi
import com.mangala.wallet.features.chains.bitcoin.data.remote.electrum.ElectrumConnectionManager
import com.mangala.wallet.features.chains.bitcoin.data.remote.electrum.ElectrumManager
import com.mangala.wallet.features.chains.bitcoin.data.repository.account.BitcoinAccountRepositoryImpl
import com.mangala.wallet.features.chains.bitcoin.data.repository.balance.BitcoinBalanceRepositoryImpl
import com.mangala.wallet.features.chains.bitcoin.data.repository.electrum.ElectrumBalanceManager
import com.mangala.wallet.features.chains.bitcoin.data.repository.electrum.ElectrumRepositoryImpl
import com.mangala.wallet.features.chains.bitcoin.data.repository.fee.BitcoinFeeRepositoryImpl
import com.mangala.wallet.features.chains.bitcoin.data.repository.transaction.BitcoinTransactionRepositoryImpl
import com.mangala.wallet.features.chains.bitcoin.data.repository.utxo.BitcoinUtxoRepositoryImpl
import com.mangala.wallet.features.chains.bitcoin.domain.repository.account.BitcoinAccountRepository
import com.mangala.wallet.features.chains.bitcoin.domain.repository.balance.BitcoinBalanceRepository
import com.mangala.wallet.features.chains.bitcoin.domain.repository.electrum.ElectrumRepository
import com.mangala.wallet.features.chains.bitcoin.domain.repository.fee.BitcoinFeeRepository
import com.mangala.wallet.features.chains.bitcoin.domain.repository.transaction.BitcoinTransactionRepository
import com.mangala.wallet.features.chains.bitcoin.domain.repository.utxo.BitcoinUtxoRepository
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.CreateBitcoinAccountUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetAccountBalancesInBitcoinAccountUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetAccountTokenBalanceUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetBitcoinAccountUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetSelectedWalletBitcoinAccountsUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.address.IsValidBitcoinAddressUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.utils.BitcoinAddressValidator
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.balance.GetBitcoinBalanceUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.electrum.GetElectrumBalanceUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.electrum.GetElectrumUtxosUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.electrum.GetElectrumWalletInfoUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.fee.GetBitcoinFeeRatesUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.transaction.GetBitcoinTransactionHistoryUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.transaction.SendBitcoinTransactionUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.utxo.GetBitcoinAddressUtxoUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.utxo.GetBitcoinWalletUtxosUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.reset.ClearBitcoinDataUseCaseImpl
import com.mangala.wallet.features.chains.bitcoin.presentation.BitcoinTestScreen
import com.mangala.wallet.features.chains.bitcoin.presentation.BitcoinTestScreenModel
import com.mangala.wallet.domain.reset.usecases.ClearBitcoinDataUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import com.mangala.wallet.remote.di.provideKtorfit
import com.mangala.wallet.ui.SharedScreen
import fr.acinq.lightning.logging.LoggerFactory
import fr.acinq.lightning.io.TcpSocket.Builder
import org.koin.core.qualifier.named

val bitcoinModule = module {
    includes(bitcoinDatabaseModule())

    single {
        provideKtorfit(
            baseUrl = "https://mempool.space/",
            enableNetworkLogs = true,
            username = "",
            password = "",
            forceJsonBody = false,
            httpClientEngine = get()
        ).createMempoolApi()
    }

    factory { MempoolRemoteDataSource(get()) }

    single<BitcoinAccountLocalDataSource> { BitcoinAccountLocalDataSourceImpl(get()) }
    single<BitcoinTransactionLocalDataSource> { BitcoinTransactionLocalDataSourceImpl(get()) }
    single<BitcoinBalanceLocalDataSource> { BitcoinBalanceLocalDataSourceImpl(get()) }
    
    single<LoggerFactory> {
        LoggerFactory(config = object : LoggerConfig {
            override val logWriterList: List<LogWriter> = listOf(object : LogWriter() {
                override fun log(
                    severity: Severity,
                    message: String,
                    tag: String,
                    throwable: Throwable?
                ) {
                    println(it)
                }
            })
            override val minSeverity: Severity = Severity.Info
        })
    }
    single {
        ElectrumManager(get(), socketBuilder = {
            Builder()
        })
    }
    single<ElectrumConnectionManager> {
        ElectrumConnectionManager(
            get()
        )
    }
    single<ElectrumBalanceManager> { ElectrumBalanceManager(get()) }
    
    factory<ElectrumRepository> { ElectrumRepositoryImpl(get(), get(), get(), get()) }
    factory<BitcoinAccountRepository> { BitcoinAccountRepositoryImpl(get()) }
    factory<BitcoinBalanceRepository> { BitcoinBalanceRepositoryImpl(get(), get()) }
    factory<BitcoinUtxoRepository> { BitcoinUtxoRepositoryImpl(get()) }
    factory<BitcoinTransactionRepository> { 
        BitcoinTransactionRepositoryImpl(
            mempoolRemoteDataSource = get(),
            bitcoinTransactionLocalDataSource = get(),
            transactionMetadataLocalDataSource = get(),
            remoteKeyLocalDataSource = get()
        ) 
    }
    factory<BitcoinFeeRepository> { BitcoinFeeRepositoryImpl(get()) }

    // Use cases
    factory<AccountCreator> { CreateBitcoinAccountUseCase(get(), get(), get()) }
    factory { GetBitcoinBalanceUseCase(get()) }
    factory { GetBitcoinAddressUtxoUseCase(get()) }
    factory { SendBitcoinTransactionUseCase(get(), get(), get(), get()) }
    factory { GetSelectedWalletBitcoinAccountsUseCase(get(), get(), get()) }
    factory { GetAccountBalancesInBitcoinAccountUseCase(get(), get(), get()) }
    factory { GetAccountTokenBalanceUseCase(get(), get(), get(), get()) }
    factory { IsValidBitcoinAddressUseCase() }
    single<AddressValidator>(named("BitcoinAddressValidator")) { BitcoinAddressValidator() }
    factory { GetBitcoinAccountUseCase(get()) }
    factory { GetBitcoinWalletUtxosUseCase(get(), get()) }
    factory { GetElectrumBalanceUseCase(get()) }
    factory { GetElectrumUtxosUseCase(get(), get()) }
    factory { GetElectrumWalletInfoUseCase(get()) }
    
    // Transaction-related use cases
    factory { GetBitcoinTransactionHistoryUseCase(get()) }
    
    // Fee-related use cases
    factory { GetBitcoinFeeRatesUseCase(get()) }

    // Reset use cases
    factoryOf(::ClearBitcoinDataUseCaseImpl) bind ClearBitcoinDataUseCase::class

    factory { BitcoinTestScreenModel(get(), get(), get(), get(), get()) }
}

val bitcoinScreenModule = screenModule {
    register<SharedScreen.BitcoinTestScreen> {
        BitcoinTestScreen()
    }
}