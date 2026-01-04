package com.wallet.iap.purchases

actual interface IapProduct {
    actual val formattedPrice: String?
    actual val productId: String
}