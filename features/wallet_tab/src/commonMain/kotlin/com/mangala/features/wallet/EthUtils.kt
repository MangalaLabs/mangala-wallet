package com.mangala.features.wallet
import kotlin.math.pow
import kotlin.math.round

const val WEI_IN_ETH = 1_000_000_000_000_000_000UL // 1 ETH = 10^18 wei

fun extractBalanceFromHexString(hexBalance: String): ULong {
    return hexBalance.removePrefix("0x").toULong(16)
}

fun convertWeiToEth(wei: ULong): Double {
    return wei.toDouble() / WEI_IN_ETH.toDouble()
}

//fun formatEthBalance(eth: Double): String {
//    val roundedEth = round(eth * 1_000_000) / 1_000_000
//    return "%.6f".format(roundedEth) // Round to 6 decimal places
//}

fun formatEthBalance(eth: Double): String {
    val roundedEth = round(eth * 1_000_000) / 1_000_000
    return customFormat(roundedEth, 6)
}

fun customFormat(value: Double, decimalPlaces: Int): String {
    val integerPart = value.toLong()
    val decimalPart = ((value - integerPart) * 10.0.pow(decimalPlaces)).toLong()
    return "$integerPart.${decimalPart.toString().padStart(decimalPlaces, '0')}"
}