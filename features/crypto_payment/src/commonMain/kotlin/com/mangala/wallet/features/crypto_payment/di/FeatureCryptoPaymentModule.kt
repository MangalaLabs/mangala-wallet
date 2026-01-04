package com.mangala.wallet.features.crypto_payment.di

import cafe.adriel.voyager.core.registry.screenModule
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.features.crypto_payment.data.remote.account.paywithcrypto.EosAccountApiFactory
import com.mangala.wallet.features.crypto_payment.data.remote.account.paywithcrypto.EosAccountDataSource
import com.mangala.wallet.features.crypto_payment.domain.repository.account.paywithcrypto.EosAccountRepository
import com.mangala.wallet.features.crypto_payment.domain.repository.account.paywithcrypto.EosAccountRepositoryImpl
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.ApproveAllowanceUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.CreateEosAccountUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.GetCryptoPaymentContractAddressUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.GetTokenAllowanceUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.GetTokenSupportedListUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.GetTokenSupportedUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.PayEosAccountByEvmUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.SendSignMessageCreateAccountUseCase
import com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto.SignAndSendTransactionDataUseCase
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.AllowanceScreen
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.AllowanceScreenModel
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.ChangeNetworkForPaymentScreen
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.ChangeNetworkForPaymentScreenModel
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.CryptoPaymentErrorScreen
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.CryptoPaymentErrorScreenModel
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.PayWithCryptoScreen
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.PayWithCryptoScreenModel
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.PaymentDetailScreen
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.SelectAccountBottomSheetScreen
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.SelectNetworkBottomSheetScreen
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.SelectPaymentMethodScreen
import com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto.SelectWalletBottomSheetScreen
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.utils.di.IGNORE_UNKNOWN_KEY_JSON
import org.koin.core.qualifier.named
import org.koin.dsl.module

val featureCryptoPaymentModule = module {
    factory {
        SignAndSendTransactionDataUseCase(
            getSelectedWalletUseCase = get(),
            generateHDKeyUseCase = get(),
            getNonceUseCase = get(),
            sendRawTransactionUseCase = get(),
            parsingJson = get(named(IGNORE_UNKNOWN_KEY_JSON))
        )
    }
    factory { GetTokenSupportedListUseCase(get(), get()) }
    factory { GetTokenSupportedUseCase(get(), get()) }
    single { EosAccountDataSource(get()) }
    factory<EosAccountRepository> { EosAccountRepositoryImpl(get()) }
    factory { SendSignMessageCreateAccountUseCase(get()) }
    factory { CreateEosAccountUseCase(get()) }
    factory { PayEosAccountByEvmUseCase(get(), get()) }
    factory { GetTokenAllowanceUseCase(get()) }
    factory { ApproveAllowanceUseCase(get()) }
    single { EosAccountApiFactory(get()) }
    factory { (blockchainType: BlockchainType) -> get<EosAccountApiFactory>().createEosAccountApi(blockchainType) }
    single { GetCryptoPaymentContractAddressUseCase() }


    // create eos account via evm
    factory {
            (paidAccountId: String, accountBlockchainTypeUid: String, eosOwnerPrivateKey: String?, eosActivePrivateKey: String?) ->
        PayWithCryptoScreenModel(
            paidAccountId = paidAccountId,
            accountBlockchainTypeUid = accountBlockchainTypeUid,
            eosOwnerPrivateKey = eosOwnerPrivateKey,
            eosActivePrivateKey = eosActivePrivateKey,
            getTokenSupportedListUseCase = get(),
            fetchTokenPriceUseCase = get(),
            getNativeCoinUseCase = get(),
            getTransactionFeeOptionsUseCase = get(),
            getTokenBalanceByTokenIdUseCase = get(),
            getRecommendedGasPriceUseCase = get(),
            getCurrentCurrencyCodeUseCase = get(),
            getLatestBlockUseCase = get(),
            getAccountBalanceUseCase = get(),
            getRamPriceUseCase = get(),
            sendSignMessageCreateAccountUseCase = get(),
            payEosAccountByEvmUseCase = get(),
            createEosAccountUseCase = get(),
            generateAccountKeyPairsUseCase = get(),
            getWalletAccountsUseCase = get(),
            getSelectedWalletUseCase = get(),
            estimateGasUseCase = get(),
            getTokenByIdUseCase = get(),
            getTokenAllowanceUseCase = get(),
            approveAllowanceUseCase = get(),
            saveAccountUseCase = get(),
            getAccountByIdUseCase = get(),
            getSelectedNetworkUseCase = get(),
            saveSelectedNetworkUseCase = get(),
            appLifecycleObserver = get(),
            getCryptoPaymentContractAddressUseCase = get(),
            buildEnvironmentProvider = get()
        )
    }
    factory {
        ChangeNetworkForPaymentScreenModel(
            saveSelectedNetworkUseCase = get(),
            getAllWalletsUseCase = get(),
            selectWalletUseCase = get(),
            getSelectedWalletAccountsUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getAccountBalanceUseCase = get(),
            buildEnvironmentProvider = get(),
            getNativeCoinUseCase = get()
        )
    }

    factory {
        CryptoPaymentErrorScreenModel(get(), get())
    }

    factory {(minimumAllowanceRequired: BigDecimal, paidAccountId: String) ->
        AllowanceScreenModel(
            minimumAllowanceRequired = minimumAllowanceRequired,
            paidAccountId = paidAccountId,
            fetchTokenPriceUseCase = get (),
            getNativeCoinUseCase = get(),
            getTransactionFeeOptionsUseCase = get(),
            getTokenBalanceByTokenIdUseCase = get(),
            getRecommendedGasPriceUseCase = get(),
            getCurrentCurrencyCodeUseCase = get(),
            getLatestBlockUseCase = get(),
            getTokenByIdUseCase = get(),
            approveAllowanceUseCase = get(),
            getCryptoPaymentContractAddressUseCase = get(),
            estimateGasUseCase = get(),
            getAccountByIdUseCase = get()
        )
    }
}

