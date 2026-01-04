package com.mangala.wallet.features.send.presentation.step4.antelope

import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.send.AntelopeSendCryptoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.token.GetAntelopeAccountCryptoBalanceUseCase
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.features.send_base.step4.antelope.BaseAntelopeStep4VerifyAndSendScreenModel
import com.mangala.wallet.features.send_base.step4.antelope.BaseAntelopeStep4VerifyAndSendScreenUiState

class AntelopeStep4VerifyAndSendScreenModel(
    contactId: Long?,
    senderAccount: String,
    toAccount: String,
    blockchainUid: String,
    tokenKey: String,
    amount: String,
    memo: String,
    transactUseCase: AntelopeSendCryptoUseCase,
    getContactByIdUseCase: GetContactByIdUseCase,
    getAntelopeAccountTokenBalanceUseCase: GetAntelopeAccountCryptoBalanceUseCase,
    getSelectedCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getNativeCoinUseCase: GetNativeCoinUseCase,
    fetchTokenPriceUseCase: FetchTokenPriceUseCase
) : BaseAntelopeStep4VerifyAndSendScreenModel(
    contactId,
    senderAccount,
    toAccount,
    blockchainUid,
    tokenKey,
    amount,
    memo,
    getContactByIdUseCase,
    getAntelopeAccountTokenBalanceUseCase,
    getSelectedCurrencyCodeUseCase,
    getNativeCoinUseCase,
    fetchTokenPriceUseCase,
    transactUseCase
) {

    fun getTxHash(): String? {
        return (_uiState.value as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data)?.txHash
    }
}