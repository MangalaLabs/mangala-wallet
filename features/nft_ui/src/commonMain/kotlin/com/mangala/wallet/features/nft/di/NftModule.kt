package com.mangala.wallet.features.nft.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.nft.presentation.send.confirmation.SendNftConfirmationScreen
import com.mangala.wallet.features.nft.presentation.send.confirmation.SendNftConfirmationScreenModel
import com.mangala.wallet.features.nft_base.di.component6
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val nftModule = module {
    factory { (blockchainUid: String, accountId: String, toAddress: String, collectionContractAddress: String, tokenId: String, contactId: Long?) ->
        SendNftConfirmationScreenModel(
            blockchainUid = blockchainUid,
            accountId = accountId,
            toAddress = toAddress,
            collectionContractAddress = collectionContractAddress,
            tokenId = tokenId,
            contactId = contactId,
            sendNftUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getAccountByIdUseCase = get(),
            getContactByIdUseCase = get(),
            getRecommendedGasPriceUseCase = get(),
            getTransactionFeeOptionsUseCase = get(),
            fetchTokenPriceUseCase = get(),
            getNativeCoinUseCase = get(),
            getTokenBalanceByTokenIdUseCase = get(),
            getCurrentCurrencyCodeUseCase = get(),
            getNftByTokenIdUseCase = get(),
            getLatestBlockUseCase = get(),
            getNonceUseCase = get(),
            getSelectedWalletUseCase = get(),
            sendSignedTransactionUseCase = get()
        )
    }
}

val nftScreenModule = screenModule {
    register<SharedScreen.SendNftConfirmationScreen> {
        SendNftConfirmationScreen(
            blockchainUid = it.blockchainUid,
            contactId = it.contactId,
            accountId = it.accountId,
            recipientAddress = it.recipientAddress,
            collectionContractAddress = it.collectionContractAddress,
            tokenId = it.tokenId
        )
    }
}