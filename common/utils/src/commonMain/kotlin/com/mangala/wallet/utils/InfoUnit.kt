package com.mangala.wallet.utils

enum class InfoUnit(val symbol: String, val bytes: Long) {
    BYTE("B", 1L),
    KILOBYTE("KB", 1024L),
    MEGABYTE("MB", 1024L * 1024),
    GIGABYTE("GB", 1024L * 1024 * 1024);

    companion object {
        fun findSuitableUnit(bytes: Long): InfoUnit {
            return when {
                bytes < KILOBYTE.bytes -> BYTE
                bytes < MEGABYTE.bytes -> KILOBYTE
                bytes < GIGABYTE.bytes -> MEGABYTE
                else -> GIGABYTE
            }
        }
    }
}