val featureCryptoPaymentScreenModule = screenModule {
    register<SharedScreen.PayWithCryptoScreen> {
        PayWithCryptoScreen(
            accountName = it.accountName,
            paidAccountId = it.paidAccountId,
            accountBlockchainTypeUid = it.accountBlockchainTypeUid,
            accountNameType = it.accountNameType,
            eosOwnerPrivateKey = it.eosOwnerPrivateKey,
            eosActivePrivateKey = it.eosActivePrivateKey,
        )
    }

    register<SharedScreen.ChangeNetworkForPaymentScreen> {
        ChangeNetworkForPaymentScreen(
            accountName = it.accountName,
            accountNameType = it.accountNameType,
            eosOwnerPrivateKey = it.eosOwnerPrivateKey,
            eosActivePrivateKey = it.eosActivePrivateKey,
        )
    }

    register<SharedScreen.SelectNetworkBottomSheetScreen> {
        SelectNetworkBottomSheetScreen(
            networks = it.networks,
            onContinue = it.onContinue,
            selectedNetwork = it.selectedNetwork,
            onDismiss = it.onDismiss
        )
    }

    register<SharedScreen.SelectWalletBottomSheetScreen> {
        SelectWalletBottomSheetScreen(
            wallets = it.networks,
            onContinue = it.onContinue,
            selectedWallet = it.selectedNetwork,
            onDismiss = it.onDismiss
        )
    }

    register<SharedScreen.SelectAccountBottomSheetScreen> {
        SelectAccountBottomSheetScreen(
            accounts = it.accounts,
            onContinue = it.onContinue,
            selectedAccount = it.selectedAccount,
            onDismiss = it.onDismiss
        )
    }

    register<SharedScreen.PaymentDetailScreen> {
        PaymentDetailScreen(
            cpu = it.cpu,
            net = it.net,
            ram = it.ram,
            serviceFee = it.serviceFee,
            totalEos = it.totalEos,
            onDismiss = it.onDismiss,
            coinUid = it.coinUid
        )
    }

    register<SharedScreen.CryptoPaymentErrorScreen> {
        CryptoPaymentErrorScreen(
            error = it.error,
            errorDescription = it.errorDescription,
            blockchainTypeUid = it.blockchainTypeUid
        )
    }

    register<SharedScreen.AllowanceScreen> {
        AllowanceScreen(
            paidAccountId = it.paidAccountId,
            token = it.token,
            minimumAllowance = it.minimumAllowance,
            onCallback = it.onCallback,
            onDismiss = it.onDismiss,
        )
    }

    register<SharedScreen.SelectPaymentMethodScreen> {
        SelectPaymentMethodScreen(
            paymentMethods = it.paymentMethods,
            onSelectedPaymentMethod = it.onSelectedPaymentMethod,
            onDismiss = it.onDismiss
        )
    }
}