package com.mangala.wallet.model.category_dapp.remote

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDApp (
    val category: Category,
    val dapps: List<DAppRemote>
)