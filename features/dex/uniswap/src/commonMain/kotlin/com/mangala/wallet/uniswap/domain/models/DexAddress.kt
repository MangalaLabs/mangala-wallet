package com.mangala.wallet.uniswap.domain.models

data class DexAddress(
    val routerAddress: String,
    val factoryAddress: String,
    val initCodeHash: String
)