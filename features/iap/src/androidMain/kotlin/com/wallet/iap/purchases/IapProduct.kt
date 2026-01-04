package com.wallet.iap.purchases

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.SkuDetails

actual interface IapProduct {
    val name: String
    actual val formattedPrice: String?
    actual val productId: String

    data class Product(val productDetails: ProductDetails) : IapProduct {
        override val name: String = productDetails.name
        override val formattedPrice: String? =
            productDetails.oneTimePurchaseOfferDetails?.formattedPrice
        override val productId: String = productDetails.productId
    }

    data class Sku(val skuDetails: SkuDetails) : IapProduct {
        override val name: String
            get() {
                val lastParenthesisIndex =
                    skuDetails.title.replace("(unreviewed)", "").lastIndexOf('(')
                return skuDetails.title.substring(0, lastParenthesisIndex).trim()
            }
        override val formattedPrice: String? = skuDetails.price
        override val productId: String = skuDetails.sku
    }
}