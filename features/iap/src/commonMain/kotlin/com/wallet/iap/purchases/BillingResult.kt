package com.wallet.iap.purchases

class BillingResult(val responseCode: ResponseCode) {
    enum class ResponseCode {
        FEATURE_NOT_SUPPORTED,
        SERVICE_DISCONNECTED,
        OK,
        USER_CANCELED,
        SERVICE_UNAVAILABLE,
        BILLING_UNAVAILABLE,
        ITEM_UNAVAILABLE,
        DEVELOPER_ERROR,
        ERROR,
        ITEM_ALREADY_OWNED,
        ITEM_NOT_OWNED,
        NETWORK_ERROR
    }
}