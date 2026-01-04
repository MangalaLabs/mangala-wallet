package com.mangala.wallet.model.category_dapp.remote

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T> (
    val status: Int, val timestamp: String, val data: T
)