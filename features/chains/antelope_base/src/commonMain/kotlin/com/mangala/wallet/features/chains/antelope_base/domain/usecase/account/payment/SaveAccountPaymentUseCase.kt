package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.payment

import com.mangala.wallet.features.chains.antelope_base.domain.model.account.payment.AntelopeAccountPayment
import com.mangala.wallet.features.chains.antelope_base.domain.repository.account.payment.AccountPaymentRepository

class SaveAccountPaymentUseCase(private val accountPaymentRepository: AccountPaymentRepository) {

    operator fun invoke(accountPayment: AntelopeAccountPayment) {
        accountPaymentRepository.saveAccountPayment(accountPayment)
    }
}