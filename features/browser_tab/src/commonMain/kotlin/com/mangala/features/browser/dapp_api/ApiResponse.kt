package com.mangala.features.browser.dapp_api

data class ApiResponse<T>(
    val status: Int,
    val timestamp: String,
    val data: T
)