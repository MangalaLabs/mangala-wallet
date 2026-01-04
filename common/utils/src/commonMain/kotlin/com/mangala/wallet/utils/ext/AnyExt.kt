package com.mangala.wallet.utils.ext

fun Any.alsoLogValue(tag: String = "") = also { println("$tag: $it") }