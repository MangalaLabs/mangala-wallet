package com.mangala.wallet.features.nft.presentation.send.confirmation

import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetNonceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.GetNftByTokenIdUseCase
import com.mangala.wallet.features.nft_base.domain.usecases.SendNftUseCase
import com.mangala.wallet.features.nft_base.presentation.send.confirmation.BaseSendNftConfirmationScreenModel

class SendNftConfirmationScreenModel(
    blockchainUid: String,
    accountId: String,
    toAddress: String, // use this only to calculate recipientAddress
    collectionContractAddress: String,
    tokenId: String,
    contactId: Long?,
    sendNftUseCase: SendNftUseCase,
    getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    getAccountByIdUseCase: GetAccountByIdUseCase,
    getNftByTokenIdUseCase: GetNftByTokenIdUseCase,
    getContactByIdUseCase: GetContactByIdUseCase,
    getRecommendedGasPriceUseCase: GetRecommendedGasPriceUseCase,
    getTransactionFeeOptionsUseCase: GetTransactionFeeOptionsUseCase,
    fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    getNativeCoinUseCase: GetNativeCoinUseCase,
    getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getLatestBlockUseCase: GetLatestBlockUseCase,
    getNonceUseCase: GetNonceUseCase
): BaseSendNftConfirmationScreenModel(
    blockchainUid, accountId, toAddress, collectionContractAddress, tokenId, contactId, sendNftUseCase, getSelectedNetworkUseCase, getAccountByIdUseCase, getNftByTokenIdUseCase, getContactByIdUseCase, getRecommendedGasPriceUseCase, getTransactionFeeOptionsUseCase, fetchTokenPriceUseCase, getNativeCoinUseCase, getTokenBalanceByTokenIdUseCase, getCurrentCurrencyCodeUseCase, getLatestBlockUseCase, getNonceUseCase
) {

}