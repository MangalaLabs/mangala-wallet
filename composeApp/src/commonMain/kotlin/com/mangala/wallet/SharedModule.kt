package com.mangala.wallet

import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.linh.antelope_qr.di.antelopeQrModule
import com.mangala.antelope.base.di.antelopeRpcModule
import com.mangala.antelope.base.di.bloksRpcModule
import com.mangala.wallet.features.chains.antelope_base.di.ramChartModule
import com.mangala.antelope.base.di.eosRpcModule
import com.mangala.browser.di.browserBridgeModule
import com.mangala.browser.di.browserBridgeScreenModule
import com.mangala.browser_bridge_base.di.browserBridgeBaseModule
import com.mangala.browser_bridge_base.di.browserBridgePlatformSpecificModule
import com.mangala.eticket.di.eTicketDatabaseModule
import com.mangala.eticket.di.eTicketModule
import com.mangala.eticket.di.eTicketRemoteModule
import com.mangala.eticket.di.eTicketScreenModule
import com.mangala.features.browser.di.browserTabCommonModule
import com.mangala.features.browser.di.browserTabModule
import com.mangala.features.wallet.di.walletTabModule
import com.mangala.features.wallet.di.walletTabScreenModule
import com.mangala.wallet.antelope_key_manager.di.antelopeKeyManagerModule
import com.mangala.wallet.auth.di.authCleanModule
import com.mangala.wallet.auth.di.authScreenModule
import com.mangala.wallet.biometry.di.biometryModule
import com.mangala.wallet.biometry.di.biometryScreenModule
import com.mangala.wallet.core.address.di.addressModule
import com.mangala.wallet.core.hdwallet.domain.di.hdWalletModule
import com.mangala.wallet.core.notification.di.coreNotificationModulePlatformSpecific
import com.mangala.wallet.domain.di.dataStoreModule
import com.mangala.wallet.domain.di.domainModule
import com.mangala.wallet.features.chains.antelope.create_account.di.antelopeCreateAccountModule
import com.mangala.wallet.features.chains.antelope.create_account.di.antelopeCreateAccountScreenModule
import com.mangala.wallet.features.chains.antelope.di.antelopeModule
import com.mangala.wallet.features.chains.antelope.di.antelopeScreenModule
import com.mangala.wallet.features.chains.antelope.ram.di.antelopeRamModule
import com.mangala.wallet.features.chains.antelope.ram.di.antelopeRamScreenModule
import com.mangala.wallet.features.chains.antelope_base.data.local.antelopeDatabaseModule
import com.mangala.wallet.features.chains.antelope_base.di.featureAntelopeBaseCommonModule
import com.mangala.wallet.features.chains.antelope_base.di.featureAntelopeBasePlatformSpecificModule
import com.mangala.wallet.features.chains.bitcoin.di.bitcoinModule
import com.mangala.wallet.features.chains.bitcoin.di.bitcoinScreenModule
import com.mangala.wallet.features.chains.evmcompatible.di.evmCompatibleModule
import com.mangala.wallet.features.addressbook.di.addressBookModule
import com.mangala.wallet.features.addressbook.di.addressBookScreenModule
// import com.mangala.wallet.features.contacts.di.settingsContactModule // Replaced with addressbook
// import com.mangala.wallet.features.contacts.di.settingsContactScreenModule // Replaced with addressbook
import com.mangala.wallet.features.crypto_payment.di.featureCryptoPaymentModule
import com.mangala.wallet.features.crypto_payment.di.featureCryptoPaymentScreenModule
import com.mangala.wallet.features.evm_snap.di.featureEvmSnapModule
import com.mangala.wallet.features.evm_snap.di.featureEvmSnapScreenModule
import com.mangala.wallet.features.home.di.homeModule
import com.mangala.wallet.features.home.di.homeScreenModule
import com.mangala.wallet.features.home_base.di.homeBaseModule
import com.mangala.wallet.features.manageaccount.di.manageAccountModule
import com.mangala.wallet.features.manageaccount.di.manageAccountScreenModule
import com.mangala.wallet.features.menu.di.settingsMenuModule
import com.mangala.wallet.features.menu.di.settingsMenuScreenModule
import com.mangala.wallet.features.nft.di.nftModule
import com.mangala.wallet.features.nft.di.nftScreenModule
import com.mangala.wallet.features.nft_base.di.nftBaseModule
import com.mangala.wallet.features.nft_base.di.nftBaseScreenModule
import com.mangala.wallet.features.onboarding.di.onboardingScreenModule
import com.mangala.wallet.features.portfolio.di.portfolioModule
import com.mangala.wallet.features.portfolio.di.portfolioScreenModule
import com.mangala.wallet.features.receive.di.receiveModule
import com.mangala.wallet.features.receive.di.receiveScreenModule
import com.mangala.wallet.features.send.di.sendModule
import com.mangala.wallet.features.send.di.sendScreenModule
import com.mangala.wallet.features.send_base.di.receiveTokenScreenModule
import com.mangala.wallet.features.send_base.di.sendFeatureModule
import com.mangala.wallet.features.send_base.di.sendTokenModule
import com.mangala.wallet.features.send_base.di.sendTokenScreenModule
import com.mangala.wallet.features.settings.currency.di.currencyModule
import com.mangala.wallet.features.settings.currency.di.currencyScreenModule
import com.mangala.wallet.features.settings.network.di.settingsNetworkModule
import com.mangala.wallet.features.settings.network.di.settingsNetworkScreenModule
import com.mangala.wallet.features.swap.di.swapModule
import com.mangala.wallet.features.swap.di.swapScreenModule
import com.mangala.wallet.features.swap_base.di.swapBaseModule
import com.mangala.wallet.features.swap_base.di.swapBaseScreenModule
import com.mangala.wallet.features.transactionhistory.di.transactionHistoryModule
import com.mangala.wallet.features.transactionhistory.di.transactionHistoryScreenModule
import com.mangala.wallet.features.transactionqr.di.transactionQrModule
import com.mangala.wallet.features.transactionqr.di.transactionQrScreenModule
import com.mangala.wallet.features.wallet.di.walletModule
import com.mangala.wallet.features.wallet.di.walletScreenModule
import com.mangala.wallet.local.di.databaseModule
import com.mangala.wallet.local.di.localModule
import com.mangala.wallet.menu_base.di.menuBaseModule
import com.mangala.wallet.menu_base.di.menuBaseScreenModule
import com.mangala.wallet.pin.di.pinKoinModule
import com.mangala.wallet.pin.di.pinModule
import com.mangala.wallet.pin.di.pinScreenModule
import com.mangala.wallet.qrcode.di.qrCodeModule
import com.mangala.wallet.qrcode.di.qrCodeScreenModule
import com.mangala.wallet.remote.di.ktorHttpClientEngineFactoryModule
import com.mangala.wallet.remote.di.remoteModule
import com.mangala.wallet.scanqr.di.scanQRCodeModule
import com.mangala.wallet.ui.di.commonUiModule
//import com.mangala.wallet.ui.terms_and_policy.TermsAndPolicyScreen
import com.mangala.wallet.uniswap.di.uniswapModule
import com.mangala.wallet.utils.di.buildEnvironmentModule
import com.mangala.wallet.utils.di.commonUtilsModule
import com.mangala.wallet.utils.di.utilsModule
import com.mangala.wallet.viewmodel.ApplicationViewModel
import com.mangala.wallet.wallet.di.coreWalletModule
import com.mangala.wallet.wallet.di.coreWalletScreenModule
import com.mangala.wallet.walletconnect.di.walletConnectModule
import com.wallet.iap.purchases.di.iapCommonModule
import com.wallet.iap.purchases.di.openIapModule
import com.mangala.wallet.twofactorauth.di.twoFactorAuthModule
import com.mangala.wallet.twofactorauth.di.twoFactorAuthScreenModule
import com.mangala.wallet.core.ai.di.coreAiModule
import com.mangala.wallet.features.contacts.di.settingsContactModule
import com.mangala.wallet.features.contacts.di.settingsContactScreenModule
import com.mangala.wallet.features.conversationui.di.conversationUiModule
import com.mangala.wallet.features.conversationui.di.conversationUiScreenModule
import com.mangala.wallet.features.onboarding.di.onboardingModule
import com.mangala.wallet.passkey.di.passkeyCleanModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(
    enableNetworkLogs: Boolean = true,
    appDeclaration: KoinAppDeclaration = {}
) =
    startKoin {
        appDeclaration()
        modules(
            addressBookModule,
            addressModule,
            antelopeCreateAccountModule,
            antelopeDatabaseModule(),
            antelopeKeyManagerModule,
            antelopeModule,
            antelopeQrModule,
            antelopeRamModule,
            antelopeRpcModule(),
            authCleanModule,
            biometryModule(),
            bitcoinModule,
            bloksRpcModule,
            browserBridgeBaseModule,
            browserBridgeModule,
            browserBridgePlatformSpecificModule(),
            browserTabCommonModule(),
            browserTabModule(),
            buildEnvironmentModule(),
            commonUiModule(),
            commonUtilsModule(),
            conversationUiModule,
            coreAiModule,
            coreNotificationModulePlatformSpecific(),
            coreWalletModule,
            currencyModule,
            dataStoreModule(),
            databaseModule(),
            domainModule(),
            eTicketDatabaseModule(),
            eTicketModule(),
            eTicketRemoteModule(),
            eTicketRemoteModule(),
            eosRpcModule(),
            evmCompatibleModule(),
            featureAntelopeBaseCommonModule,
            featureAntelopeBasePlatformSpecificModule(),
            featureCryptoPaymentModule,
            featureEvmSnapModule,
            hdWalletModule(),
            homeBaseModule,
            homeModule,
            iapCommonModule,
            ktorHttpClientEngineFactoryModule(),
            localModule(),
            manageAccountModule,
            menuBaseModule,
            nftBaseModule,
            nftModule,
            openIapModule(),
            passkeyCleanModule("https://gateway.taman2h.fun"),
            pinKoinModule(),
            pinModule,
            portfolioModule,
            qrCodeModule,
            ramChartModule,
            receiveModule,
            remoteModule("https://api.coingecko.com/api/v3/", enableNetworkLogs),
            scanQRCodeModule(),
            sendFeatureModule,
            sendModule,
            sendTokenModule,
            settingsContactModule,
            settingsMenuModule,
            settingsNetworkModule,
            sharedModule,
            swapBaseModule,
            swapModule,
            transactionHistoryModule,
            transactionQrModule,
            twoFactorAuthModule,
            uniswapModule(),
            utilsModule(),
            walletModule,
            walletTabModule,
            onboardingModule,
//            contractWizardModule(),
        )
    }

