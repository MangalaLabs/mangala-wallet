package com.mangala.wallet.utils

fun onClickIfNotLoading(isLoading: Boolean, action: () -> Unit) {
    if (!isLoading) action()
}