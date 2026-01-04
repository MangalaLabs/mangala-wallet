package com.mangala.wallet.utils

interface IToast{
    fun show(text: String)
}

expect class ToastFactory: IToast {
    override fun show(text: String)
}