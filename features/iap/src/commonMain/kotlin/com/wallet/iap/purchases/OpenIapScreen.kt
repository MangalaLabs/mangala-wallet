package com.wallet.iap.purchases

public expect class OpenIapScreen {

    public fun openIapScreen(
        paymentResultListener: PaymentResultListener,
        accountName: String,
        blockchainUid: String
    )

}