package com.mangala.wallet.biometry

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform