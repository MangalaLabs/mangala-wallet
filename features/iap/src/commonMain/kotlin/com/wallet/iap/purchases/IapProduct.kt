package com.wallet.iap.purchases

expect interface IapProduct {
    val formattedPrice: String?
    val productId: String
}