package com.mangala.wallet.features.chains.antelope_base.domain.repository.account.payment

import com.mangala.wallet.features.chains.antelope_base.domain.model.account.payment.AntelopeAccountPayment

interface AccountPaymentRepository {

    fun saveAccountPayment(accountPayment: AntelopeAccountPayment)
    fun getAccountPayment(purchaseToken: String): AntelopeAccountPayment?
}