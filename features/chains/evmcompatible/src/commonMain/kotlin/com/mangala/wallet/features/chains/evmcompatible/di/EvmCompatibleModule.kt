package com.mangala.wallet.features.chains.evmcompatible.di

import com.mangala.wallet.features.chains.BlockSyncer
import com.mangala.wallet.features.chains.evmcompatible.BuildKonfig
import com.mangala.wallet.features.chains.evmcompatible.data.remote.provider.infura.NodeApi
import com.mangala.wallet.features.chains.evmcompatible.data.remote.provider.infura.NodeRemoteDataSource
import com.mangala.wallet.features.chains.evmcompatible.domain.EvmBlockSyncer
import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository
import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepositoryImpl
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.AllowanceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.ApproveUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.CallNodeUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.ConstructTempLogEventsUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EncodeSignTransactionRequestUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EstimateGasUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetFeeHistoryUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetNonceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionReceiptUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.ParseNodeResponseUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendRawTransactionUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendSignedTransactionUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendTokenUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SignAndEncodeTransactionRequestUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SignAndSendTransactionDataUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SignPersonalMessageUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.qrcheckers.SignTransactionRequestQrCodeChecker
import com.mangala.wallet.features.chains.evmcompatible.domain.qrcheckers.SignedTransactionQrCodeChecker
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SignTransactionUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SwapTokenUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.utils.EvmAddressValidator
import com.mangala.wallet.qrcode.domain.qrcheckers.QrCodeTypeChecker
import com.mangala.wallet.remote.di.provideKtorfit
import com.mangala.wallet.utils.di.IGNORE_UNKNOWN_KEY_JSON
import com.mangala.wallet.utils.di.JSON
import com.mangala.wallet.core.ai.domain.AddressValidator
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun evmCompatibleModule() = module {
    single<NodeApi> {
        provideKtorfit(
            baseUrl = "https://mainnet.infura.io/v3/",
            enableNetworkLogs = true,
            username = BuildKonfig.INFURA_API_KEY,
            password = BuildKonfig.INFURA_SECRET_KEY,
            forceJsonBody = false,
            httpClientEngine = get()
        ).create()
    }
    single { NodeRemoteDataSource(get()) }
    single<BlockSyncer> { EvmBlockSyncer(
        nodeRepository = get(),
        transactionRepository = get(),
        scanTokenByChainNetworkUseCase = get(),
        getAccountByIdUseCase = get(),
        blockSyncerPlugins = getAll()
    ) }

    single<NodeRepository> {
        NodeRepositoryImpl(
            get(),
            get(named(IGNORE_UNKNOWN_KEY_JSON)),
            get(named(JSON))
        )
    }
    factory { GetFeeHistoryUseCase(get()) }
    factory { EstimateGasUseCase(
        nodeRepository = get(),
        parsingJson = get(named(IGNORE_UNKNOWN_KEY_JSON))
    ) }
    factory { GetNonceUseCase(get(), get(named(JSON))) }
    factory { GetRecommendedGasPriceUseCase(get(), get(), get(named(IGNORE_UNKNOWN_KEY_JSON))) }
    factory { GetGasPriceUseCase(get()) }
    factory { GetLatestBlockUseCase(get()) }
    factory { SendRawTransactionUseCase(get()) }
    factory { CallNodeUseCase(get()) }
    factory { AllowanceUseCase(get()) }
    factory { SendTokenUseCase(
        getSelectedWalletUseCase = get(),
        getAccountByIdUseCase = get(),
        generateHDKeyUseCase = get(),
        getNonceUseCase = get(),
        signAndSendTransactionDataUseCase = get(),
        saveTransactionHistoryUseCase = get(),
        blockSyncer = get(),
        parsingJson = get(named(IGNORE_UNKNOWN_KEY_JSON))
    ) }
    single { SignAndSendTransactionDataUseCase(
        getSelectedWalletUseCase = get(),
        generateHDKeyUseCase = get(),
        getNonceUseCase = get(),
        sendRawTransactionUseCase = get(),
        parsingJson = get(named(IGNORE_UNKNOWN_KEY_JSON))
    ) }
    factory { SwapTokenUseCase(get()) }
    factory { ApproveUseCase(get(), get()) }
    factory { SignTransactionUseCase(
        getNonceUseCase = get(),
        sendRawTransactionUseCase = get(),
        saveTransactionHistoryUseCase = get(),
        parseNodeResponseUseCase = get(),
        constructTempLogEventsUseCase = get(),
        blockSyncer = get(),
        parsingJson = get(named(IGNORE_UNKNOWN_KEY_JSON))
    ) }
    factory { GetTransactionFeeOptionsUseCase() }
    factory { SignPersonalMessageUseCase() }
    factory { ParseNodeResponseUseCase(get(named(IGNORE_UNKNOWN_KEY_JSON))) }
    factory { ConstructTempLogEventsUseCase() }
    factory { GetTransactionReceiptUseCase(get()) }
    factory { EncodeSignTransactionRequestUseCase(get(named(JSON))) }
    factory { SignAndEncodeTransactionRequestUseCase(
        getWalletByIdUseCase = get(),
        getAccountByIdUseCase = get(),
        generateHDKeyUseCase = get(),
        json = get(named(JSON))
    ) }
    factory {
        SendSignedTransactionUseCase(get(), get(named(JSON)))
    }

    single<QrCodeTypeChecker>(named("SignTransactionRequestQrCodeChecker")) { SignTransactionRequestQrCodeChecker(get()) }
    single<QrCodeTypeChecker>(named("SignedTransactionQrCodeChecker")) { SignedTransactionQrCodeChecker() }
    
    single<AddressValidator>(named("EvmAddressValidator")) { EvmAddressValidator() }
}