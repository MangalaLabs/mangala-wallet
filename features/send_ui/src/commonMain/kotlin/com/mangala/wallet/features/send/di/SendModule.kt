package com.mangala.wallet.features.send.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.chains.evmcompatible.model.SignedTransactionResponse
import com.mangala.wallet.features.send.presentation.sendsignedtransaction.SendSignedTransactionScreen
import com.mangala.wallet.features.send.presentation.sendsignedtransaction.SendSignedTransactionScreenModel
import com.mangala.wallet.features.send.presentation.step4.EvmStep4VerifyAndSendScreen
import com.mangala.wallet.features.send.presentation.step4.EvmStep4VerifyAndSendScreenModel
import com.mangala.wallet.features.send_base.di.component6
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val sendModule = module {
    factory { (signedTransactionResponse: SignedTransactionResponse) ->
        SendSignedTransactionScreenModel(
            signedTransactionResponse,
            get()
        )
    }
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
            sendSignedTransactionUseCase = get()
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

    register<SharedScreen.SendSignedTransactionScreen> { provider ->
        with(provider) {
            SendSignedTransactionScreen(
                walletId = walletId,
                accountId = accountId,
                nonce = nonce,
                fromAddress = fromAddress,
                blockchainUid = blockchainUid,
                toAddress = toAddress,
                value = value,
                input = input,
                legacyGasPrice = legacyGasPrice,
                maxFeePerGas = maxFeePerGas,
                maxPriorityFeePerGas = maxPriorityFeePerGas,
                baseFee = baseFee,
                gasLimit = gasLimit,
                gasFiatValue = gasFiatValue,
                transactionType = transactionType,
                contactName = contactName,
                contactAddress = contactAddress,
                v = v,
                r = r,
                s = s
            )
        }

    }
}