val sharedModule = module {
    single {
        ApplicationViewModel(get(), get(), get(), get())
    }

    ScreenRegistry {
        // settingsContactScreenModule() // Replaced with addressbook
        addressBookScreenModule()
        antelopeCreateAccountScreenModule()
        antelopeRamScreenModule()
        antelopeScreenModule()
        authScreenModule()
        biometryScreenModule()
        bitcoinScreenModule()
        browserBridgeScreenModule()
        conversationUiScreenModule()
        coreWalletScreenModule()
        currencyScreenModule()
        eTicketScreenModule()
        featureCryptoPaymentScreenModule()
        featureEvmSnapScreenModule()
        homeScreenModule()
        manageAccountScreenModule()
        menuBaseScreenModule()
        nftBaseScreenModule()
        nftScreenModule()
        onboardingScreenModule()
        pinScreenModule()
        portfolioScreenModule()
        qrCodeScreenModule()
        receiveScreenModule()
        receiveTokenScreenModule()
        sendScreenModule()
        sendTokenScreenModule()
        settingsContactScreenModule()
        settingsMenuScreenModule()
        settingsNetworkScreenModule()
        swapBaseScreenModule()
        swapScreenModule()
        transactionHistoryScreenModule()
        transactionQrScreenModule()
        twoFactorAuthScreenModule()
        walletConnectModule()
        walletScreenModule()
        walletTabScreenModule()
    }
}

fun KoinApplication.Companion.start(): KoinApplication = initKoin { }
