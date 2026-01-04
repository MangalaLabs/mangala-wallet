package com.mangala.wallet.utils.ext

fun <K, V> MutableMap<K, List<V>>.putOrAppend(key: K, value: V) {
    val existingValue = get(key)

    if (existingValue == null) {
        put(key, listOf(value))
    } else {
        put(key, existingValue + value)
    }
}