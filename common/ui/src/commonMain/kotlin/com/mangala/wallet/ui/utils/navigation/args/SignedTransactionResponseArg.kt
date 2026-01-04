package com.mangala.wallet.ui.utils.navigation.args

data class SignedTransactionResponseArg(
    val signTransactionRequestArgs: SignTransactionRequestArgs,
    val v: Int,
    val r: ByteArray,
    val s: ByteArray
)