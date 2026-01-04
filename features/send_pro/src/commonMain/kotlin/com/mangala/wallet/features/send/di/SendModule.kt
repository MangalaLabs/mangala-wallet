package com.mangala.wallet.features.send.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.send.presentation.step4.antelope.AntelopeStep4VerifyAndSendScreen
import com.mangala.wallet.features.send.presentation.step4.antelope.AntelopeStep4VerifyAndSendScreenModel
import com.mangala.wallet.features.send.presentation.step4.bitcoin.BitcoinStep4VerifyAndSendScreen
import com.mangala.wallet.features.send.presentation.step4.bitcoin.BitcoinStep4VerifyAndSendScreenModel
import com.mangala.wallet.features.send.presentation.step4.evm.EvmStep4VerifyAndSendScreen
import com.mangala.wallet.features.send.presentation.step4.evm.EvmStep4VerifyAndSendScreenModel
import com.mangala.wallet.features.send_base.di.component6
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.parameter.ParametersHolder
import org.koin.dsl.module

val sendModule = module {
    factory { (contactId: Long?, blockchainUid: String, tokenId: Long, recipientAddress: String, amount: String, accountId: String) ->
        EvmStep4VerifyAndSendScreenModel(
            contactId = contactId,
            blockchainUid = blockchainUid,
            tokenId = tokenId,
            recipientAddress = recipientAddress,
            amount = amount,
            accountId = accountId,
            getCurrentCurrencyCodeUseCase = get(),
            getAccountByIdUseCase = get(),
            fetchTokenPriceUseCase = get(),
            getSelectedCurrencyCodeUseCase = get(),
            getTransactionFeeOptionsUseCase = get(),
            getContactUseCase = get(),
            getTokenBalanceByTokenIdUseCase = get(),
            getRecommendedGasPriceUseCase = get(),
            sendTokenUseCase = get(),
            estimateGasUseCase = get(),
            getNativeCoinUseCase = get(),
            getLatestBlockUseCase = get(),
            getSelectedWalletUseCase = get(),
            getNonceUseCase = get(),
        )
    }
    factory { (contactId: Long?, senderAccount: String, toAccount: String, blockchainUid: String?, tokenKey: String, amount: String, memo: String) ->
        AntelopeStep4VerifyAndSendScreenModel(
            contactId = contactId,
            senderAccount = senderAccount,
            toAccount = toAccount,
            blockchainUid = blockchainUid.orEmpty(),
            tokenKey = tokenKey,
            amount = amount,
            memo = memo,
            transactUseCase = get(),
            getContactByIdUseCase = get(),
            getAntelopeAccountTokenBalanceUseCase = get(),
            getSelectedCurrencyCodeUseCase = get(),
            getNativeCoinUseCase = get(),
            fetchTokenPriceUseCase = get()
        )
    }
    factory { (contactId: Long?, blockchainUid: String, tokenId: String, recipientAddress: String, amount: String, accountId: String) ->
        BitcoinStep4VerifyAndSendScreenModel(
            contactId = contactId,
            blockchainUid = blockchainUid,
            tokenId = tokenId,
            recipientAddress = recipientAddress,
            amount = amount,
            accountId = accountId,
            getAccountByIdUseCase = get(),
            getContactByIdUseCase = get(),
            getTokenBalanceByTokenIdUseCase = get(),
            getCurrentCurrencyCodeUseCase = get(),
            getNativeCoinUseCase = get(),
            fetchTokenPriceUseCase = get(),
            getSelectedWalletUseCase = get(),
            getBitcoinWalletUtxosUseCase = get(),
            sendBitcoinTransactionUseCase = get(),
            getBitcoinFeeRatesUseCase = get(),
            getAccountBalancesInBitcoinAccountUseCase = get(),
            getBitcoinAccountUseCase = get()
        )
    }
}

val sendScreenModule = screenModule {
    register<SharedScreen.Step4EvmVerifyAndSendScreen> { provider ->
        EvmStep4VerifyAndSendScreen(
            provider.contactId,
            provider.address,
            provider.blockchainUid,
            provider.tokenId,
            provider.amount,
            provider.accountId
        )
    }

    register<SharedScreen.Step4AntelopeVerifyAndSendScreen> {
        AntelopeStep4VerifyAndSendScreen(
            it.contactId,
            it.senderAccount,
            it.toAccount,
            it.blockchainUid,
            it.tokenSymbol,
            it.amount,
            it.memo
        )
    }

    register<SharedScreen.Step4BitcoinVerifyAndSendScreen> {
        BitcoinStep4VerifyAndSendScreen(
            it.contactId,
            it.address,
            it.blockchainUid,
            it.tokenId,
            it.amount,
            it.accountId
        )
    }
}

inline operator fun <reified T> ParametersHolder.component6(): T = elementAt(5, T::class)
inline operator fun <reified T> ParametersHolder.component7(): T = elementAt(6, T::class)