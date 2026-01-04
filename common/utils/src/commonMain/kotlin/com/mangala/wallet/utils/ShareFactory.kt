package com.mangala.wallet.utils

interface IShare{
    fun shareText(title: String, text: String)
    fun shareImage(image: Any)
}

expect class ShareFactory: IShare {
    override fun shareText(title: String, text: String)
    override fun shareImage(image: Any)
}