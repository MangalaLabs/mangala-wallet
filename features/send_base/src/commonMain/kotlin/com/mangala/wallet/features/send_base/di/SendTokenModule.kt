package com.mangala.wallet.features.send_base.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionPlugin
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandlerPlugin
import com.mangala.wallet.features.send_base.conversation.WalletFunctions
import com.mangala.wallet.features.send_base.conversation.WalletFunctionConfirmationRendererPlugin
import com.mangala.wallet.features.send_base.conversation.WalletFunctionHandlers
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererPlugin
import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.features.send_base.conversation.SelectAssetForTransactionConfirmationRenderer
import com.mangala.wallet.features.send_base.conversation.SendConfirmationRendererPlugin
import com.mangala.wallet.features.send_base.pickaccount.ReceiveTokenPickAccountScreen
import com.mangala.wallet.features.send_base.pickaccount.ReceiveTokenPickAccountScreenModel
import com.mangala.wallet.features.send_base.selectcontactaddress.SelectContactAddressScreen
import com.mangala.wallet.features.send_base.selectcontactaddress.SelectContactAddressScreenModel
import com.mangala.wallet.features.send_base.selectrecipienttype.SelectRecipientTypeScreen
import com.mangala.wallet.features.send_base.selectrecipienttype.SelectRecipientTypeScreenModel
import com.mangala.wallet.features.send_base.sendcontact.SendContactListScreen
import com.mangala.wallet.features.send_base.sendcontact.SendContactListScreenModel
import com.mangala.wallet.features.send_base.step2.Step2SelectNetworkScreen
import com.mangala.wallet.features.send_base.step2.Step2SelectNetworkScreenModel
import com.mangala.wallet.features.send_base.step3.Step3SelectAmountScreen
import com.mangala.wallet.features.send_base.step3.Step3SelectAmountScreenModel
import com.mangala.wallet.features.send_base.step5.Step5SendSuccessScreen
import com.mangala.wallet.features.send_base.step5.Step5SendSuccessScreenModel
import com.mangala.wallet.features.send_base.transactionfee.TransactionFeeScreenModel
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sendTokenModule = module {

    factory {(networkType: NetworkType) ->
        ReceiveTokenPickAccountScreenModel(
            networkType = networkType,
            getCurrentCurrencyCodeUseCase = get(),
            getSelectedWalletAccountsUseCase = get(),
            getAccountBalanceUseCase = get(),
            getNativeCoinUseCase = get(),
            getRamPriceUseCase = get(),
            getAccountWithBalanceInfoUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getRexBalanceInNativeCoinUseCase = get(),
            fetchTokenPriceUseCase = get(),
            getAntelopeAccountTokenBalanceUseCase = get(),
            getAntelopeAccountsUseCase = get()
        )
    }

    factory { (accountId: String) ->
        SelectRecipientTypeScreenModel(
            accountId = accountId,
            getAccountByIdUseCase = get(),
            getSelectedNetworkUseCase = get()
        )
    }

    factory { (address: String?, networkType: NetworkType) ->
        Step2SelectNetworkScreenModel(
            address = address,
            networkType = networkType,
            createContactUseCase = get(),
            antelopeValidateAccountUseCase = get(),
            checkAccountNotExistsUseCase = get(),
            buildEnvironmentProvider = get(),
            isValidBitcoinAddressUseCase = get(),
            parseQRCodeResultUseCase = get()
        )
    }

    factory { (accountId: String, contactId: Long?, recipientAddress: String?, blockchainUid: String?, amount: String?) ->
        Step3SelectAmountScreenModel(
            accountId = accountId,
            contactId = contactId,
            recipientAddress = recipientAddress,
            blockchainUid = blockchainUid,
            initialAmount = amount,
            getCurrentCurrencyCodeUseCase = get(),
            getContactUseCase = get(),
            getAccountBalanceUseCase = get(),
            getSelectedWalletAccountsUseCase = get(),
            fetchTokenPriceUseCase = get(),
            getAntelopeAccountsUseCase = get(),
            getNativeCoinUseCase = get(),
            getAntelopeAccountCryptoBalanceUseCase = get(),
            getSelectedWalletBitcoinAccountsUseCase = get(),
            getAccountBalancesInBitcoinAccountUseCase = get()
        )
    }

    factory { (txHash: String, blockchainUid: String) ->
        Step5SendSuccessScreenModel(
            txHash = txHash,
            blockchainUid = blockchainUid,
            getBlockchainExplorerLinkUseCase = get()
        )
    }
    factory { (transactionFeeOptions: List<EvmFeeOptionUiModel>) ->
        TransactionFeeScreenModel(transactionFeeOptions)
    }

    factory { (contactId: String, accountId: String) ->
        SelectContactAddressScreenModel(
            contactId = contactId,
            accountId = accountId,
            contactRepository = get(),
            walletAddressRepository = get(),
            blockchainRepository = get()
        )
    }

    factory { (accountId: String) ->
        SendContactListScreenModel(
            accountId = accountId,
            getAllContactsUseCase = get(),
            countWalletAddressesForContactUseCase = get(),
            getWalletAddressesForContactUseCase = get(),
        )
    }
    
    factory { (address: String?, networkType: NetworkType) ->
        Step2SelectNetworkScreenModel(
            address = address,
            networkType = networkType,
            createContactUseCase = get(),
            antelopeValidateAccountUseCase = get(),
            checkAccountNotExistsUseCase = get(),
            buildEnvironmentProvider = get(),
            isValidBitcoinAddressUseCase = get(),
            parseQRCodeResultUseCase = get()
        )
    }

    single<ConfirmationRendererPlugin>(named("SendConfirmationRenderer")) { SendConfirmationRendererPlugin() }
    single<FunctionPlugin>(named("WalletFunctions")) { WalletFunctions() }
    single<ConfirmationRendererPlugin>(named("WalletConfirmationRenderer")) { WalletFunctionConfirmationRendererPlugin() }
    single<FunctionHandlerPlugin>(named("WalletFunctionHandler")) { WalletFunctionHandlers(get()) }
}

inline operator fun <reified T> ParametersHolder.component6(): T = elementAt(5, T::class)

val sendTokenScreenModule = screenModule {
    register<SharedScreen.SelectRecipientTypeScreen> {
        SelectRecipientTypeScreen(it.accountId, it.networkType)
    }
    register<SharedScreen.Step2SelectNetwork> {
        Step2SelectNetworkScreen(
            accountId = it.accountId,
            address = it.address,
            networkType = it.networkType
        )
    }
    register<SharedScreen.Step3SelectAmountScreen> { provider ->
        Step3SelectAmountScreen(
            accountId = provider.accountId,
            contactId = provider.contactId,
            receivingAddress = provider.address,
            blockchainUid = provider.blockchainUid,
            amount = provider.amount
        )
    }

    register<SharedScreen.Step5SendSuccessScreen> { provider ->
        Step5SendSuccessScreen(
            txHash = provider.txHash,
            blockchainUid = provider.blockchainUid
        )
    }

    register<SharedScreen.SendContactListScreen> { provider ->
        SendContactListScreen(
            accountId = provider.accountId
        )
    }

    register<SharedScreen.SelectContactAddressScreen> { provider ->
        SelectContactAddressScreen(
            contactId = provider.contactId,
            accountId = provider.accountId
        )
    }
}

val receiveTokenScreenModule = screenModule {

    register<SharedScreen.ReceiveTokenPickAccountScreen> {
        ReceiveTokenPickAccountScreen(it.onClickAccountInfo, it.networkType)
    }
}

val sendFeatureModule = module {
}