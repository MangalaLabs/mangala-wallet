package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.payment

import com.mangala.wallet.features.chains.antelope_base.domain.model.account.payment.AntelopeAccountPayment
import com.mangala.wallet.features.chains.antelope_base.domain.repository.account.payment.AccountPaymentRepository

class GetAccountPaymentUseCase(private val accountPaymentRepository: AccountPaymentRepository) {

    operator fun invoke(purchaseToken: String): AntelopeAccountPayment? {
        return accountPaymentRepository.getAccountPayment(purchaseToken)
    }
}