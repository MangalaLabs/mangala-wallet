package com.mangala.wallet.features.send.presentation.step4.evm

import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EstimateGasUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetNonceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendTokenUseCase
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.features.send_base.step4.evm.BaseEvmStep4VerifyAndSendScreenModel

class EvmStep4VerifyAndSendScreenModel(
    contactId: Long?,
    blockchainUid: String,
    tokenId: Long,
    recipientAddress: String,
    amount: String,
    accountId: String,
    getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getAccountByIdUseCase: GetAccountByIdUseCase,
    fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    getSelectedCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getTransactionFeeOptionsUseCase: GetTransactionFeeOptionsUseCase,
    getContactUseCase: GetContactByIdUseCase,
    getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase: GetRecommendedGasPriceUseCase,
    sendTokenUseCase: SendTokenUseCase,
    estimateGasUseCase: EstimateGasUseCase,
    getNativeCoinUseCase: GetNativeCoinUseCase,
    getLatestBlockUseCase: GetLatestBlockUseCase,
    getSelectedWalletUseCase: GetSelectedWalletUseCase,
    getNonceUseCase: GetNonceUseCase
) : BaseEvmStep4VerifyAndSendScreenModel(
    contactId,
    blockchainUid,
    tokenId,
    recipientAddress,
    amount,
    accountId,
    getCurrentCurrencyCodeUseCase,
    getAccountByIdUseCase,
    fetchTokenPriceUseCase,
    getSelectedCurrencyCodeUseCase,
    getTransactionFeeOptionsUseCase,
    getContactUseCase,
    getTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase,
    sendTokenUseCase,
    estimateGasUseCase,
    getNativeCoinUseCase,
    getLatestBlockUseCase,
    getSelectedWalletUseCase,
    getNonceUseCase
)