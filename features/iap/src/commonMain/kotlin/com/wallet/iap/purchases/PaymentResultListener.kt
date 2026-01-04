package com.wallet.iap.purchases

public interface PaymentResultListener {
    public fun onPaymentResult(state: PaymentState?, message: String?, paymentInfo: PaymentInfo?)
}