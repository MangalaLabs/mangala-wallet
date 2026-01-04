package com.mangala.wallet.model.category_dapp.remote

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val uuid: String,
    val name: String,
    val description: String
)
